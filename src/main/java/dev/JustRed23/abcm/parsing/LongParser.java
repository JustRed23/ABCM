package dev.JustRed23.abcm.parsing;

import java.util.Arrays;
import java.util.List;

public final class LongParser implements IParser<Long> {

    public Long parse(String value) {
        return Long.parseLong(value);
    }

    public List<Class<?>> canParse() {
        return Arrays.asList(long.class, Long.class);
    }
}
