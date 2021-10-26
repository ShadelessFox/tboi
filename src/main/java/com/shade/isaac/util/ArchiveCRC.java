package com.shade.isaac.util;

import java.util.zip.Checksum;

public class ArchiveCRC implements Checksum {
    private int crc = 0xababeb98;

    @Override
    public void update(int v) {
        crc = Integer.rotateRight(crc, 1) + v;
    }

    @Override
    public void update(byte[] b, int off, int len) {
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException("off = " + off + ", len = " + len + ", buffer = " + b.length);
        }
        final byte[] tmp = new byte[4];
        for (int i = off; i < off + len; i += 4) {
            System.arraycopy(b, i, tmp, 0, Math.min(4, off + len - i));
            update(tmp[0] & 0xff | tmp[1] & 0xff << 8 | tmp[2] & 0xff << 16 | tmp[3] & 0xff << 24);
        }
    }

    @Override
    public long getValue() {
        return crc & 0xffffffffL;
    }

    @Override
    public void reset() {
        crc = 0xABABEB98;
    }
}
