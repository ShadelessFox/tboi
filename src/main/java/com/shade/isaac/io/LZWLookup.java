package com.shade.isaac.io;

import java.io.IOException;
import java.io.OutputStream;

public class LZWLookup {
    public static final int RECORDS_COUNT = 4096;
    public static final int CODE_BITS = 8;

    private final Record[] records;
    private final byte[] queue;
    private int recordIndex;
    private int queueIndex;
    private int codeLength;

    public LZWLookup() {
        this.records = new Record[RECORDS_COUNT];
        this.queue = new byte[RECORDS_COUNT];
        this.reset();
    }

    public byte outputText(byte lastCharacter, int index, OutputStream os) throws IOException {
        while (index != -1) {
            final Record record = records[index];
            lastCharacter = record.character;
            index = record.previousCharacter;
            queue[queueIndex++] = lastCharacter;
        }
        while (queueIndex > 0) {
            os.write(queue[--queueIndex]);
        }
        return lastCharacter;
    }

    public void appendText(byte lastCharacter, int previousCharacter) {
        final Record record = records[recordIndex++];
        record.previousCharacter = previousCharacter;
        record.character = lastCharacter;
    }

    public int ensureCodeLength() {
        if (recordIndex >= (1 << codeLength))
            codeLength++;
        return codeLength;
    }

    public void reset() {
        for (int i = 0; i < records.length; i++) {
            records[i] = new Record((byte) (i < 1 << CODE_BITS ? i : 0));
        }
        recordIndex = 1 << CODE_BITS;
        codeLength = CODE_BITS;
    }

    public int getRecordsCount() {
        return recordIndex;
    }

    public int getCodeLength() {
        return codeLength;
    }

    private static class Record {
        private int previousCharacter;
        private byte character;

        public Record(byte character) {
            this.previousCharacter = -1;
            this.character = character;
        }
    }
}
