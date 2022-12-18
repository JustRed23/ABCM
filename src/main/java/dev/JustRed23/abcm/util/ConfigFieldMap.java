package dev.JustRed23.abcm.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ConfigFieldMap {

    private final Field field;
    private final String defaultValue;
    private final boolean optional;
    private final String description;

    public ConfigFieldMap(@NotNull Field field, @NotNull String defaultValue, boolean optional, @NotNull String description) {
        this.field = field;
        this.defaultValue = defaultValue;
        this.optional = optional;
        this.description = description;
    }

    public static @NotNull ConfigFieldMap of(@NotNull Field field, @NotNull String defaultValue, boolean optional, @NotNull String description) {
        return new ConfigFieldMap(field, defaultValue, optional, description);
    }

    public Field getField() {
        return field;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getDescription() {
        return description;
    }
}
