package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class BooleanParser implements IParser<Boolean> {

    public Boolean parse(String value) throws ConfigParseException {
        switch (value.toLowerCase()) {
            case "true":
                return true;
            case "false":
                return false;
            default:
                throw new ConfigParseException("Value '" + value + "' is not a valid boolean");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(boolean.class, Boolean.class);
    }
}
