package com.shade.isaac.archive.record;

import com.shade.isaac.util.ByteChunk;
import com.shade.isaac.util.IOUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class AbstractRecord {
    private final int hashHigh;
    private final int hashLow;
    private final int offset;
    private final int length;
    private final int checksum;
    private final ByteBuffer data;

    public AbstractRecord(ByteBuffer buffer) {
        this.hashHigh = buffer.getInt();
        this.hashLow = buffer.getInt();
        this.offset = buffer.getInt();
        this.length = buffer.getInt();
        this.checksum = buffer.getInt();
        this.data = ByteBuffer.allocate(length);

        decode(buffer.slice(offset, buffer.limit() - offset).order(ByteOrder.LITTLE_ENDIAN), data);
        validate();
    }

    protected abstract void decode(ByteBuffer src, ByteBuffer dst);

    public int getHashHigh() {
        return hashHigh;
    }

    public int getHashLow() {
        return hashLow;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getChecksum() {
        return checksum;
    }

    public ByteBuffer getData() {
        return data;
    }

    private void validate() {
        final int actual = checksum(data.position(0));
        if (checksum != actual) {
            throw new IllegalArgumentException(String.format("Invalid checksum: 0x%08x, expected 0x%08x%n", checksum, actual));
        }
    }

    private static int checksum(ByteBuffer buffer) {
        int hash = 0xABABEB98;

        for (ByteChunk chunk : IOUtils.getBytesChunked(buffer, buffer.limit(), 512)) {
            for (int i = 0; i < chunk.length(); i += 4) {
                hash = Integer.rotateRight(hash, 1)
                    + (chunk.data()[i] & 0xff
                    | (chunk.data()[i + 1] & 0xff) << 8
                    | (chunk.data()[i + 2] & 0xff) << 16
                    | (chunk.data()[i + 3] & 0xff) << 24);
            }
        }

        return hash;
    }
}
