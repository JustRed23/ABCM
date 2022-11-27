package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class ByteParser implements IParser<Byte> {

    public Byte parse(String value) {
        return Byte.parseByte(value);
    }

    public List<Class<?>> canParse() {
        return List.of(byte.class, Byte.class);
    }
}
