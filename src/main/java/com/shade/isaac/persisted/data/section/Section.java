package com.shade.isaac.persisted.data.section;

import com.shade.isaac.util.Structure;

import java.nio.ByteBuffer;

public abstract class Section implements Structure {
    private final int id;
    private final int size;
    private final int count;

    public Section(ByteBuffer buffer) {
        this.id = buffer.getInt();
        this.size = buffer.getInt();
        this.count = buffer.getInt();
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.putInt(id);
        buffer.putInt(size);
        buffer.putInt(count);
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }
}
