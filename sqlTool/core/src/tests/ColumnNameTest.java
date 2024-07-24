package tests;

import com.client.sqlTool.domain.Column;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.model.domain.Table;
import com.core.sqlTool.utils.DtoUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.IntStream;

public class ColumnNameTest extends TestBase {

    @Test
    public void testTableColumns() {
        var table = execute(
                Query.from("courses").select(Column.of("id"), Column.of("name"))
        );

        expectColumns(List.of(Column.of("courses.id"), Column.of("courses.name")), table);
    }

    @Test
    public void testTableRenamedColumns() {
        var table = execute(
                Query.from("courses").select(Column.of("id").as("id_1"), Column.of("name").as("name_1"))
        );

        expectColumns(List.of(Column.of("courses.id_1"), Column.of("courses.name_1")), table);
    }

    @Test
    public void testRenamedTableColumns() {
        var table = execute(
                Query.from("courses").as("c").select(Column.of("id"), Column.of("name"))
        );

        expectColumns(List.of(Column.of("c.id"), Column.of("c.name")), table);
    }

    @Test
    public void testJoinedTableColumns() {
        var table = execute(
                Query.from("courses")
                        .innerLoopJoin()
                        .select(Column.of("id"), Column.of("name"))
        );

        expectColumns(List.of(Column.of("c.id"), Column.of("c.name")), table);
    }


    private void expectColumns(List<Column> columns, Table table) {
        var domainColumns = table.columns();

        IntStream.range(0, domainColumns.size())
                .forEach(index -> {
                    var domainColumn = domainColumns.get(index);
                    var dtoColumn = columns.get(index);
                    var pair = DtoUtils.parseTableAndColumnNames(dtoColumn);

                    Assert.assertEquals(domainColumn.getTableName(), pair.getLeft());
                    Assert.assertEquals(domainColumn.getColumnName(), pair.getRight());
                });
    }

}
