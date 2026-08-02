package io.github.davidbotezatu.nrbf;

import io.github.davidbotezatu.nrbf.io.NrbfBinaryReader;
import io.github.davidbotezatu.nrbf.parser.NrbfParser;
import io.github.davidbotezatu.nrbf.record.BinaryLibrary;
import io.github.davidbotezatu.nrbf.record.SerializedStreamHeader;
import io.github.davidbotezatu.nrbf.type.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Main {
     static void main() {
        InputStream inputFile = Main.class.getResourceAsStream("/samples/playerInfo.dat");

        if (inputFile == null) {
            System.err.println("Could not find /samples/playerInfo.dat");
            return;
        }

        try (
                inputFile;
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputFile)
        ) {
            byte[] data = gzipInputStream.readAllBytes();

            NrbfBinaryReader reader = new NrbfBinaryReader(data);
            NrbfParser parser = new NrbfParser(reader);
            SerializedStreamHeader header = parser.readSerializedStreamHeader();
            BinaryLibrary library = parser.readBinaryLibrary();
            BinaryLibrary secondLibrary = parser.readBinaryLibrary();

            System.out.println(header);
            System.out.println(library);
            System.out.println(secondLibrary);
            System.out.println("Current position: " + reader.currentPosition());

            int nextRecordType = reader.readByte();

            System.out.println("Next record type: " + nextRecordType);
            System.out.println("Current position: " + reader.currentPosition());

            int objectId = reader.readInt32();
            String className = reader.readLengthPrefixedString();
            int memberCount = reader.readInt32();

            System.out.println("Object ID: " + objectId);
            System.out.println("Class name: " + className);
            System.out.println("Member count: " + memberCount);
            System.out.println("Current position: " + reader.currentPosition());

            ArrayList<String> memberNames = new ArrayList<>(memberCount);
            for (int i = 0; i < memberCount; i++) {
                memberNames.add(reader.readLengthPrefixedString());
            }

            List<Integer> memberTypes = new ArrayList<>(memberCount);

            for (int index = 0; index < memberCount; index++) {
                memberTypes.add(reader.readByte());
            }

            System.out.println("First 20 members and binary types:");

            for (int index = 0; index < Math.min(20, memberCount); index++) {
                System.out.printf(
                        "%3d: %-35s type=%d%n",
                        index,
                        memberNames.get(index),
                        memberTypes.get(index)
                );
            }

            System.out.println(
                    "Current position: " + reader.currentPosition()
            );

            for (int index = 0; index < memberCount; index++) {
                int binaryType = memberTypes.get(index);

                if (binaryType == 3 || binaryType == 4) {
                    System.out.printf(
                            "First complex type: index=%d, name=%s, type=%d%n",
                            index,
                            memberNames.get(index),
                            binaryType
                    );
                    break;
                }
            }

            int[] typeCounts = new int[8];

            for (int binaryType : memberTypes) {
                typeCounts[binaryType]++;
            }

            for (int type = 0; type < typeCounts.length; type++) {
                System.out.printf(
                        "Binary type %d: %d members%n",
                        type,
                        typeCounts[type]
                );
            }

            List<AdditionalTypeInfo> additionalTypeInfos =
                    new ArrayList<>(memberCount);

            for (int index = 0; index < memberCount; index++) {
                int binaryType = memberTypes.get(index);

                AdditionalTypeInfo additionalInfo = switch (binaryType) {
                    case 0, 7 -> new PrimitiveTypeInfo(
                            reader.readByte()
                    );

                    case 1, 2, 5, 6 ->
                            NoAdditionalTypeInfo.INSTANCE;

                    case 3 -> new SystemClassTypeInfo(
                            reader.readLengthPrefixedString()
                    );

                    case 4 -> new ClassTypeInfo(
                            reader.readLengthPrefixedString(),
                            reader.readInt32()
                    );

                    default -> throw new IllegalStateException(
                            "Unknown binary type %d at member index %d."
                                    .formatted(binaryType, index)
                    );
                };

                additionalTypeInfos.add(additionalInfo);
            }

            for (int index = 0; index < 100; index++) {
                AdditionalTypeInfo info = additionalTypeInfos.get(index);

                if (info != NoAdditionalTypeInfo.INSTANCE) {
                    System.out.printf(
                            "%3d: %-35s binaryType=%d additional=%s%n",
                            index,
                            memberNames.get(index),
                            memberTypes.get(index),
                            info
                    );
                }
            }

            int classLibraryId = reader.readInt32();

            System.out.println("Class library ID: " + classLibraryId);
            System.out.println("Current position: " + reader.currentPosition());

        } catch (IOException exception) {
            System.err.println("Error reading file: " + exception.getMessage());
        }
    }
}
