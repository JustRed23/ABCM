package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class ShortParser implements IParser<Short> {

    public Short parse(String value) throws ConfigParseException {
        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Value '" + value + "' is not a valid short");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(short.class, Short.class);
    }
}
