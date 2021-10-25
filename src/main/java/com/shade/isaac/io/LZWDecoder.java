package com.shade.isaac.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LZWDecoder {
    private final LZWLookup lookup = new LZWLookup();

    public byte[] decode(ByteBuffer buffer, int offset, int length) {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final BitReader reader = new BitReader(buffer.slice(offset, length));

        try {
            int previousValue = reader.readBits(lookup.getCodeLength());
            byte lastCharacter = lookup.outputText((byte) 0, previousValue, result);

            while (reader.getBitsRead() + lookup.getCodeLength() <= reader.getBitsLength()) {
                if (lookup.getRecordsCount() >= LZWLookup.RECORDS_COUNT - 1) {
                    lookup.reset();
                    previousValue = reader.readBits(lookup.getCodeLength());
                    lastCharacter = lookup.outputText(lastCharacter, previousValue, result);
                } else {
                    final int value = reader.readBits(lookup.ensureCodeLength());

                    if (value == lookup.getRecordsCount()) {
                        if (previousValue != -1) {
                            lookup.appendText(lastCharacter, previousValue);
                        }
                        lastCharacter = lookup.outputText(lastCharacter, value, result);
                    } else {
                        lastCharacter = lookup.outputText(lastCharacter, value, result);
                        if (previousValue != -1) {
                            lookup.appendText(lastCharacter, previousValue);
                        }
                    }

                    previousValue = value;
                }
            }
        } catch (IOException ignored) {
            // We're operating on byte array input stream, so no exceptions should be thrown
        }

        buffer.position(offset + length);

        return result.toByteArray();
    }
}
