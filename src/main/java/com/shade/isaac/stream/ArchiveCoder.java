package com.shade.isaac.stream;

import com.shade.isaac.stream.coders.PlainCoder;
import com.shade.isaac.stream.coders.XorCoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public enum ArchiveCoder {
    PLAIN(PlainCoder.ENCODER, PlainCoder.DECODER, -1),
    XOR(XorCoder.ENCODER, XorCoder.DECODER, 5);

    private final Encoder encoder;
    private final Decoder decoder;
    private final byte id;

    ArchiveCoder(Encoder encoder, Decoder decoder, int id) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.id = (byte) (id & 0xff);
    }

    public Encoder getEncoder() {
        return encoder;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public byte getId() {
        return id;
    }

    public interface Encoder {
        void process(ByteBuffer src, ByteChannel dst) throws IOException;
    }

    public interface Decoder {
        void process(ByteChannel src, ByteBuffer dst) throws IOException;
    }
}
