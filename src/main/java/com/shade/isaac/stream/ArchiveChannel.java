package com.shade.isaac.stream;

import com.shade.isaac.util.ArchiveCRC;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

public abstract class ArchiveChannel implements Closeable {
    protected static final byte[] SIGNATURE = {'A', 'R', 'C', 'H', '0', '0', '0'};
    protected static final int HEADER_SIZE = 14;
    protected static final int ENTRY_SIZE = 20;

    protected final SeekableByteChannel channel;
    protected final ArchiveCRC crc;
    protected boolean finished;

    public ArchiveChannel(SeekableByteChannel channel) {
        this.channel = channel;
        this.crc = new ArchiveCRC();
    }

    protected void finish() throws IOException {
        if (finished) {
            throw new IOException("This archive has already been finished");
        }
        finished = true;
    }

    @Override
    public void close() throws IOException {
        if (!finished) {
            finish();
        }
        channel.close();
    }
}
