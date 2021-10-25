package com.shade.isaac.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.IntFunction;

public class IOUtils {
    private IOUtils() {
    }

    public static int alignUp(int value, int alignment) {
        return (value + alignment - 1) / alignment * alignment;
    }

    public static int alignDown(int value, int alignment) {
        return value - value % alignment;
    }

    public static Iterable<ByteChunk> getBytesChunked(ByteBuffer src, int limit, int chunkSize) {
        return () -> new Iterator<>() {
            private final byte[] chunk = new byte[chunkSize];
            private final ByteBuffer buffer = src.slice(src.position(), limit);

            @Override
            public boolean hasNext() {
                return buffer.remaining() > 0;
            }

            @Override
            public ByteChunk next() {
                final int length = Math.min(chunkSize, buffer.remaining());
                buffer.get(chunk, 0, length);
                return new ByteChunk(chunk, length);
            }
        };
    }

    public static byte[] getBytes(ByteBuffer buffer, int length) {
        final byte[] output = new byte[length];
        buffer.get(output);
        return output;
    }

    public static int[] getInts(ByteBuffer buffer, int length) {
        final int[] output = new int[length];
        buffer.asIntBuffer().get(output);
        buffer.position(buffer.position() + length * Integer.BYTES);
        return output;
    }

    public static <T> T[] getObjects(ByteBuffer buffer, int count, IntFunction<T[]> generator, Function<ByteBuffer, T> reader) {
        final T[] output = generator.apply(count);
        for (int i = 0; i < output.length; i++) {
            output[i] = reader.apply(buffer);
        }
        return output;
    }

    public static void put(ByteBuffer buffer, int... values) {
        buffer.asIntBuffer().put(values);
        buffer.position(buffer.position() + values.length * Integer.BYTES);
    }
}
