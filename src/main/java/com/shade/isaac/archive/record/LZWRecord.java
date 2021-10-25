package com.shade.isaac.archive.record;

import com.shade.isaac.io.LZWDecoder;

import java.nio.ByteBuffer;

public class LZWRecord extends AbstractRecord {
    public LZWRecord(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    protected void decode(ByteBuffer src, ByteBuffer dst) {
        final LZWDecoder decoder = new LZWDecoder();
        while (dst.remaining() > 0) {
            final int length = src.getInt();
            final int offset = src.position();
            dst.put(decoder.decode(src, offset, length));
        }
    }
}
