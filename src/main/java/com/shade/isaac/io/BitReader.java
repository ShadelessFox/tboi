package com.shade.isaac.io;

import java.nio.ByteBuffer;

public class BitReader {
    private final ByteBuffer is;
    private long buffer;
    private int bitsInBuffer;
    private int bitsRead;

    public BitReader(ByteBuffer is) {
        this.is = is;
    }

    public int readBits(int count) {
        if (count > 32) {
            throw new IllegalArgumentException("Cannot read more than 32 bits at a time");
        }

        while (bitsInBuffer < count) {
            buffer <<= 8;
            buffer |= is.get() & 0xff;
            bitsRead += 8;
            bitsInBuffer += 8;
        }

        final int result = (int) ((buffer >>> (bitsInBuffer - count)) & ((1 << count) - 1));

        bitsInBuffer -= count;
        buffer &= (1L << bitsInBuffer) - 1;

        return result;
    }

    public int getBitsRead() {
        return bitsRead - bitsInBuffer;
    }

    public int getBitsLength() {
        return is.limit() * 8;
    }
}
