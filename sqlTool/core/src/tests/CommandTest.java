package tests;

import com.client.sqlTool.command.Order;
import com.client.sqlTool.domain.Column;
import com.client.sqlTool.expression.Binary;
import com.client.sqlTool.expression.Bool;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.expression.Unary;
import com.client.sqlTool.query.Query;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.client.sqlTool.expression.Operator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandTest extends TestBase {

    @Test
    public void limitTest() {
        expectRowsCount(Query.from("actions").limit(1052), 1052);
        expectRowsCount(Query.from("actions").limit(50), 50);
        expectRowsCount(Query.from("actions").limit(4000), 1052);
        expectRowsCount(Query.from("actions").limit(50).limit(300).limit(20), 20);
    }

    @Test
    public void offsetTest() {
        expectRowsCount(Query.from("actions").offset(0), 1052);
        expectRowsCount(Query.from("actions").offset(1052), 0);
        expectRowsCount(Query.from("actions").offset(4000), 0);
        expectRowsCount(Query.from("actions").offset(50), 1002);
        expectRowsCount(Query.from("actions").offset(500).offset(300).offset(20), 232);
    }

    @Test
    public void columnsCountTest() {
        expectColumnsCount(Query.from("actions"), 9);
        expectColumnsCount(Query.from("actions").innerLoopJoin(Query.from("actions").as("a"), Bool.TRUE), 18);
        expectColumnsCount(Query.from("actions")
                .innerLoopJoin(Query.from("activities"), Bool.TRUE)
                .innerLoopJoin(Query.from("auth_sources"), Bool.TRUE), 24);
        expectColumnsCount(Query.from("actions").select(Column.of("id")), 1);
    }

    @Test
    public void innerJoinTest() {
        expectRowsCount(Query.from("courses").innerLoopJoin(
                        Query.from("course_users"),
                        Binary.of(EQ, Column.of("courses.id"), Column.of("course_id"))
                ),
                45
        );
        expectRowsCount(Query.from("math_elements").innerLoopJoin(
                        Query.from("final_answers"),
                        Binary.of(EQ, Column.of("math_elements.id"), Column.of("element_id"))
                ),
                160
        );
        expectRowsCount(Query.from("content").innerLoopJoin(
                        Query.from("content_descriptor"),
                        Binary.of(EQ, Column.of("content.id"), Column.of("actual_content_id"))
                ),
                323
        );
    }

    @Test
    public void leftJoinTest() {
        expectRowsCount(Query.from("courses").leftLoopJoin(
                        Query.from("course_users"),
                        Binary.of(EQ, Column.of("courses.id"), Column.of("course_id"))
                ),
                46
        );
        expectRowsCount(Query.from("math_elements").leftLoopJoin(
                        Query.from("final_answers"),
                        Binary.of(EQ, Column.of("math_elements.id"), Column.of("element_id"))
                ),
                1529
        );
        expectRowsCount(Query.from("content").leftLoopJoin(
                        Query.from("content_descriptor"),
                        Binary.of(EQ, Column.of("content.id"), Column.of("actual_content_id"))
                ),
                331
        );
    }

    @Test
    public void rightJoinTest() {
        expectRowsCount(Query.from("courses").rightLoopJoin(
                        Query.from("course_users"),
                        Binary.of(EQ, Column.of("courses.id"), Column.of("course_id"))
                ),
                45
        );
        expectRowsCount(Query.from("math_elements").rightLoopJoin(
                        Query.from("final_answers"),
                        Binary.of(EQ, Column.of("math_elements.id"), Column.of("element_id"))
                ),
                434
        );
        expectRowsCount(Query.from("content").rightLoopJoin(
                        Query.from("content_descriptor"),
                        Binary.of(EQ, Column.of("content.id"), Column.of("actual_content_id"))
                ),
                331
        );
    }

    @Test
    public void fullJoinTest() {
        expectRowsCount(Query.from("courses").fullLoopJoin(
                        Query.from("course_users"),
                        Binary.of(EQ, Column.of("courses.id"), Column.of("course_id"))
                ),
                46
        );
        expectRowsCount(Query.from("math_elements").fullLoopJoin(
                        Query.from("final_answers"),
                        Binary.of(EQ, Column.of("math_elements.id"), Column.of("element_id"))
                ),
                1803
        );
        expectRowsCount(Query.from("content").fullLoopJoin(
                        Query.from("content_descriptor"),
                        Binary.of(EQ, Column.of("content.id"), Column.of("actual_content_id"))
                ),
                339
        );
    }

    @Test
    public void fullJoinEqualsCartesianProduct() {
        var tableSize = execute(Query.from("content").fullLoopJoin(Query.from("content_descriptor"), Bool.TRUE))
                .dataStream().count();

        var mathElementsTable = execute(Query.from("content"));
        var finalAnswersTable = execute(Query.from("content_descriptor"));

        var cartesianProduct = cartesianProduct(mathElementsTable, finalAnswersTable);

        assertEquals(cartesianProduct.dataStream().count(), tableSize);
    }

    @Test
    public void hashJoinStrategy() {
        expectRowsCount(Query.from("content")
                        .fullLoopJoin(Query.from("content_descriptor"), Binary.of(EQ, Column.of("content.id"), Column.of("actual_content_id"))),
                339
        );

        expectRowsCount(
                Query.from("content").fullLoopJoin(
                        Query.from("content_descriptor"),
                        Binary.of(EQ,
                                Binary.of(MINUS,
                                        Binary.of(PLUS,
                                                Binary.of(MULTIPLY, Number.of(2), Column.of("content.id")),
                                                Column.of("content.id")
                                        ),
                                        Column.of("content.id")
                                ),
                                Binary.of(DIVISION,
                                        Column.of("actual_content_id"),
                                        Number.of(1)
                                )
                        )
                ),
                584
        );
    }

    @Test
    public void distinctValues() {
        expectRowsCount(Query.from("courses").select(Column.of("version")).distinct(), 6);
        expectRowsCount(Query.from("courses").select(Column.of("lti_context_id")).distinct(), 3);
        expectRowsCount(Query.from("courses").select(Column.of("version"), Column.of("university_id")).distinct(), 8);
        expectRowsCount(Query.from("courses").select(Column.of("lti_context_id"), Column.of("name")).distinct(), 10);
    }

    @Test
    public void groupBy() {
        expectRowsCount(Query.from("courses")
                        .select(Column.of("lti_context_id"), Column.of("id"))
                        .groupBy(Column.of("lti_context_id"), Column.of("id"))
                        .aggregate(),
                14
        );
    }

    @Test
    public void externalRowTest() {
        var predicate = Binary.of(AND,
                Binary.of(EQ, Binary.of(MINUS, Column.of("c.id"), Number.of(2)), Column.of("c1.id")),
                Binary.of(EQ, Column.of("c1.id"), Binary.of(MINUS, Column.of("c2.id"), Number.of(4)))
        );

        var query = Query.from("courses").as("c")
                .where(Unary.of(EXISTS, Query.from("courses").as("c1")
                        .where(Unary.of(EXISTS, Query.from("courses").as("c2")
                                .where(predicate)))
                ))
                .orderBy(Order.of(Column.of("c.id")))
                .select(Column.of("c.id"));

        var ids = retrieveIds(execute(query).dataStream().toList());

        assertEquals(List.of(153, 154, 155, 156, 157, 158), ids);
    }

    private void expectRowsCount(Query query, int count) {
        assertEquals(count, execute(query).dataStream().count());
    }

    private void expectColumnsCount(Query query, int count) {
        assertEquals(count, execute(query).columns().size());
    }

}
