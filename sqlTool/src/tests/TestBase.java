package tests;

import com.savchenko.sqlTool.model.Resolver;
import com.savchenko.sqlTool.utils.DatabaseReader;
import com.savchenko.sqlTool.model.domain.Projection;
import org.junit.Assert;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.savchenko.sqlTool.config.Constants.*;
import static java.lang.String.format;

public class TestBase {

    static {
        try {
            var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", DB_DRIVER, DB_PORT, DB_NAME), DB_USER, DB_PASSWORD);
            projection = new DatabaseReader(connection).read();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final Projection projection;

    protected static final Resolver resolver = new Resolver();

    void expectError(Runnable runnable, Class<? extends RuntimeException> exception) {
        try {
            runnable.run();
            Assert.fail();
        } catch (Exception ex) {
            if(!exception.equals(ex.getClass())) {
                Assert.fail(format("Expected [%s], but found [%s]", exception.getSimpleName(), ex.getClass().getSimpleName()));
            }
        }
    }
}
