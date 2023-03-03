package dev.JustRed23.abcm;

import dev.JustRed23.abcm.exception.ConfigAlreadyInitializedException;
import dev.JustRed23.abcm.exception.ConfigInitException;
import dev.JustRed23.abcm.exception.ConfigNotInitializedException;
import dev.JustRed23.abcm.exception.ConfigParseException;
import dev.JustRed23.abcm.parsing.IParser;
import dev.JustRed23.abcm.parsing.ListParser;
import dev.JustRed23.abcm.util.ConfigFieldMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Config {

    //Fields
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private static boolean INITIALIZED, DEBUG;

    /**
     * The default config directory. This is where the config file will be stored.
     */
    private static File configDir = new File(System.getProperty("user.dir") + File.separator + "config");

    /**
     * A map containing all available parsers.
     */
    private static final Map<Class<?>, IParser<?>> parsers = new HashMap<>();

    /**
     * A map containing all scanned configuration classes and a list of their fields, default values and if the configuration is optional.
     */
    private static final Map<Class<?>, List<ConfigFieldMap>> configurations = new HashMap<>();

    /**
     * A list containing all packages that will be scanned for configuration classes.
     */
    private static final List<String> packages = new ArrayList<>();

    /**
     * A list containing all packages that are already scanned for configuration classes.
     */
    private static final List<String> scannedPackages = new ArrayList<>();

    //Settings
    /**
     * Adds a package to the list of packages that will be scanned for configuration classes.
     * <br><b>NOTE - The class must be in a package!</b>
     * @see #addScannable(String)
     * @param pkgClass The class that is in the package that will be scanned
     */
    public static void addScannable(@NotNull Class<?> pkgClass) {
        if (pkgClass.getPackage() == null)
            throw new IllegalArgumentException("The class " + pkgClass.getName() + " is not in a package");
        addScannable(pkgClass.getPackage());
    }

    /**
     * Adds a package to the list of packages that will be scanned for configuration classes.
     * @see #addScannable(String)
     * @param pkg The package to recursively scan for configurables
     */
    public static void addScannable(@NotNull Package pkg) {
        addScannable(pkg.getName());
    }

    /**
     * Adds a package to the list of packages that will be scanned for configuration classes.
     * <br>
     * <b>NOTE:</b> This method will not scan the package for configurables, it will only add it to the list of packages to scan.
     * You can call this method before you call {@link #init} or you can call {@link #rescan} after you added all your packages.
     * @param pkg The package to recursively scan for configurables
     */
    public static void addScannable(@NotNull String pkg) {
        if (!packages.contains(pkg))
            packages.add(pkg);
    }

    /**
     * Adds a parser to the list of parsers.
     * <br>
     * <b>NOTE:</b> This method requires you to call {@link #rescan} for all values to update correctly.
     * @param clazz The class of the parser to add. This must extend {@link IParser}.
     */
    public static void addParser(@NotNull Class<? extends IParser> clazz) {
        try {
            IParser<?> parser = clazz.getConstructor().newInstance();

            parser.canParse().forEach(type -> parsers.put(type, parser));

            debug("Added parser {} for {} {}",
                    parser.getClass().getSimpleName(),
                    parser.canParse().size() > 1 ? "types" : "type",
                    parser.canParse());

        } catch (Exception e) {
            debug("ERROR: The parser {} could not be loaded: {}", clazz.getSimpleName(), e.getMessage());
        }
    }

    /**
     * Sets the config directory. This is where the config file will be stored.
     * <br>
     * This defaults to the current working directory + "/config".
     * @param configDir The new config directory
     * @throws ConfigAlreadyInitializedException If the config is already initialized
     */
    public static void setConfigDir(@NotNull File configDir) {
        if (INITIALIZED)
            throw new ConfigAlreadyInitializedException();

        Config.configDir = configDir;
    }

    /**
     * Enables or disables debug mode. This will log more information about the configuration process.
     * @param debug Whether debug mode should be enabled
     * @throws ConfigAlreadyInitializedException If the config is already initialized
     */
    public static void setDebug(boolean debug) {
        if (INITIALIZED)
            throw new ConfigAlreadyInitializedException();

        DEBUG = debug;
    }

    //Methods
    /**
     * Initializes the config system,
     * this will scan all packages for configurables and initialize them,
     * setting all accessible fields marked with {@link ConfigField} to a value from the config file.
     * If the config file does not exist it will be made automatically and all fields will be set to their default values.
     * <p>
     * <b>NOTE:</b> To add a package to scan for configurables, use the {@link #addScannable(String)} method before calling this method.
     * @throws ConfigInitException If the config directory couldn't be created or if something went wrong while applying
     * @throws ConfigAlreadyInitializedException If the config is already initialized
     */
    public static void init() throws ConfigInitException {
        if (INITIALIZED)
            throw new ConfigAlreadyInitializedException();
        INITIALIZED = true;

        StopWatch stopWatch = new StopWatch();
        header("Initializing config manager");
        stopWatch.start();

        debug("Creating config directory at '{}'", configDir.getAbsolutePath());
        if (!configDir.exists()) {
            boolean created = configDir.mkdir();
            if (!created)
                throw new ConfigInitException("Failed to create config directory at '" + configDir.getAbsolutePath() + "'");
            debug("Created config directory");
        } else debug("Config directory already exists");

        subHeader("Adding type parsers");
        mapParsers();

        subHeader("Scanning packages");
        configurations.clear();
        if (packages.isEmpty())
            debug("Nothing to scan");
        else
            packages.forEach(Config::scanPackage);

        subHeader("Initializing configurations");
        makeFiles();

        subHeader("Applying configurations");
        apply();

        stopWatch.stop();
        header("Initialized config manager in {}ms", stopWatch.getTime());
    }

    /**
     * Rescans all packages for configurables, this is useful if you called {@link #addScannable(String)} or {@link #addParser(Class)} after the config system has been initialized already.
     * @param force If true, the config system will not check if a rescan is actually needed
     * @throws ConfigInitException If the config directory does not exist
     * @throws ConfigNotInitializedException If the config system has not been initialized yet
     */
    public static void rescan(boolean force) throws ConfigInitException {
        if (!INITIALIZED)
            throw new ConfigNotInitializedException();

        if (!force && !rescanNeeded())
            return;

        if (!configDir.exists())
            throw new ConfigInitException("Config directory does not exist");

        StopWatch stopWatch = new StopWatch();
        header("Rescanning packages");
        stopWatch.start();

        scannedPackages.clear();

        subHeader("Scanning packages");
        configurations.clear();
        if (packages.isEmpty())
            debug("Nothing to scan");
        else
            packages.forEach(Config::scanPackage);

        subHeader("Initializing configurations");
        makeFiles();

        subHeader("Applying configurations");
        apply();

        stopWatch.stop();
        header("Rescan completed in {}ms", stopWatch.getTime());
    }

    /**
     * Saves all modified {@link ConfigField}s to the config file. If no changes have been made, this method will do nothing.
     * <br>
     * To change a value, you can just simply set the {@link ConfigField} to a new value
     * @throws ConfigInitException If the config directory does not exist
     * @throws ConfigNotInitializedException If the config system has not been initialized yet
     */
    public static void save() throws ConfigInitException {
        if (!INITIALIZED)
            throw new ConfigNotInitializedException();

        if (!configDir.exists())
            throw new ConfigInitException("Config directory does not exist");

        StopWatch stopWatch = new StopWatch();
        header("Saving configurations");
        stopWatch.start();

        if (configurations.isEmpty())
            debug("Nothing to save");
        else
            updateFiles();

        stopWatch.stop();
        header("Saved configurations in {}ms", stopWatch.getTime());
    }

    /**
     * Destroy the config system, this will remove all configurables and parsers and set all setting to default but will not remove the already set fields.
     * @throws ConfigNotInitializedException If the config system has not been initialized yet
     */
    public static void destroy() {
        if (!INITIALIZED)
            throw new ConfigNotInitializedException();

        INITIALIZED = false;
        DEBUG = false;
        configDir = new File(System.getProperty("user.dir") + File.separator + "config");

        configurations.clear();
        scannedPackages.clear();
        packages.clear();
        parsers.clear();
    }

    //Helper methods
    private static void mapParsers() {
        Reflections reflections = new Reflections(IParser.class.getPackage().getName());
        reflections.getSubTypesOf(IParser.class).forEach(Config::addParser);
    }

    private static void scanPackage(String pkg) {
        Reflections reflections = new Reflections(pkg);
        debug("Looking for classes annotated with {} in package '{}'", Configurable.class.getSimpleName(), pkg);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Configurable.class);

        if (annotated.isEmpty()) {
            debug("\tNo classes found");
            return;
        }

        annotated.forEach(configClass -> {
            debug("\tFound config class: {}", configClass.getSimpleName());
            debug("\t\tLooking for fields annotated with {}", ConfigField.class.getSimpleName());
            List<ConfigFieldMap> fieldsWithDefaults = new ArrayList<>();
            Arrays.stream(configClass.getDeclaredFields()).forEach(field -> {
                try {
                    field.setAccessible(true);
                } catch (Exception e) {
                    debug("\t\t\tERROR: Failed to set field '{}' accessible: {}", field.getName(), e.getMessage());
                    return;
                }

                if (!field.isAccessible() || !field.isAnnotationPresent(ConfigField.class))
                    return;

                debug("\t\t\tFound field {} of type {}, setting to default value", field.getName(), field.getType().getSimpleName());

                ConfigField configField = field.getAnnotation(ConfigField.class);
                String defaultValue = configField.defaultValue();
                boolean optional = configField.optional();
                String description = configField.description().trim();

                try {
                    IParser<?> parser = parsers.get(field.getType());

                    if (parser == null) {
                        debug("\t\t\t\tNo parser found for type {}, skipping", field.getType().getSimpleName());
                        return;
                    }

                    ListParser.defaultValue = true;
                    field.set(field.getType(), parser.parse(defaultValue));
                    ListParser.defaultValue = false;
                } catch (Exception e) {
                    debug("\t\t\tERROR: Could not set field {} to it's default value: {}", field.getName(), e.getMessage());
                }
                fieldsWithDefaults.add(ConfigFieldMap.of(field, defaultValue, optional, description));
            });
            configurations.put(configClass, fieldsWithDefaults);
        });
        scannedPackages.add(pkg);
    }

    private static void makeFiles() {
        List<Exception> exceptions = new ArrayList<>();

        configurations.keySet().forEach(aClass -> {
            String name = aClass.getSimpleName().toLowerCase();
            Configurable configurable = aClass.getAnnotation(Configurable.class);
            if (configurable != null && !configurable.name().trim().isEmpty())
                name = configurable.name().trim();
            File configFile = new File(configDir, name + ".cfg");
            String cfgName = configFile.getName();

            debug("File {} {}", cfgName, configFile.exists() ? "already exists" : "does not exist, creating it");
            if (configFile.exists())
                return;

            try {
                if (!configFile.createNewFile()) {
                    debug("\tERROR: Could not create file {}. Check if the directory has valid read and write permissions", cfgName);
                    return;
                }

                PrintWriter pw = new PrintWriter(configFile);
                configurations.get(aClass).forEach(map -> {
                    Field field = map.getField();
                    String defaultValue = map.getDefaultValue();
                    String description = map.getDescription();

                    if (!description.isEmpty())
                        pw.println("# " + description);

                    ListParser.defaultValue = true;
                    try {
                        pw.println(field.getName() + "=" + parsers.get(field.getType()).save(defaultValue));
                    } catch (ConfigParseException e) {
                        exceptions.add(e);
                    }
                    ListParser.defaultValue = false;
                });
                pw.flush();
                pw.close();
                debug("\tCreated file {}", cfgName);
            } catch (IOException e) {
                debug("\tERROR: Could not create file {}: {}", cfgName, e.getMessage());
            }
        });

        if (!exceptions.isEmpty()) {
            Exception configex = new Exception("Failed to save default values to config files");
            exceptions.forEach(configex::addSuppressed);
            throw new RuntimeException(configex);
        }
    }

    private static void updateFiles() {
        List<Exception> exceptions = new ArrayList<>();

        configurations.keySet().forEach(aClass -> {
            String name = aClass.getSimpleName().toLowerCase();
            Configurable configurable = aClass.getAnnotation(Configurable.class);
            if (configurable != null && !configurable.name().trim().isEmpty())
                name = configurable.name().trim();
            File configFile = new File(configDir, name + ".cfg");
            String cfgName = configFile.getName();

            debug("Updating file {}", cfgName);
            if (!configFile.exists()) {
                debug("\tERROR: File {} does not exist", cfgName);
                return;
            }

            AtomicBoolean fileChanged = new AtomicBoolean(false);

            try {
                List<String> lines = Files.readAllLines(configFile.toPath());
                lines.removeIf(line -> line.startsWith("# "));

                List<String> newLines = new ArrayList<>();
                configurations.get(aClass).forEach(map -> {
                    Field field = map.getField();
                    String defaultValue = map.getDefaultValue();
                    boolean optional = map.isOptional();
                    String description = map.getDescription();
                    Object fieldValue;
                    try {
                        fieldValue = field.get(field.getType());
                    } catch (IllegalAccessException e) {
                        debug("\tERROR: Could not get value of field {}: {}", field.getName(), e.getMessage());
                        fieldValue = defaultValue;
                    }

                    if (fieldValue == null) {
                        if (optional)
                            return;
                        debug("\tERROR: Field {} is not optional but has no value, setting to default value", field.getName());
                        fieldValue = defaultValue;
                    }

                    ListParser.defaultValue = fieldValue.equals(defaultValue);
                    String line;
                    try {
                        line = field.getName() + "=" + parsers.get(field.getType()).save(fieldValue);
                    } catch (ConfigParseException e) {
                        exceptions.add(e);
                        ListParser.defaultValue = false;
                        return;
                    }
                    ListParser.defaultValue = false;

                    if (lines.contains(line)) {
                        if (!description.isEmpty())
                            newLines.add("# " + description);
                        newLines.add(line);
                        return;
                    }

                    debug("\tField {} has changed, updating", field.getName());
                    if (!description.isEmpty())
                        newLines.add("# " + description);
                    newLines.add(line);
                    fileChanged.set(true);
                });

                if (!fileChanged.get()) {
                    debug("\tNo changes found");
                    return;
                }

                PrintWriter pw = new PrintWriter(configFile);
                newLines.forEach(pw::println);
                pw.flush();
                pw.close();
                debug("\tUpdated file {}", cfgName);
            } catch (Exception e) {
                debug("\tERROR: Could not update file {}: {}", cfgName, e.getMessage());
            }
        });

        if (!exceptions.isEmpty()) {
            Exception configex = new Exception("Could not update config files");
            exceptions.forEach(configex::addSuppressed);
            throw new RuntimeException(configex);
        }
    }

    private static void apply() throws ConfigInitException {
        List<Throwable> exceptions = new ArrayList<>();
        configurations.keySet().forEach(aClass -> {
            String name = aClass.getSimpleName().toLowerCase();
            Configurable configurable = aClass.getAnnotation(Configurable.class);
            if (configurable != null && !configurable.name().trim().isEmpty())
                name = configurable.name().trim();
            File configFile = new File(configDir, name + ".cfg");
            String cfgName = configFile.getName();

            Properties prop = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                prop.load(fis);

                configurations.get(aClass).forEach(map -> {
                    Field field = map.getField();
                    String fieldName = field.getName();
                    String defaultValue = map.getDefaultValue();
                    boolean optional = map.isOptional();

                    debug("Setting field {}", fieldName);
                    String value = prop.getProperty(fieldName, null);

                    if (value == null) {
                        if (optional) {
                            debug("\tCould not find optional field {}, setting to default value", fieldName);
                            value = defaultValue;
                        } else {
                            debug("\tERROR: Could not find required field {}", fieldName);
                            exceptions.add(new ConfigInitException("Could not find required field " + fieldName + " in file " + cfgName));
                            return;
                        }
                    }

                    if (value.trim().isEmpty())
                        value = defaultValue;

                    try {
                        ListParser.defaultValue = value.equals(defaultValue);
                        field.set(field.getType(), parsers.get(field.getType()).parse(value));
                        ListParser.defaultValue = false;
                    } catch (IllegalAccessException e) {
                        debug("ERROR: Could not set field {} to it's value: {}", fieldName, e.getMessage());
                        exceptions.add(new ConfigInitException("Could not set field " + fieldName + " to it's value", e));
                    } catch (ConfigParseException e) {
                        debug("ERROR: Could not parse value of field {}: {}", fieldName, e.getMessage());
                        exceptions.add(new ConfigInitException("Could not parse value of field " + fieldName, e));
                    }
                });
            } catch (IOException e) {
                debug("ERROR: something went wrong while reading file {}: {}", cfgName, e.getMessage());
                exceptions.add(new ConfigInitException("Something went wrong while reading file " + cfgName, e));
            }
        });

        if (!exceptions.isEmpty()) {
            ConfigInitException initex = new ConfigInitException("Could not initialize configurables, check your terminal for more information");
            exceptions.forEach(initex::addSuppressed);
            throw initex;
        }
    }

    private static boolean rescanNeeded() {
        if (packages.isEmpty())
            return false;

        return !scannedPackages.equals(packages);
    }

    private static void header(String object, Object... args) {
        debug(StringUtils.center(" " + object + " ", 75, "="), args);
    }

    private static void subHeader(String object, Object... args) {
        debug(StringUtils.center(" " + object + " ", 70, "="), args);
    }

    private static void debug(String message, Object... args) {
        if (DEBUG)
            LOGGER.debug(message, args);
    }

    private static void debug(String message) {
        if (DEBUG)
            LOGGER.debug(message);
    }

    //Getters
    public static File getConfigDir() {
        return configDir;
    }

    public static boolean isInitialized() {
        return INITIALIZED;
    }

    public static boolean isDebug() {
        return DEBUG;
    }
}
