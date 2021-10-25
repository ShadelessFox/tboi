package com.shade.isaac.persisted.data;

import com.shade.isaac.util.Structure;

import java.io.Serializable;
import java.nio.ByteBuffer;

public record MonsterId(int type, int variant) implements Structure, Serializable {
    public MonsterId(ByteBuffer buffer) {
        this(buffer.getInt());
    }

    private MonsterId(int value) {
        this(value >>> 20, (value >>> 8) & 0xff);
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.putInt(type << 20 | variant << 8);
    }

    @Override
    public String toString() {
        return type + "." + variant;
    }
}
