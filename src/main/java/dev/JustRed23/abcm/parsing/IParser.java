package dev.JustRed23.abcm.parsing;

import java.util.List;

public interface IParser<T> {

    /**
     * Parses the given value to the type of the parser.
     * <br>
     * Note: This method can throw an exception if the value is not valid.
     * Ex. {@link NumberFormatException} if the value is not a number.
     * @param value The value to parse.
     * @return The parsed value.
     */
    T parse(String value);

    /**
     * @return The list of classes that this parser can parse.
     */
    List<Class<?>> canParse();
}
