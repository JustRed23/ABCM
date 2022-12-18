package testpkg;

import dev.JustRed23.abcm.ConfigField;
import dev.JustRed23.abcm.Configurable;

@Configurable
public class TestConfig {

    @ConfigField(defaultValue = "world")
    public static String hello;

    @ConfigField(defaultValue = "true", description = "This is a test")
    public static boolean test;

    @ConfigField(defaultValue = "1")
    public static int number;

    @ConfigField(defaultValue = "1.0")
    public static double decimal;

    @ConfigField(defaultValue = "1.0")
    public static float decimal2;

    @ConfigField(defaultValue = "1")
    public static long number2;

    @ConfigField(defaultValue = "1")
    public static byte number3;

    @ConfigField(defaultValue = "1")
    public static short number4;

    @ConfigField(defaultValue = "1")
    public static char character;

    @ConfigField(defaultValue = "1")
    public static Integer number5;

    @ConfigField(defaultValue = "1.0")
    public static Double decimal3;

    @ConfigField(defaultValue = "1.0")
    public static Float decimal4;

    @ConfigField(defaultValue = "1")
    public static Long number6;

    @ConfigField(defaultValue = "1")
    public static Byte number7;

    @ConfigField(defaultValue = "1")
    public static Short number8;

    @ConfigField(defaultValue = "1")
    public static Character character2;
}
