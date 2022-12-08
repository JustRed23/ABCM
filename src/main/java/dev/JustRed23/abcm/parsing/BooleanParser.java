package dev.JustRed23.abcm.parsing;

import java.util.Arrays;
import java.util.List;

public final class BooleanParser implements IParser<Boolean> {

    public Boolean parse(String value) {
        return Boolean.parseBoolean(value);
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(boolean.class, Boolean.class);
    }
}
