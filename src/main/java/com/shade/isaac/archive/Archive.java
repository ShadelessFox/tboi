package com.shade.isaac.archive;

import com.shade.isaac.archive.record.*;
import com.shade.isaac.util.HashUtils;
import com.shade.isaac.util.IOUtils;

import java.nio.ByteBuffer;

public class Archive {
    private final AbstractRecord[] records;

    public Archive(ByteBuffer buffer) {
        final byte[] magic = IOUtils.getBytes(buffer, 7);
        final Format format = Format.valueOf(buffer.get());
        final int offset = buffer.getInt();
        final short count = buffer.getShort();

        if (!"ARCH000".equals(new String(magic))) {
            throw new IllegalArgumentException("Invalid archive file magic");
        }

        this.records = IOUtils.getObjects(buffer.position(offset), count, AbstractRecord[]::new, switch (format) {
            case SCRAMBLED -> ScrambledRecord::new;
            case DEFLATE -> DeflateRecord::new;
            case LZW -> LZWRecord::new;
            case XOR -> XorRecord::new;
        });
    }

    public AbstractRecord[] getRecords() {
        return records;
    }

    public AbstractRecord getRecord(int hashLow, int hashHigh) {
        for (AbstractRecord entry : records) {
            if (entry.getHashLow() == hashLow && entry.getHashHigh() == hashHigh) {
                return entry;
            }
        }
        return null;
    }

    public AbstractRecord getRecord(String name) {
        return getRecord(HashUtils.fnv1(name), HashUtils.djb2(name));
    }

    public enum Format {
        SCRAMBLED,
        DEFLATE,
        LZW,
        XOR;

        public static Format valueOf(byte value) {
            return switch (value) {
                case 1 -> LZW;
                case 2 -> DEFLATE;
                case 5 -> XOR;
                default -> SCRAMBLED;
            };
        }
    }
}
