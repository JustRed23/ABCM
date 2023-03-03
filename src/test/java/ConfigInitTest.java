import dev.JustRed23.abcm.Config;
import dev.JustRed23.abcm.exception.ConfigAlreadyInitializedException;
import dev.JustRed23.abcm.exception.ConfigInitException;
import dev.JustRed23.abcm.exception.ConfigNotInitializedException;
import org.junit.jupiter.api.Test;
import testpkg.TestConfig;

import static org.junit.jupiter.api.Assertions.*;

class ConfigInitTest {

    @Test
    void testConfig() {
        System.out.println("TEST 1");
        Config.setDebug(true);
        Config.addScannable(TestConfig.class);
        assertDoesNotThrow(Config::init);
        Config.destroy();
    }

    @Test
    void testThrows() throws ConfigInitException {
        System.out.println("TEST 2");
        assertThrows(ConfigNotInitializedException.class, () -> Config.rescan(false));
        Config.setDebug(true);
        assertThrows(IllegalArgumentException.class, () -> Config.addScannable(PackagelessTestConfig.class));
        Config.init();
        assertThrows(ConfigAlreadyInitializedException.class, Config::init);
        assertThrows(ConfigAlreadyInitializedException.class, () -> Config.setDebug(false));
        assertDoesNotThrow(() -> Config.rescan(true));
        Config.destroy();
    }

    @Test
    void testSave() throws ConfigInitException {
        System.out.println("TEST 3");
        Config.setDebug(true);
        Config.addScannable(TestConfig.class);
        Config.init();
        assertEquals("world", TestConfig.hello);
        TestConfig.hello = "Hello mom";
        Config.save();
        Config.destroy();

        Config.setDebug(true);
        Config.addScannable(TestConfig.class);
        Config.init();
        assertEquals("Hello mom", TestConfig.hello);
        TestConfig.hello = "world";
        Config.save();
        Config.destroy();
    }

    @Test
    void testList() throws ConfigInitException {
        System.out.println("TEST 4");
        Config.setDebug(true);
        Config.addScannable(TestConfig.class);
        Config.init();
        assertEquals(3, TestConfig.listTest.size());
        assertEquals("test", TestConfig.listTest.get(0));
        assertEquals("one", TestConfig.listTest.get(1));
        assertEquals("two", TestConfig.listTest.get(2));
        Config.destroy();
    }
}
