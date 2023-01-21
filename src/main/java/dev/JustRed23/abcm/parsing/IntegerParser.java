package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class IntegerParser implements IParser<Integer> {

    public Integer parse(String value) throws ConfigParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Value '" + value + "' is not a valid integer");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(int.class, Integer.class);
    }
}
