package io.github.davidbotezatu.nrbf.io;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class NrbfBinaryReader {
    private final byte[] data;
    private int position;

    public NrbfBinaryReader(byte[] data) {
        this.data = Objects.requireNonNull(data, "data must not be null");
        this.position = 0;
    }

    public int readByte() {
        if (position >= data.length) {
            throw new IllegalStateException("End of data file.");
        }
        return Byte.toUnsignedInt(data[position++]);
    }

    public int readInt32() {
        int byte0 = readByte();
        int byte1 = readByte();
        int byte2 = readByte();
        int byte3 = readByte();

        return byte0
                | byte1 << 8
                | byte2 << 16
                | byte3 << 24;
    }

    public int read7BitEncodedInt() {
        int result = 0;
        int shift = 0;

        while (shift < 35) {
            int currentByte = readByte();

            result |= (currentByte & 0x7F) << shift;

            if ((currentByte & 0x80) == 0) {
                return result;
            }

            shift += 7;
        }

        throw new IllegalStateException(
                "Invalid 7-bit encoded integer."
        );

    }

    public byte[] readBytes(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative.");
        }

        if (length > data.length - position) {
            throw new IllegalStateException("Not enough data remaining to read %d bytes.".formatted(length));
        }

        byte[] result = new byte[length];

        System.arraycopy(
                data,
                position,
                result,
                0,
                length
        );

        position += length;

        return result;
    }

    public String readLengthPrefixedString() {
        int byteLength = read7BitEncodedInt();
        byte[] stringBytes = readBytes(byteLength);

        return new String(
                stringBytes,
                StandardCharsets.UTF_8
        );
    }

    public int currentPosition() {
        return position;
    }

    public void seek(int newPosition) {
        if (newPosition > data.length || newPosition < 0) {
            throw new IllegalArgumentException(
                    "Position must be between 0 and %d, but was %d.".formatted(data.length, newPosition)
            );
        }

        this.position = newPosition;
    }

    public void move(int offset) {
        long newPosition = (long) position + offset;

        if (newPosition < 0 || newPosition > data.length) {
            throw new IllegalArgumentException(
                    "Cannot move from position %d by offset %d.".formatted(position, offset)
            );
        }

        position = (int) newPosition;
    }
}
