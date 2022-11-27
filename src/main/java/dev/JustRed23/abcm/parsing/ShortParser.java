package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class ShortParser implements IParser<Short> {

    public Short parse(String value) {
        return Short.parseShort(value);
    }

    public List<Class<?>> canParse() {
        return List.of(short.class, Short.class);
    }
}
