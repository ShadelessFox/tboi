package com.shade.isaac.archive.record;

import com.shade.isaac.util.IsaacRandom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateRecord extends AbstractRecord {
    public DeflateRecord(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    protected void decode(ByteBuffer src, ByteBuffer dst) {
        src.order(ByteOrder.LITTLE_ENDIAN);

        final IsaacRandom random = new IsaacRandom(makeKey());
        boolean scrambled = false;
        boolean compressed;

        while (dst.remaining() > 0) {
            int length = src.getInt();
            compressed = length >>> 31 == 1;
            scrambled = scrambled || (!compressed && length == 1024);
            length &= 0x7fffffff;

            if (scrambled) {
                unscramble(src, dst, length, random);
            } else {
                inflate(src, dst, length);
            }
        }
    }

    private void inflate(ByteBuffer src, ByteBuffer dst, int length) {
        final byte[] block = new byte[length + 2];
        block[0] = (byte) 0x78;
        block[1] = (byte) 0x01;
        src.get(block, 2, length);

        try (InflaterInputStream is = new InflaterInputStream(new ByteArrayInputStream(block), new Inflater(), 1024)) {
            dst.put(is.readNBytes(Math.min(1024, dst.remaining())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void unscramble(ByteBuffer src, ByteBuffer dst, int length, IsaacRandom random) {
        final byte[] chunk = new byte[length];
        src.get(chunk, 0, Math.min(length, src.remaining()));
        dst.put(random.process(chunk));
    }

    private int[] makeKey() {
        return makeKey(getHashLow() & 0xffffffffL);
    }

    private static int[] makeKey(long seed) {
        final int[] key = new int[256];

        seed = (seed << 32) | ((seed << 15 ^ seed) << 8 ^ seed >>> 9 ^ seed) & 0xffffffffL;

        for (int i = 0; i < key.length; i++) {
            key[i] = Integer.rotateRight((int) ((seed ^ seed >>> 18) >>> 27), (int) (seed >>> 59));
            seed = (0x5851F42D4C957F2DL * seed) + 127;
        }

        return key;
    }
}
