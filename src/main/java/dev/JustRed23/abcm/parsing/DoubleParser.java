package dev.JustRed23.abcm.parsing;

import java.util.Arrays;
import java.util.List;

public final class DoubleParser implements IParser<Double> {

    public Double parse(String value) {
        return Double.parseDouble(value);
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(double.class, Double.class);
    }
}
