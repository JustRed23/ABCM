package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class FloatParser implements IParser<Float> {

    public Float parse(String value) throws ConfigParseException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Value '" + value + "' is not a valid float");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(float.class, Float.class);
    }
}
