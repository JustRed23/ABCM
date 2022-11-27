package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class FloatParser implements IParser<Float> {

    public Float parse(String value) {
        return Float.parseFloat(value);
    }

    public List<Class<?>> canParse() {
        return List.of(float.class, Float.class);
    }
}
