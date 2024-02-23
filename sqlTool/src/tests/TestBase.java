package tests;

import com.savchenko.sqlTool.config.Constants;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.query.QueryResolver;
import com.savchenko.sqlTool.repository.DBConnection;
import com.savchenko.sqlTool.repository.DBReader;
import com.savchenko.sqlTool.repository.Projection;
import org.junit.Assert;

import java.sql.SQLException;

import static java.lang.String.format;

public class TestBase {

    static {
        DBConnection.init(Constants.DB_DRIVER, Constants.DB_PORT, Constants.DB_NAME, Constants.DB_USER, Constants.DB_PASSWORD);
        projection = new DBReader().read(DBConnection.get());
        query = new Query(TestBase.projection);
    }

    protected static final Projection projection;
    protected static final Query query;
    protected static final QueryResolver resolver = new QueryResolver();


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
