package tests;

import org.junit.Assert;

public class TestBase {
    void expectError(Runnable runnable) {
        try {
            runnable.run();
            Assert.fail();
        } catch (Exception ignore) {}
    }
}
