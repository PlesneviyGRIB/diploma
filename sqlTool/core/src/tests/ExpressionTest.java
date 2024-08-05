package tests;

import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.expression.String;
import com.client.sqlTool.expression.*;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.exception.ComputedTypeException;
import com.core.sqlTool.model.domain.LazyTable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.client.sqlTool.expression.Operator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionTest extends TestBase {

    @Test
    public void expectSingleTypeInList() {
        Consumer<Expression> executeExpression = expression -> execute(Query.from("courses").select(expression.as("tmp")));

        expectException(() -> executeExpression.accept(Binary.of(IN, Number.of(1),
                        com.client.sqlTool.expression.List.of(Number.of(1), Number.of(2)))),
                ComputedTypeException.class
        );
    }

    @Test
    public void inSubTable() {

        var data = execute(Query.from("courses")
                .where(Binary.of(IN, Column.of("id"), Query.from("course_users").select(Column.of("course_id"))))
                .select(Column.of("id"))
        ).dataStream().toList();

        assertEquals(13, data.size());
        assertEquals(List.of(101, 1, 153, 154, 155, 151, 156, 157, 158, 159, 160, 2, 3), retrieveIds(data));
    }

    @Test
    public void notInSubTable() {

        var data = execute(Query.from("courses")
                .where(Unary.of(NOT, Binary.of(IN, Column.of("id"), Query
                        .from("course_users")
                        .select(Column.of("course_id")))))
                .select(Column.of("id"))
        ).dataStream().toList();

        assertEquals(1, data.size());
        assertEquals(List.of(152), retrieveIds(data));
    }

    @Test
    public void existsInSubTable() {

        var data = execute(Query
                .from("expression").as("ex_1")
                .where(Unary.of(EXISTS, Query.from("expression").as("ex_2")
                                .select(Column.of("ex_2.id"))
                                .where(Binary.of(EQ, Column.of("ex_1.id"), Binary.of(MINUS, Column.of("ex_2.id"), Number.of(43))))
                        )
                )
                .select(Column.of("ex_1.id"))
        ).dataStream().toList();

        assertEquals(550, data.size());

    }

    @Test
    public void like() {

        Function<java.lang.String, LazyTable> resultProvider = pattern -> execute(Query
                .from("actions")
                .where(Binary.of(LIKE, Column.of("parameters"), String.of(pattern)))
        );

        assertEquals(435, resultProvider.apply("%final%").dataStream().toList().size());
        assertEquals(418, resultProvider.apply("\\{\\}").dataStream().toList().size());
        assertEquals(409, resultProvider.apply("%finalAnswerPlaceholderId%").dataStream().toList().size());
        assertEquals(50, resultProvider.apply("%c5666703-4b9f-40f6-9268-8f92619d1199%").dataStream().toList().size());
    }

    @Test
    public void expressionPrinter() {

        var dtoExpression = Binary.of(OR,
                Binary.of(
                        LESS_OR_EQ,
                        Column.of("id"),
                        Binary.of(MULTIPLY, Number.of(1), Number.of(1042))
                ),
                Binary.of(EQ, Column.of("action_id"), String.of("addRow"))
        );

        assertEquals("(COLUMN(id) <= (1 * 1042)) OR (COLUMN(action_id) = addRow)", convertExpression(dtoExpression).stringify());
    }

}
