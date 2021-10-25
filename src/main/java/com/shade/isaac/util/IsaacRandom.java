package com.shade.isaac.util;

import java.util.Arrays;

public class IsaacRandom {
    private final int[] results = new int[256];
    private final int[] state = new int[256];
    private int index;
    private int a;
    private int b;
    private int c;

    public IsaacRandom(int[] seed) {
        init(seed);
    }

    private void init(int[] seed) {
        a = 0;
        b = 0;
        c = 0;
        index = 1024;

        final int[] initState = new int[8];
        Arrays.fill(initState, 0x9e3779b9);

        for (int i = 0; i < 4; i++) {
            mix(initState);
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 256; j += 8) {
                for (int k = 0; k < 8; k++) {
                    initState[k] += (i == 0) ? seed[j + k] : state[j + k];
                }
                mix(initState);
                System.arraycopy(initState, 0, state, j, 8);
            }
        }
    }

    private void advance() {
        c++;
        b += c;
        index = 0;

        for (int i = 0; i < 256; i++) {
            switch (i & 3) {
                case 0 -> a ^= a << 13;
                case 1 -> a ^= a >>> 6;
                case 2 -> a ^= a << 2;
                case 3 -> a ^= a >>> 16;
            }
            a += state[(i + 0x80) & 0xff];
            final int x = state[i];
            final int y = state[i] = state[(x >>> 2) & 0xFF] + a + b;
            results[i] = b = state[(y >>> 10) & 0xFF] + x;
        }
    }

    private static void mix(int[] a) {
        a[0] ^= a[1] << 11;
        a[3] += a[0];
        a[1] += a[2];
        a[1] ^= a[2] >>> 2;
        a[4] += a[1];
        a[2] += a[3];
        a[2] ^= a[3] << 8;
        a[5] += a[2];
        a[3] += a[4];
        a[3] ^= a[4] >>> 16;
        a[6] += a[3];
        a[4] += a[5];
        a[4] ^= a[5] << 10;
        a[7] += a[4];
        a[5] += a[6];
        a[5] ^= a[6] >>> 4;
        a[0] += a[5];
        a[6] += a[7];
        a[6] ^= a[7] << 8;
        a[1] += a[6];
        a[7] += a[0];
        a[7] ^= a[0] >>> 9;
        a[2] += a[7];
        a[0] += a[1];
    }

    private byte nextByte() {
        if (index == 1024) {
            advance();
        }
        final byte value = (byte) (results[index >>> 2] >>> (8 * (index & 3)) & 0xff);
        index++;
        return value;
    }

    private int nextInt() {
        if (index == 1024) {
            advance();
        }
        final int value = results[index >>> 2];
        index += 4;
        return value;
    }

    public byte[] process(byte[] input) {
        final int length = input.length;
        final int alignedLength = IOUtils.alignDown(length, 4);
        final byte[] result = new byte[length];

        for (int i = 0; i < alignedLength; i += 4) {
            final int value = nextInt();
            result[i] = (byte) (value & 0xff ^ input[i]);
            result[i + 1] = (byte) (value >>> 8 & 0xff ^ input[i + 1]);
            result[i + 2] = (byte) (value >>> 16 & 0xff ^ input[i + 2]);
            result[i + 3] = (byte) (value >>> 24 & 0xff ^ input[i + 3]);
        }

        for (int i = alignedLength; i < length; i++) {
            result[i] = (byte) (nextByte() ^ input[i]);
        }

        return result;
    }
}