package tests;

import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.exception.ValidationException;
import com.core.sqlTool.model.command.FromCommand;
import com.core.sqlTool.model.command.function.Identity;
import com.client.sqlTool.domain.JoinStrategy;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.*;
import com.client.sqlTool.query.Query;
import com.core.sqlTool.model.expression.Number;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class CommandTest extends TestBase {

//    @Test
//    public void limitTest() {
//        Supplier<Query> table = () -> Query.from("actions");
//
//        expectRowsCount(table.get().limit(1052), 1052);
//        expectRowsCount(table.get().limit(50), 50);
//        expectRowsCount(table.get().limit(4000), 1052);
//        expectRowsCount(table.get().limit(50).limit(300).limit(20), 20);
//    }
//
//    @Test
//    public void offsetTest() {
//        Supplier<Query> table = () -> Query.from("actions");
//
//        expectRowsCount(table.get().offset(0), 1052);
//        expectRowsCount(table.get().offset(1052), 0);
//        expectRowsCount(table.get().offset(4000), 0);
//        expectRowsCount(table.get().offset(50), 1002);
//        expectRowsCount(table.get().offset(500).offset(300).offset(20), 232);
//    }
//
//    @Test
//    public void columnsCountTest() {
//        assertEquals(resolver.resolve(Query.from("actions")).lazyTable().columns().size(), 9);
//        assertEquals(resolver.resolve(Query.from("actions").innerJoin(Query.from("actions").as("a"), new BooleanValue(true), JoinStrategy.LOOP)).lazyTable().columns().size(), 18);
//        assertEquals(resolver.resolve(Query.from("actions")
//                .innerJoin(Query.from("activities"), new BooleanValue(true), JoinStrategy.LOOP)
//                .innerJoin(Query.from("auth_sources"), new BooleanValue(true), JoinStrategy.LOOP)
//        ).lazyTable().columns().size(), 24);
//        assertEquals(resolver.resolve(Query.from("actions").select(Q.column("actions", "id"))).lazyTable().columns().size(), 1);
//    }
//
//    @Test
//    public void innerJoinTest() {
//        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> Query
//                .from(table1)
//                .innerJoin(Query.from(table2), expression, JoinStrategy.LOOP);
//
//        expectRowsCount(
//                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
//                45
//        );
//        expectRowsCount(
//                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
//                160
//        );
//        expectRowsCount(
//                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
//                323
//        );
//    }
//
//    @Test
//    public void leftJoinTest() {
//        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> Query
//                .from(table1)
//                .leftJoin(Query.from(table2), expression, JoinStrategy.LOOP);
//        expectRowsCount(
//                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
//                46
//        );
//        expectRowsCount(
//                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
//                1529
//        );
//        expectRowsCount(
//                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
//                331
//        );
//    }
//
//    @Test
//    public void rightJoinTest() {
//        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> Query
//                .from(table1)
//                .rightJoin(Query.from(table2), expression, JoinStrategy.LOOP);
//        expectRowsCount(
//                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
//                45
//        );
//        expectRowsCount(
//                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
//                434
//        );
//        expectRowsCount(
//                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
//                331
//        );
//    }
//
//    @Test
//    public void fullJoinTest() {
//        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> Query
//                .from(table1)
//                .fullJoin(Query.from(table2), expression, JoinStrategy.LOOP);
//        expectRowsCount(
//                join.apply("courses", "course_users", Q.op(EQ, Q.column("courses", "id"), Q.column("course_users", "course_id"))),
//                46
//        );
//        expectRowsCount(
//                join.apply("math_elements", "final_answers", Q.op(EQ, Q.column("math_elements", "id"), Q.column("final_answers", "element_id"))),
//                1803
//        );
//        expectRowsCount(
//                join.apply("content", "content_descriptor", Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
//                339
//        );
//    }
//
//    @Test
//    public void fullJoinEqualsCartesianProduct() {
//        var resTable = resolver.resolve(
//                Query.from("content").fullJoin(Query.from("content_descriptor"), new BooleanValue(true), JoinStrategy.LOOP)
//        ).lazyTable();
//
//        var emptyTable = new LazyTable(null, null, null, ExternalHeaderRow.empty());
//
//        var mathElementsTable = new FromCommand("content").run(emptyTable, projection);
//        var finalAnswersTable = new FromCommand("content_descriptor").run(emptyTable, projection);
//
//        var cartesianProduct = cartesianProduct(mathElementsTable, finalAnswersTable);
//
//        Assert.assertEquals(cartesianProduct.dataStream().count(), resTable.dataStream().count());
//    }
//
//    @Test
//    public void hashJoinStrategy() {
//        Function<Expression, Query> hashJoin = expression -> Query
//                .from("content")
//                .fullJoin(Query.from("content_descriptor"), expression, JoinStrategy.HASH);
//
//        expectError(() -> resolver.resolve(hashJoin.apply(new BooleanValue(true))), UnexpectedException.class);
//        expectError(() -> resolver.resolve(hashJoin.apply(new BinaryOperation(NOT_EQ, new Number(3), new Number(9)))), UnexpectedException.class);
//
//        expectRowsCount(
//                hashJoin.apply(Q.op(EQ, Q.column("content", "id"), Q.column("content_descriptor", "actual_content_id"))),
//                339
//        );
//
//        expectRowsCount(
//                hashJoin.apply(Q.op(EQ,
//                        Q.op(
//                                MINUS,
//                                Q.op(
//                                        PLUS,
//                                        Q.op(MULTIPLY, new LongNumber(2L), Q.column("content", "id")),
//                                        Q.column("content", "id")
//                                ),
//                                Q.column("content", "id")
//                        ),
//                        Q.op(
//                                DIVISION,
//                                Q.column("content_descriptor", "actual_content_id"),
//                                new LongNumber(1L)
//                        ))
//                ),
//                584
//        );
//    }
//
//    @Test
//    public void indicesPresentsInProjection() {
//        var table = resolver.resolve(Query.from("courses")
//        );
////        Assert.assertEquals(4, table.indices().size());
////        table.indices().forEach(index -> Assert.assertEquals(BalancedTreeIndex.class, index.getClass()));
//    }
//
//    @Test
//    public void aliasTest() {
//        BiConsumer<LazyTable, List<String>> check = (table, columnNames) -> IntStream.range(0, columnNames.size())
//                .forEach(index -> Assert.assertEquals(columnNames.get(index), table.columns().get(index).toString()));
//
//        var table1 = resolver.resolve(Query.from("wikis").as("w")).lazyTable();
//        check.accept(table1, List.of("w.id", "w.text"));
//
//        var table2 = resolver.resolve(Query.from("wikis").innerJoin(Query.from("tag"), new BooleanValue(true), JoinStrategy.LOOP)).lazyTable();
//        check.accept(table2, List.of("wikis_tag.wikis.id", "wikis_tag.text", "wikis_tag.tag.id", "wikis_tag.label",
//                "wikis_tag.discriminator", "wikis_tag.math_id", "wikis_tag.relevancy", "wikis_tag.type"));
//
//        var table3 = resolver.resolve(Query.from("wikis").innerJoin(Query.from("tag"), new BooleanValue(true), JoinStrategy.LOOP).as("w")).lazyTable();
//        check.accept(table3, List.of("w.wikis.id", "w.text", "w.tag.id", "w.label", "w.discriminator", "w.math_id", "w.relevancy", "w.type"));
//
//        expectError(
//                () -> resolver.resolve(Query.from("wikis").innerJoin(Query.from("wikis"), new BooleanValue(true), JoinStrategy.LOOP)),
//                ValidationException.class
//        );
//
//        var table4 = resolver.resolve(Query.from("wikis").as("w1").innerJoin(Query.from("wikis").as("w2"), new BooleanValue(true), JoinStrategy.LOOP)).lazyTable();
//        check.accept(table4, List.of("w1_w2.w1.id", "w1_w2.w1.text", "w1_w2.w2.id", "w1_w2.w2.text"));
//
//        var table5 = resolver.resolve(Query.from("wikis").as("w1").innerJoin(Query.from("wikis").as("w2"), new BooleanValue(true), JoinStrategy.LOOP).as("res")).lazyTable();
//        check.accept(table5, List.of("res.w1.id", "res.w1.text", "res.w2.id", "res.w2.text"));
//    }
//
//    @Test
//    public void distinctValues() {
//        Function<List<Column>, Query> table = columns -> Query.from("courses").select(columns.toArray(Column[]::new)).distinct();
//
//        expectRowsCount(table.apply(List.of(Q.column("courses", "version"))), 6);
//        expectRowsCount(table.apply(List.of(Q.column("courses", "lti_context_id"))), 3);
//        expectRowsCount(table.apply(List.of(Q.column("courses", "version"), Q.column("courses", "university_id"))), 8);
//        expectRowsCount(table.apply(List.of(Q.column("courses", "lti_context_id"), Q.column("courses", "name"))), 10);
//    }
//
//    @Test
//    public void groupBy() {
//        var query = Query
//                .from("courses")
//                .select(Q.column("courses", "lti_context_id"), Q.column("courses", "id"))
//                .groupBy(Map.of(Q.column("courses", "lti_context_id"), new Identity(), Q.column("courses", "id"), new Identity()));
//
//        expectRowsCount(query, 14);
//    }
//
//    @Test
//    public void ExternalRowTest() {
//
//        var predicate = Q.op(AND,
//                Q.op(EQ, Q.op(MINUS, Q.column("c", "id"), new LongNumber(2L)), Q.column("c1", "id")),
//                Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
//        );
//
//        var query = Query
//                .from("courses")
//                .as("c")
//                .where(Q.op(EXISTS, new SubTable(
//                        Query
//                                .from("courses")
//                                .as("c1")
//                                .where(Q.op(EXISTS, new SubTable(
//                                        Query
//                                                .from("courses")
//                                                .as("c2")
//                                                .where(predicate)
//                                                .build()))
//                                )
//                                .build()))
//                )
//                .orderBy(List.of(Pair.of(Q.column("c", "id"), false)))
//                .select(Q.column("c", "id"));
//
//        var resolverResult = resolver.resolve(query);
//        var ids = retrieveIds(resolverResult.lazyTable().dataStream().toList());
//
//        Assert.assertEquals(List.of(153L, 154L, 155L, 156L, 157L, 158L), ids);
//
//    }
//
//    private void expectRowsCount(Query query, int count) {
//        assertEquals(count, resolver.resolve(query).lazyTable().dataStream().toList().size());
//    }
}
