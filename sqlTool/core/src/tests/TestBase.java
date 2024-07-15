package tests;

import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.domain.ExternalHeaderRow;
import com.savchenko.sqlTool.model.domain.LazyTable;
import com.savchenko.sqlTool.model.domain.Projection;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.support.WrappedStream;
import com.savchenko.sqlTool.utils.DatabaseReader;
import org.apache.commons.collections4.ListUtils;
import org.junit.Assert;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.savchenko.sqlTool.config.Constants.*;
import static java.lang.String.format;

public class TestBase {

    static {
        try {
            var connection = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", DB_DRIVER, DB_PORT, DB_NAME), DB_USER, DB_PASSWORD);
            projection = new DatabaseReader(connection).read();
            resolver = new Resolver(TestBase.projection, new CacheContext(CacheStrategy.NONE));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final Projection projection;

    protected static final Resolver resolver;

    void expectError(Runnable runnable, Class<? extends RuntimeException> exception) {
        try {
            runnable.run();
            Assert.fail();
        } catch (Exception ex) {
            if (!exception.equals(ex.getClass())) {
                Assert.fail(format("Expected [%s], but found [%s]", exception.getSimpleName(), ex.getClass().getSimpleName()));
            }
        }
    }

    protected LazyTable cartesianProduct(LazyTable lazyTable1, LazyTable lazyTable2) {
        var data1 = lazyTable1.dataStream();
        var data2 = new WrappedStream<>(lazyTable2.dataStream());
        var data = data1.flatMap(prefix -> data2.getStream().map(postfix -> Row.merge(prefix, postfix)));
        var tableName = format("%s_%s", lazyTable1.name(), lazyTable2.name());
        var columns = ListUtils.union(lazyTable1.columns(), lazyTable2.columns());
        return new LazyTable(tableName, columns, data, ExternalHeaderRow.empty());
    }

    protected List<Long> retrieveIds(List<Row> data) {
        return data.stream()
                .map(row -> row.values().get(0))
                .filter(value -> value instanceof LongNumber)
                .map(ln -> ((LongNumber) ln).value())
                .toList();
    }

}
