package io.github.davidbotezatu.nrbf.record;

public record SerializedStreamHeader(int rootId,
                                     int headerId,
                                     int majorVersion,
                                     int minorVersion) {
}