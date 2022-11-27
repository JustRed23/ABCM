package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class IntegerParser implements IParser<Integer> {

    public Integer parse(String value) {
        return Integer.parseInt(value);
    }

    public List<Class<?>> canParse() {
        return List.of(int.class, Integer.class);
    }
}
