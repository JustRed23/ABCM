package dev.JustRed23.abcm.exception;

/**
 * This exception is thrown when a method is called after the Config class is initialized.
 */
public class ConfigAlreadyInitializedException extends IllegalStateException {

    public ConfigAlreadyInitializedException() {
        super("Config is already initialized");
    }
}
