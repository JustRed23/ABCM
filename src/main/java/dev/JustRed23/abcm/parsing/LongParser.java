package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class LongParser implements IParser<Long> {

    public Long parse(String value) throws ConfigParseException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Value '" + value + "' is not a valid long");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(long.class, Long.class);
    }
}
