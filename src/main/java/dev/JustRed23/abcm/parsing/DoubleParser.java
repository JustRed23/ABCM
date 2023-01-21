package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.Arrays;
import java.util.List;

public final class DoubleParser implements IParser<Double> {

    public Double parse(String value) throws ConfigParseException {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Value '" + value + "' is not a valid double");
        }
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(double.class, Double.class);
    }
}
