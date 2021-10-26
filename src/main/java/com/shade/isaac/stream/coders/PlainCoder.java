package com.shade.isaac.stream.coders;

import com.shade.isaac.stream.ArchiveCoder;

import java.nio.channels.ReadableByteChannel;

public class PlainCoder {
    public static final ArchiveCoder.Encoder ENCODER = (src, dst) -> dst.write(src);

    public static final ArchiveCoder.Decoder DECODER = ReadableByteChannel::read;

    private PlainCoder() {
    }
}
