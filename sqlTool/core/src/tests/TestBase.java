package tests;

import com.client.sqlTool.query.Query;
import com.core.sqlTool.model.cache.CacheContext;
import com.core.sqlTool.model.cache.CacheStrategy;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.domain.Projection;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.NumberValue;
import com.core.sqlTool.model.resolver.Resolver;
import com.core.sqlTool.support.WrappedStream;
import com.core.sqlTool.utils.DatabaseReader;
import com.core.sqlTool.utils.DtoToModelConverter;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.Assertions;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.core.sqlTool.config.Constants.*;
import static java.lang.String.format;

public class TestBase {

    static {
        try {
            var connection = DriverManager.getConnection(String.format("jdbc:postgresql://localhost:%s/%s", TEST_DB_PORT, TEST_DB_NAME), TEST_DB_USER, TEST_DB_PASSWORD);
            projection = new DatabaseReader(connection).read();
            resolver = new Resolver(TestBase.projection, new CacheContext(CacheStrategy.NONE));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final Projection projection;

    protected static final Resolver resolver;

    public LazyTable execute(Query query) {

        var modelCommands = new DtoToModelConverter().convert(query.getCommands());
        var resolverResult = resolver.resolve(modelCommands, ExternalHeaderRow.empty());

        return resolverResult.lazyTable();
    }

    protected void expectException(Runnable runnable, Class<? extends RuntimeException> exception) {
        try {
            runnable.run();
            Assertions.fail();
        } catch (Exception ex) {
            if (!exception.equals(ex.getClass())) {
                Assertions.fail(format("Expected [%s], but found [%s]", exception.getSimpleName(), ex.getClass().getSimpleName()));
            }
        }
    }

    protected LazyTable cartesianProduct(LazyTable lazyTable1, LazyTable lazyTable2) {

        var data1 = lazyTable1.dataStream();
        var data2 = new WrappedStream<>(lazyTable2.dataStream());
        var data = data1.flatMap(prefix -> data2.getStream().map(postfix -> Row.merge(prefix, postfix)));
        var columns = ListUtils.union(lazyTable1.columns(), lazyTable2.columns());

        return new LazyTable(null, columns, data, ExternalHeaderRow.empty());
    }

    protected List<Integer> retrieveIds(List<Row> data) {
        return data.stream()
                .map(row -> row.values().get(0))
                .filter(value -> value instanceof NumberValue)
                .map(ln -> ((NumberValue) ln).value())
                .toList();
    }

    protected Expression convertExpression(com.client.sqlTool.expression.Expression expression) {
        try {
            var convertExpressionMethod = DtoToModelConverter.class.getDeclaredMethod("convertExpression", com.client.sqlTool.expression.Expression.class);
            convertExpressionMethod.setAccessible(true);
            return (Expression) convertExpressionMethod.invoke(new DtoToModelConverter(), expression);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
