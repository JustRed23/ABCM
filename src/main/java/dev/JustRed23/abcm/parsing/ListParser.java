package dev.JustRed23.abcm.parsing;

import dev.JustRed23.abcm.exception.ConfigParseException;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListParser implements IParser<List<Object>> {

    public static boolean defaultValue = false;

    public List<Object> parse(String value) throws ConfigParseException {
        if (value == null) {
            if (!defaultValue)
                throw new ConfigParseException("Value is null");
            return Collections.emptyList();
        }

        try {
            JSONArray array = new JSONArray(value);
            return array.toList();
        } catch (JSONException ignored) {
            //assume that the value is not a json array string (default value?)
            if (!defaultValue)
                throw new ConfigParseException("Value '" + value + "' is not a valid json array string");

            String[] split = value.split(",");
            return Arrays.asList(split);
        }
    }

    public List<Class<?>> canParse() {
        return Collections.singletonList(List.class);
    }

    public String save(Object value) throws ConfigParseException {
        JSONArray array;
        try {
            array = new JSONArray((List<?>) value);
            return array.toString();
        } catch (JSONException | ClassCastException ignored) {
            //assume that the value is not a json array string (default value?)
            if (!defaultValue)
                throw new ConfigParseException("Value '" + value + "' is not a valid json array string");

            if (!(value instanceof String)) //should never happen but just in case
                throw new ConfigParseException("Value '" + value + "' is not a string");

            String[] split = ((String) value).split(",");
            array = new JSONArray(split);
            return array.toString();
        }
    }
}
