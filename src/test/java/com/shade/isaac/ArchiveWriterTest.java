package com.shade.isaac;

import com.shade.isaac.stream.ArchiveCoder;
import com.shade.isaac.stream.ArchiveWriter;
import com.shade.isaac.util.ByteArrayChannel;
import com.shade.isaac.util.HashUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ArchiveWriterTest {
    @Test
    public void shouldBeAbleToWriteEmptyArchive() throws IOException {
        final byte[] actual = new byte[14];

        try (ArchiveWriter writer = new ArchiveWriter(new ByteArrayChannel(actual), ArchiveCoder.PLAIN)) {
            writer.close();

            final ByteBuffer expected = ByteBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{'A', 'R', 'C', 'H', '0', '0', '0'}) /* signature */
                .put(writer.getCoder().getId())                     /* compression */
                .putInt(14)                                         /* descriptor table entries offset */
                .putShort((short) 0);                               /* descriptor table entries count */

            Assertions.assertArrayEquals(expected.array(), actual);
        }
    }

    @Test
    public void shouldBeAbleToWriteArchiveWithSingleEmptyEntry() throws IOException {
        final byte[] actual = new byte[34];

        try (ArchiveWriter writer = new ArchiveWriter(new ByteArrayChannel(actual), ArchiveCoder.PLAIN)) {
            writer.beginEntry("test");
            writer.endEntry();
            writer.close();

            final ByteBuffer expected = ByteBuffer.allocate(34).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{'A', 'R', 'C', 'H', '0', '0', '0'}) /* signature */
                .put(writer.getCoder().getId())                     /* compression */
                .putInt(14)                                         /* descriptor table entries offset */
                .putShort((short) 1)                                /* descriptor table entries count */

                .putInt(HashUtils.djb2("test"))                     /* entry hash low */
                .putInt(HashUtils.fnv1("test"))                     /* entry hash high */
                .putInt(14)                                         /* entry data offset */
                .putInt(0)                                          /* entry data length */
                .putInt(0xababeb98);                                /* entry data crc */

            Assertions.assertArrayEquals(expected.array(), actual);
        }
    }

    @Test
    public void shouldBeAbleToWriteArchiveWithSingleEntryWithData() throws IOException {
        final byte[] actual = new byte[45];

        try (ArchiveWriter writer = new ArchiveWriter(new ByteArrayChannel(actual), ArchiveCoder.PLAIN)) {
            writer.beginEntry("test");
            writer.write(ByteBuffer.wrap("hello world".getBytes()));
            writer.endEntry();
            writer.close();

            final ByteBuffer expected = ByteBuffer.allocate(45).order(ByteOrder.LITTLE_ENDIAN)
                .put(new byte[]{'A', 'R', 'C', 'H', '0', '0', '0'}) /* signature */
                .put(writer.getCoder().getId())                     /* compression */
                .putInt(25)                                         /* descriptor table entries offset */
                .putShort((short) 1)                                /* descriptor table entries count */

                .put("hello world".getBytes())                      /* entry data */

                .putInt(HashUtils.djb2("test"))                     /* entry hash low */
                .putInt(HashUtils.fnv1("test"))                     /* entry hash high */
                .putInt(14)                                         /* entry data offset */
                .putInt(11)                                         /* entry data length */
                .putInt(0x95757e36);                                /* entry data crc */

            Assertions.assertArrayEquals(expected.array(), actual);
        }
    }
}
