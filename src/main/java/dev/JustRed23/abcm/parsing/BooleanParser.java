package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class BooleanParser implements IParser<Boolean> {

    public Boolean parse(String value) {
        return Boolean.parseBoolean(value);
    }

    public List<Class<?>> canParse() {
        return List.of(boolean.class, Boolean.class);
    }
}
