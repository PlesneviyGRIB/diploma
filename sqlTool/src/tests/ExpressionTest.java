package tests;

import com.savchenko.sqlTool.exception.ComputedTypeException;
import com.savchenko.sqlTool.model.command.ExpressionList;
import com.savchenko.sqlTool.model.expression.IntegerNumber;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.expression.SubTable;
import com.savchenko.sqlTool.model.visitor.ExpressionValidator;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static com.savchenko.sqlTool.model.operator.Operator.IN;

public class ExpressionTest extends TestBase {

    @Test
    public void expectSingleTypeInList() {
        Consumer<ExpressionList> test = list -> Q.op(IN, new IntegerNumber(1), list).accept(new ExpressionValidator(List.of()));

        test.accept(new ExpressionList(List.of(new IntegerNumber(1), new IntegerNumber(2))));

        expectError(
                () -> test.accept(new ExpressionList(List.of(new IntegerNumber(1), new LongNumber(2L)))),
                ComputedTypeException.class
        );
    }

    @Test
    public void inSubTable() {
        var subTable = new SubTable(new Query(projection)
                .from("course_users")
                .select(Q.column("course_users", "course_id"))
                .build()
        );

        var res = resolver.resolve(
                new Query(projection)
                        .from("courses")
                        .where(Q.op(IN,
                                Q.column("courses", "id"),
                                subTable
                        ))
                        .select(Q.column("courses", "id"))
        );

        Assert.assertEquals(13, res.data().size());

        var ids = res.data().stream()
                .map(row -> row.get(0))
                .filter(value -> value instanceof LongNumber)
                .map(ln -> ((LongNumber) ln).value())
                .toList();

        Assert.assertEquals(
                List.of(101L, 1L, 153L, 154L, 155L, 151L, 156L, 157L, 158L, 159L, 160L, 2L, 3L),
                ids
        );
    }
}
