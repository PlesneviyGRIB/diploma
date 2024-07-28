package tests;

import com.client.sqlTool.command.Command;
import com.client.sqlTool.command.Select;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Binary;
import com.client.sqlTool.expression.Bool;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.exception.AmbiguousColumnReferenceException;
import com.core.sqlTool.exception.ColumnNotFoundException;
import com.core.sqlTool.model.command.SelectCommand;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.visitor.ExpressionResultTypeResolver;
import com.core.sqlTool.utils.DtoToModelConverter;
import com.core.sqlTool.utils.ModelUtils;
import org.junit.Test;

import java.util.List;

import static com.client.sqlTool.expression.Operator.EQ;

public class ColumnNameTest extends TestBase {

    @Test
    public void newTableColumns() {
        var table = execute(Query.from("courses")
                .select(Number.of(1).as("num"), Number.of(1).as("num"), Bool.TRUE.as("flag"))
        );

        expectResolvedColumns(table, Column.of("courses.flag"), Column.of("flag"));
        expectAmbiguousColumnException(table, Column.of("num"));
    }


    @Test
    public void tableColumns() {
        var table = execute(Query.from("courses")
                .select(Column.of("id"), Column.of("name"))
        );

        expectResolvedColumns(table, Column.of("courses.id"), Column.of("id"), Column.of("courses.name"), Column.of("name"));
        expectColumnNotFoundException(table, Column.of("courses.price"), Column.of("price"));
    }

    @Test
    public void tableRenamedColumns() {
        var table = execute(Query.from("courses")
                .select(Column.of("id").as("id_1"), Column.of("name").as("name_1"))
        );

        expectResolvedColumns(table, Column.of("courses.id_1"), Column.of("id_1"), Column.of("courses.name_1"), Column.of("name_1"));
        expectColumnNotFoundException(table, Column.of("courses.id"), Column.of("id"), Column.of("courses.name"), Column.of("name"));
    }

    @Test
    public void renamedTableColumns() {
        var table = execute(Query.from("courses").as("c")
                .select(Column.of("id"), Column.of("name"))
        );

        expectResolvedColumns(table, Column.of("c.id"), Column.of("id"), Column.of("c.name"), Column.of("name"));
        expectColumnNotFoundException(table, Column.of("courses.id"), Column.of("courses.name"));
    }

    @Test
    public void joinedTableColumns() {
        var table = execute(Query.from("courses")
                .innerLoopJoin(
                        Query.from("course_users"),
                        Binary.of(EQ, Column.of("courses.id"), Column.of("course_users.id")))
                .select(Column.of("courses.id"), Column.of("course_users.id"), Column.of("name"))
        );

        expectResolvedColumns(table, Column.of("courses.id"), Column.of("course_users.id"), Column.of("courses.name"), Column.of("name"));
        expectAmbiguousColumnException(table, Column.of("id"));
    }

    @Test
    public void renamedJoinedTableColumns() {
        var table = execute(Query.from("courses").as("c")
                .innerLoopJoin(
                        Query.from("course_users").as("cu"),
                        Binary.of(EQ, Column.of("c.id"), Column.of("cu.id")))
                .select(Column.of("c.id"), Column.of("cu.id"), Column.of("name"))
        );

        expectResolvedColumns(table, Column.of("c.id"), Column.of("cu.id"), Column.of("c.name"), Column.of("name"));
        expectColumnNotFoundException(table, Column.of("courses.id"), Column.of("courses.name"), Column.of("course_users.id"));
        expectAmbiguousColumnException(table, Column.of("id"));
    }

    @Test
    public void selfJoinedTableColumns() {
        var table = execute(Query.from("courses").as("c")
                .innerLoopJoin(Query.from("courses"), Bool.TRUE)
                .select(Column.of("c.id"), Column.of("courses.id"), Column.of("courses.name"), Column.of("c.name"))
        );

        expectResolvedColumns(table, Column.of("c.id"), Column.of("courses.id"), Column.of("courses.name"), Column.of("c.name"));
        expectAmbiguousColumnException(table, Column.of("id"), Column.of("name"));
    }


    private void expectResolvedColumns(LazyTable lazyTable, Column... columns) {
        var domainColumns = lazyTable.columns();

        List.of(columns).forEach(column -> {
            var dtoExpressions = List.<Command>of(new Select(List.of(column)));
            var commands = new DtoToModelConverter().convert(dtoExpressions);
            var selectCommand = (SelectCommand) commands.get(0);
            var expression = selectCommand.expressions().get(0);

            var expressionResultTypeResolver = new ExpressionResultTypeResolver(lazyTable.columns(), ExternalHeaderRow.empty());
            var resolvedColumn = ModelUtils.getColumnFromExpression(expression, lazyTable, expressionResultTypeResolver);
            ModelUtils.resolveColumn(lazyTable.columns(), resolvedColumn);
        });
    }

    private void expectAmbiguousColumnException(LazyTable lazyTable, Column... columns) {
        List.of(columns).forEach(column ->
                expectException(() -> expectResolvedColumns(lazyTable, column), AmbiguousColumnReferenceException.class));
    }

    private void expectColumnNotFoundException(LazyTable lazyTable, Column... columns) {
        List.of(columns).forEach(column ->
                expectException(() -> expectResolvedColumns(lazyTable, column), ColumnNotFoundException.class));
    }

}
