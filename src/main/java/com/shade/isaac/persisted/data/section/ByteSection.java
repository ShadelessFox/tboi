package com.shade.isaac.persisted.data.section;

import com.shade.isaac.util.IOUtils;

import java.nio.ByteBuffer;

public class ByteSection extends Section {
    private final byte[] values;

    public ByteSection(ByteBuffer buffer) {
        super(buffer);
        this.values = IOUtils.getBytes(buffer, getCount());
    }

    @Override
    public void write(ByteBuffer buffer) {
        super.write(buffer);
        buffer.put(values);
    }

    public byte[] get() {
        return values;
    }

    public byte get(int index) {
        return values[index];
    }

    public void set(int index, byte value) {
        values[index] = value;
    }
}
