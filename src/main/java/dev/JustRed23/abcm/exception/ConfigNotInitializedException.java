package dev.JustRed23.abcm.exception;

/**
 * This exception is thrown when a method is called before the Config class is initialized.
 */
public class ConfigNotInitializedException extends IllegalStateException {

    public ConfigNotInitializedException() {
        super("Config is not initialized");
    }
}
