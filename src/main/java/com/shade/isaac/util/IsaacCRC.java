package com.shade.isaac.util;

import java.util.zip.Checksum;

public final class IsaacCRC implements Checksum {
    private static final int[] LOOKUP = new int[256];

    static {
        for (int i = 0; i < LOOKUP.length; i++) {
            int a = i;
            int b = i;

            for (int j = 0; j < 8; j++) {
                final int c = j != 5 ? a : b;
                a = (a >> 1) ^ ((c & 1) != 0 ? 0xEDB88320 : 0);
                b = (b >> 1) ^ ((b & 1) != 0 ? 0x20 : 0);
            }

            LOOKUP[i] = a;
        }
    }

    private int crc = 0x1234589;

    @Override
    public void update(int b) {
        crc = LOOKUP[(crc ^ b) & 0xff] ^ (crc >>> 8);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int i = off; i < off + len; i++) {
            update(b[i]);
        }
    }

    @Override
    public long getValue() {
        return (long) ~crc & 0xffffffffL;
    }

    @Override
    public void reset() {
        crc = 0x1234589;
    }
}
