package tests;

import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.Test;

import java.util.function.Supplier;

import static com.savchenko.sqlTool.model.operator.Operator.EQ;
import static org.junit.Assert.assertEquals;

public class CommandTest extends TestBase {
    @Test
    public void limitTest() {
        Supplier<Query> query = () -> Query.create().from("actions");

        expectRowsCount(query.get().limit(1052), 1052);
        expectRowsCount(query.get().limit(50), 50);
        expectRowsCount(query.get().limit(4000), 1052);
        expectRowsCount(query.get().limit(50).limit(300).limit(20), 20);
    }

    @Test
    public void offsetTest() {
        Supplier<Query> query = () -> Query.create().from("actions");

        expectRowsCount(query.get().offset(0), 1052);
        expectRowsCount(query.get().offset(1052), 0);
        expectRowsCount(query.get().offset(4000), 0);
        expectRowsCount(query.get().offset(50), 1002);
        expectRowsCount(query.get().offset(500).offset(300).offset(20), 232);
    }

    @Test
    public void columnsCountTest() {
        assertEquals(resolver.resolve(Query.create().from("actions")).columns().size(), 9);
        assertEquals(resolver.resolve(Query.create().from("actions").innerJoin("actions", new BooleanValue(true))).columns().size(), 18);
        assertEquals(resolver.resolve(Query.create().from("actions")
                .innerJoin("activities", new BooleanValue(true))
                .innerJoin("auth_sources", new BooleanValue(true))
        ).columns().size(), 24);
        assertEquals(resolver.resolve(Query.create().from("actions").select(Q.column("actions", "id"))).columns().size(), 1);
    }

    @Test
    public void innerJoinTest() {
        TriFunction<String, String, Expression<?>, Query> join = (table1, table2, expression) -> Query.create()
                .from(table1)
                .innerJoin(table2, expression);

        expectRowsCount(
                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
                45
        );
        expectRowsCount(
                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
                160
        );
        expectRowsCount(
                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
                323
        );
    }

    @Test
    public void leftJoinTest() {
        TriFunction<String, String, Expression<?>, Query> join = (table1, table2, expression) -> Query.create()
                .from(table1)
                .leftJoin(table2, expression);
        expectRowsCount(
                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
                46
        );
        expectRowsCount(
                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
                1529
        );
        expectRowsCount(
                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
                331
        );
    }

    @Test
    public void rightJoinTest() {
        TriFunction<String, String, Expression<?>, Query> join = (table1, table2, expression) -> Query.create()
                .from(table1)
                .rightJoin(table2, expression);
        expectRowsCount(
                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
                45
        );
        expectRowsCount(
                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
                434
        );
        expectRowsCount(
                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
                331
        );
    }

    @Test
    public void fullJoinTest() {
        TriFunction<String, String, Expression<?>, Query> join = (table1, table2, expression) -> Query.create()
                .from(table1)
                .fullJoin(table2, expression);
        expectRowsCount(
                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
                46
        );
        expectRowsCount(
                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
                1803
        );
        expectRowsCount(
                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
                339
        );
    }

    private void expectRowsCount(Query query, int count) {
        assertEquals(count, resolver.resolve(query).data().size());
    }
}
