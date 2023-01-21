package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class ByteParser implements IParser<Byte> {

    public Byte parse(String value) throws ConfigParseException {
        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Value '" + value + "' is not a valid byte");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(byte.class, Byte.class);
    }
}
