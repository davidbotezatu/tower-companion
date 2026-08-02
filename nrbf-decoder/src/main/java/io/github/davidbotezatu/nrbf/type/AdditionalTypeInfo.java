package io.github.davidbotezatu.nrbf.type;

public sealed interface AdditionalTypeInfo
        permits PrimitiveTypeInfo,
        SystemClassTypeInfo,
        ClassTypeInfo,
        NoAdditionalTypeInfo {
}
