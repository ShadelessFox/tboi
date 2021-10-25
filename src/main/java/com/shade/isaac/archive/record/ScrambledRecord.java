package com.shade.isaac.archive.record;

import com.shade.isaac.util.ByteChunk;
import com.shade.isaac.util.IOUtils;

import java.nio.ByteBuffer;

public class ScrambledRecord extends AbstractRecord {
    public ScrambledRecord(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    protected void decode(ByteBuffer src, ByteBuffer dst) {
        int key = getHashLow() ^ 0xf9524287 | 1;

        for (ByteChunk chunk : IOUtils.getBytesChunked(src, IOUtils.alignUp(dst.limit(), 4), 1024)) {
            final byte[] data = chunk.data();
            for (int i = 0; i < data.length; i += 4) {
                data[i] ^= key & 0xff;
                data[i + 1] ^= key >>> 8 & 0xff;
                data[i + 2] ^= key >>> 16 & 0xff;
                data[i + 3] ^= key >>> 24 & 0xff;

                final byte data0 = data[i];
                final byte data1 = data[i + 1];
                final byte data2 = data[i + 2];
                final byte data3 = data[i + 3];

                switch (key & 15) {
                    case 2:  // [1 2 3 4] -> [4 3 2 1]
                        data[i] = data3;
                        data[i + 1] = data2;
                        data[i + 2] = data1;
                        data[i + 3] = data0;
                        break;
                    case 9:  // [1 2 3 4] -> [2 1 4 3]
                        data[i] = data1;
                        data[i + 1] = data0;
                        data[i + 2] = data3;
                        data[i + 3] = data2;
                        break;
                    case 13: // [1 2 3 4] -> [3 4 1 2]
                        data[i] = data2;
                        data[i + 1] = data3;
                        data[i + 2] = data0;
                        data[i + 3] = data1;
                        break;
                }

                key ^= key << 8;
                key ^= key >>> 9;
                key ^= key << 23;
            }

            dst.put(data, 0, Math.min(chunk.length(), dst.remaining()));
        }
    }
}
