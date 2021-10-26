package com.shade.isaac.util;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;

public class ByteArrayChannel implements SeekableByteChannel {
    private byte[] buf;
    private int pos;
    private int len;
    private boolean closed;

    public ByteArrayChannel(byte[] buf) {
        this.buf = buf;
        this.pos = 0;
        this.len = buf.length;
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public long position() throws IOException {
        ensureOpen();
        return pos;
    }

    @Override
    public SeekableByteChannel position(long pos) throws IOException {
        ensureOpen();
        if (pos < 0 || pos >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Illegal position " + pos);
        }
        this.pos = Math.min((int) pos, len);
        return this;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen();
        if (pos == len) {
            return -1;
        }
        int n = Math.min(dst.remaining(), len - pos);
        dst.put(buf, pos, n);
        pos += n;
        return n;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        ensureOpen();
        throw new UnsupportedOperationException();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        ensureOpen();
        final int n = src.remaining();
        ensureCapacity(pos + n);
        src.get(buf, pos, n);
        pos += n;
        if (pos > len) {
            len = pos;
        }
        return n;
    }

    @Override
    public long size() throws IOException {
        ensureOpen();
        return len;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        buf = null;
        pos = 0;
        len = 0;
    }


    private void ensureOpen() throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity - buf.length > 0) {
            throw new BufferOverflowException();
        }
    }
}
