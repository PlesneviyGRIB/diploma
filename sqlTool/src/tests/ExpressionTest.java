package tests;

import com.savchenko.sqlTool.exception.ComputedTypeException;
import com.savchenko.sqlTool.model.domain.ExternalRow;
import com.savchenko.sqlTool.model.expression.ExpressionList;
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

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class ExpressionTest extends TestBase {

    @Test
    public void expectSingleTypeInList() {
        Consumer<ExpressionList> test = list -> Q.op(IN, new IntegerNumber(1), list).accept(new ExpressionValidator(List.of(), ExternalRow.empty()));

        test.accept(new ExpressionList(List.of(new IntegerNumber(1), new IntegerNumber(2)), IntegerNumber.class));

        expectError(
                () -> test.accept(new ExpressionList(List.of(new IntegerNumber(1), new LongNumber(2L)), IntegerNumber.class)),
                ComputedTypeException.class
        );
    }

    @Test
    public void inSubTable() {
        var subTable = new SubTable(new Query()
                .from("course_users")
                .select(Q.column("course_users", "course_id"))
                .build()
        );

        var res = resolver.resolve(
                new Query()
                        .from("courses")
                        .where(Q.op(IN,
                                Q.column("courses", "id"),
                                subTable
                        ))
                        .select(Q.column("courses", "id"))
        );

        Assert.assertEquals(13, res.data().size());

        Assert.assertEquals(
                List.of(101L, 1L, 153L, 154L, 155L, 151L, 156L, 157L, 158L, 159L, 160L, 2L, 3L),
                retrieveIds(res)
        );
    }

    @Test
    public void notInSubTable() {
        var subTable = new SubTable(new Query()
                .from("course_users")
                .select(Q.column("course_users", "course_id"))
                .build()
        );

        var res = resolver.resolve(
                new Query()
                        .from("courses")
                        .where(Q.op(
                                NOT,
                                Q.op(IN,
                                        Q.column("courses", "id"),
                                        subTable
                                )
                        ))
                        .select(Q.column("courses", "id"))
        );

        Assert.assertEquals(1, res.data().size());

        Assert.assertEquals(List.of(152L), retrieveIds(res));
    }

    @Test
    public void existsInSubTable() {
        var res = resolver.resolve(
                new Query()
                        .from("expression")
                        .as("ex")
                        .where(Q.op(EXISTS,
                                new SubTable(new Query()
                                        .from("expression")
                                        .select(Q.column("expression", "id"))
                                        .where(Q.op(EQ,
                                                Q.column("expression", "id"),
                                                Q.op(MINUS,
                                                        Q.column("ex", "id"),
                                                        new LongNumber(43L)
                                                )))
                                        .build()
                                )
                        ))
                        .select(Q.column("ex", "id"))
        );

        Assert.assertEquals(550, res.data().size());

    }

}
