package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class StringParser implements IParser<String> {

    public String parse(String value) {
        return value;
    }

    public List<Class<?>> canParse() {
        return List.of(String.class);
    }
}
