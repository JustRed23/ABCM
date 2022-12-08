package dev.JustRed23.abcm.parsing;

import java.util.Arrays;
import java.util.List;

public final class ByteParser implements IParser<Byte> {

    public Byte parse(String value) {
        return Byte.parseByte(value);
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(byte.class, Byte.class);
    }
}
