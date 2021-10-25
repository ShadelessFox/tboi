package com.shade.isaac.persisted.data.section;

import com.shade.isaac.util.Structure;
import com.shade.isaac.util.IOUtils;

import java.nio.ByteBuffer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class ObjectSection<T extends Structure> extends Section {
    private final T[] values;

    public ObjectSection(ByteBuffer buffer, IntFunction<T[]> generator, Function<ByteBuffer, T> reader) {
        super(buffer);
        this.values = IOUtils.getObjects(buffer, getCount(), generator, reader);
    }

    public void write(ByteBuffer buffer) {
        super.write(buffer);
        for (T value : values) {
            value.write(buffer);
        }
    }

    public T[] get() {
        return values;
    }

    public T get(int index) {
        return values[index];
    }

    public void set(int index, T value) {
        values[index] = value;
    }
}
