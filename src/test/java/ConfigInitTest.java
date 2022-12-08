import dev.JustRed23.abcm.Config;
import dev.JustRed23.abcm.exception.ConfigAlreadyInitializedException;
import dev.JustRed23.abcm.exception.ConfigInitException;
import dev.JustRed23.abcm.exception.ConfigNotInitializedException;
import org.junit.jupiter.api.Test;
import testpkg.TestConfig;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Config.init();
        assertThrows(ConfigAlreadyInitializedException.class, Config::init);
        assertThrows(ConfigAlreadyInitializedException.class, () -> Config.setDebug(false));
        assertDoesNotThrow(() -> Config.rescan(true));
        Config.destroy();
    }
}
