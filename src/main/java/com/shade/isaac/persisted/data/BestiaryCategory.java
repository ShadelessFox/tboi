package com.shade.isaac.persisted.data;

import com.shade.isaac.util.Structure;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class BestiaryCategory implements Structure {
    private final Type type;
    private final int length;
    private final Map<MonsterId, Integer> counters;

    public BestiaryCategory(ByteBuffer buffer) {
        this.type = Type.values()[buffer.getInt() - 1];
        this.length = buffer.getInt();
        this.counters = new HashMap<>(length / 4);

        for (int i = 0; i < length / 4; i++) {
            counters.put(new MonsterId(buffer), buffer.getInt());
        }
    }

    @Override
    public void write(ByteBuffer buffer) {
        buffer.putInt(type.ordinal() + 1);
        buffer.putInt(length);
        for (Map.Entry<MonsterId, Integer> entry : counters.entrySet()) {
            entry.getKey().write(buffer);
            buffer.putInt(entry.getValue());
        }
    }

    public Type getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public int getCounter(MonsterId monster) {
        return counters.get(monster);
    }

    public Map<MonsterId, Integer> getCounters() {
        return counters;
    }

    public enum Type {
        DEATHS, KILLS, HITS, ENCOUNTERS
    }
}
