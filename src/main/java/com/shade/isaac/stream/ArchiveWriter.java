package com.shade.isaac.stream;

import com.shade.isaac.util.HashUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ArchiveWriter extends ArchiveChannel {
    private final ArchiveCoder coder;
    private final List<Entry> entries = new ArrayList<>();
    private Entry current;

    public ArchiveWriter(File file, ArchiveCoder coder) throws IOException {
        this(file.toPath(), coder);
    }

    public ArchiveWriter(Path path, ArchiveCoder coder) throws IOException {
        this(Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), coder);
    }

    public ArchiveWriter(SeekableByteChannel channel, ArchiveCoder coder) throws IOException {
        super(channel);
        this.coder = coder;
        channel.position(HEADER_SIZE);
    }

    public void beginEntry(String name) throws IOException {
        if (current != null) {
            throw new IOException("Previous entry is not ended");
        }
        current = new Entry((long) HashUtils.fnv1(name) << 32 | HashUtils.djb2(name) & 0xffffffffL, (int) channel.position(), 0, 0);
    }

    public void endEntry() throws IOException {
        if (current == null) {
            throw new IOException("No entry to end");
        }
        entries.add(new Entry(current.hash, current.off, (int) (channel.position() - current.off), (int) crc.getValue()));
        current = null;
        crc.reset();
    }

    public void write(ByteBuffer src) throws IOException {
        if (current == null) {
            throw new IOException("No current entry");
        }
        final ArchiveCoder.Encoder encoder = coder.getEncoder();
        if (encoder == null) {
            throw new IOException("Coder does not support encoding: " + coder);
        }
        crc.update(src.slice());
        encoder.process(src, channel);
    }

    public ArchiveCoder getCoder() {
        return coder;
    }

    @Override
    protected void finish() throws IOException {
        if (current != null) {
            throw new IOException("Last entry is not ended");
        }
        super.finish();
        writeHeader();
        writeEntries();
    }

    private void writeHeader() throws IOException {
        final ByteBuffer buffer = ByteBuffer
            .allocate(HEADER_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN);
        final long position = channel.position();

        buffer.put(SIGNATURE);
        buffer.put(coder.getId());
        buffer.putInt((int) position);
        buffer.putShort((short) entries.size());

        channel.position(0);
        channel.write(buffer.position(0));
        channel.position(position);
    }

    private void writeEntries() throws IOException {
        final ByteBuffer buffer = ByteBuffer
            .allocate(ENTRY_SIZE * entries.size())
            .order(ByteOrder.LITTLE_ENDIAN);

        for (Entry entry : entries) {
            buffer.putLong(entry.hash);
            buffer.putInt(entry.off);
            buffer.putInt(entry.len);
            buffer.putInt(entry.crc);
        }

        channel.write(buffer.position(0));
    }

    private record Entry(long hash, int off, int len, int crc) {
    }
}
