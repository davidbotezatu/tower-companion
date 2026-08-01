package io.github.davidbotezatu.nrbf;

import io.github.davidbotezatu.nrbf.io.NrbfBinaryReader;
import io.github.davidbotezatu.nrbf.record.SerializedStreamHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class Main {
    static void main(String[] args) {

        try (InputStream inputFile =
                     Main.class.getResourceAsStream("/samples/playerInfo.dat")) {

            if (inputFile == null) {
                System.err.println("Could not find /samples/playerInfo.dat");
                return;
            }

            try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputFile)) {
                byte[] data = gzipInputStream.readAllBytes();

                System.out.println("File size: " + data.length + " bytes");

                NrbfBinaryReader reader = new NrbfBinaryReader(data);

                for (int index = 0; index < 16; index++) {
                    int value = reader.readByte();
                    System.out.printf("%02X ", value);
                }

                System.out.println();

                reader.seek(0);

                int recordType = reader.readByte();

                if (recordType != 0) {
                    throw new IllegalStateException(
                            "Expected SerializedStreamHeader record type 0, but found " + recordType
                    );
                }

                SerializedStreamHeader header = readSerializedStreamHeader(reader);

                System.out.println(header);
                System.out.println("Current position: " + reader.currentPosition());
            }
        } catch (IOException exception) {
            System.err.println("Error reading file: " + exception.getMessage());
        }
    }

    private static SerializedStreamHeader readSerializedStreamHeader(NrbfBinaryReader reader) {
        int rootId = reader.readInt32();
        int headerId = reader.readInt32();
        int majorVersion = reader.readInt32();
        int minorVersion = reader.readInt32();

        return new SerializedStreamHeader(rootId, headerId, majorVersion, minorVersion);
    }

}
