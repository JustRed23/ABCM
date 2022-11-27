package dev.JustRed23.abcm.exception;

import java.util.Collections;
import java.util.List;

public class ConfigInitException extends Exception {

    private final List<Throwable> causes;

    public ConfigInitException(String message) {
        super(message);
        causes = Collections.emptyList();
    }

    public ConfigInitException(String message, Throwable... causes) {
        this(message, List.of(causes));
    }

    public ConfigInitException(String message, List<Throwable> causes) {
        super(message);
        this.causes = causes;
    }

    public List<Throwable> getCauses() {
        return causes;
    }
}
