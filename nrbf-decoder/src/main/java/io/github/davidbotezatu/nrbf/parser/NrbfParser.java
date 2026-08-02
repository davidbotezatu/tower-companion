package io.github.davidbotezatu.nrbf.parser;

import io.github.davidbotezatu.nrbf.io.NrbfBinaryReader;
import io.github.davidbotezatu.nrbf.record.BinaryLibrary;
import io.github.davidbotezatu.nrbf.record.SerializedStreamHeader;

import java.util.Objects;

public class NrbfParser {
    private final NrbfBinaryReader reader;

    public NrbfParser(NrbfBinaryReader reader) {
        this.reader = Objects.requireNonNull(reader, "Reader must not be null");
    }

    public SerializedStreamHeader readSerializedStreamHeader() {
        int recordType = reader.readByte();

        if (recordType != 0) {
            throw new IllegalStateException(
                    "Expected SerializedStreamHeader record type 0, but found " + recordType
            );
        }

        int rootId = reader.readInt32();
        int headerId = reader.readInt32();
        int majorVersion = reader.readInt32();
        int minorVersion = reader.readInt32();

        return new SerializedStreamHeader(rootId, headerId, majorVersion, minorVersion);
    }

    private BinaryLibrary readBinaryLibraryBody() {
        int libraryId = reader.readInt32();
        String libraryName = reader.readLengthPrefixedString();

        return new BinaryLibrary(
                libraryId,
                libraryName
        );
    }

    public BinaryLibrary readBinaryLibrary() {
        int recordType = reader.readByte();

        if (recordType != 12) {
            throw new IllegalStateException("Expected BinaryLibrary record type 12, but found " + recordType);
        }

        return readBinaryLibraryBody();
    }
}
