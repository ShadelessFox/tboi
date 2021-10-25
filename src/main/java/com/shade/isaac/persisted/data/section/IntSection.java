package com.shade.isaac.persisted.data.section;

import com.shade.isaac.util.IOUtils;

import java.nio.ByteBuffer;

public class IntSection extends Section {
    private final int[] values;

    public IntSection(ByteBuffer buffer) {
        super(buffer);
        this.values = IOUtils.getInts(buffer, getCount());
    }

    @Override
    public void write(ByteBuffer buffer) {
        super.write(buffer);
        IOUtils.put(buffer, values);
    }

    public int[] get() {
        return values;
    }

    public int get(int index) {
        return values[index];
    }

    public void set(int index, int value) {
        values[index] = value;
    }
}
