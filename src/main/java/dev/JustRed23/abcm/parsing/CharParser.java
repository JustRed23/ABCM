package dev.JustRed23.abcm.parsing;

import java.util.List;

public final class CharParser implements IParser<Character> {

    public Character parse(String value) {
        return value.charAt(0);
    }

    public List<Class<?>> canParse() {
        return List.of(char.class, Character.class);
    }
}
