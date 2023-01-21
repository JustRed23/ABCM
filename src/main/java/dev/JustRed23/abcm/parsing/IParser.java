package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;

import java.util.List;

public interface IParser<T> {

    /**
     * Parses the given value to the type of the parser.
     * <br>
     * Note: This method can throw an exception if the value is not valid.
     * Ex. {@link NumberFormatException} if the value is not a number.
     * @param value The value to parse.
     * @return The parsed value.
     * @throws ConfigParseException If the provided value is not valid.
     */
    T parse(String value) throws ConfigParseException;

    /**
     * @return The list of classes that this parser can parse.
     */
    List<Class<?>> canParse();

    /**
     * Saves the given value to a string.
     * @param value The value to save.
     * @return The saved value.
     * @throws ConfigParseException If the provided value is not valid.
     */
    default String save(Object value) throws ConfigParseException {
        return String.valueOf(value);
    }
}
