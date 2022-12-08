package dev.JustRed23.abcm.parsing;

import java.util.Arrays;
import java.util.List;

public final class IntegerParser implements IParser<Integer> {

    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(int.class, Integer.class);
    }
}
