package test;

import com.savchenko.sqlTool.exception.ValidationException;
import com.savchenko.sqlTool.model.command.From;
import com.savchenko.sqlTool.model.command.JoinStrategy;
import com.savchenko.sqlTool.model.expression.BooleanValue;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.index.BalancedTreeIndex;
import com.savchenko.sqlTool.model.structure.Table;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import com.savchenko.sqlTool.utils.ModelUtils;
import com.savchenko.sqlTool.utils.SqlUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.savchenko.sqlTool.model.operator.Operator.EQ;
import static org.junit.Assert.assertEquals;

public class CommandTest extends TestBase {

    @Test
    public void limitTest() {
        Supplier<Query> table = () -> new Query(projection).from("actions");

        expectRowsCount(table.get().limit(1052), 1052);
        expectRowsCount(table.get().limit(50), 50);
        expectRowsCount(table.get().limit(4000), 1052);
        expectRowsCount(table.get().limit(50).limit(300).limit(20), 20);
    }

    @Test
    public void offsetTest() {
        Supplier<Query> table = () -> new Query(projection).from("actions");

        expectRowsCount(table.get().offset(0), 1052);
        expectRowsCount(table.get().offset(1052), 0);
        expectRowsCount(table.get().offset(4000), 0);
        expectRowsCount(table.get().offset(50), 1002);
        expectRowsCount(table.get().offset(500).offset(300).offset(20), 232);
    }

    @Test
    public void columnsCountTest() {
        assertEquals(resolver.resolve(new Query(projection).from("actions")).columns().size(), 9);
        assertEquals(resolver.resolve(new Query(projection).from("actions").innerJoin(new Query(projection).from("actions").as("a"), new BooleanValue(true), JoinStrategy.LOOP)).columns().size(), 18);
        assertEquals(resolver.resolve(new Query(projection).from("actions")
                .innerJoin(new Query(projection).from("activities"), new BooleanValue(true), JoinStrategy.LOOP)
                .innerJoin(new Query(projection).from("auth_sources"), new BooleanValue(true), JoinStrategy.LOOP)
        ).columns().size(), 24);
        assertEquals(resolver.resolve(new Query(projection).from("actions").select(Q.column("actions", "id"))).columns().size(), 1);
    }

    @Test
    public void innerJoinTest() {
        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> new Query(projection)
                .from(table1)
                .innerJoin(new Query(projection).from(table2), expression, JoinStrategy.LOOP);

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
        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> new Query(projection)
                .from(table1)
                .leftJoin(new Query(projection).from(table2), expression, JoinStrategy.LOOP);
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
        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> new Query(projection)
                .from(table1)
                .rightJoin(new Query(projection).from(table2), expression, JoinStrategy.LOOP);
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
        TriFunction<String, String, Expression, Query> join = (table1, table2, expression) -> new Query(projection)
                .from(table1)
                .fullJoin(new Query(projection).from(table2), expression, JoinStrategy.LOOP);
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

    @Test
    public void fullJoinEqualsCartesianProduct() {
        var resTable = resolver.resolve(
                new Query(projection)
                        .from("content")
                        .fullJoin( new Query(projection).from("content_descriptor"), new BooleanValue(true), JoinStrategy.LOOP)
        );

        var emtyTable = new Table("", List.of(), List.of(), List.of());

        var mathElementsTable = new From("content", projection).run(emtyTable);
        var finalAnswersTable = new From("content_descriptor", projection).run(emtyTable);

        var cartesianProduct = SqlUtils.cartesianProduct(mathElementsTable, finalAnswersTable);
        Assert.assertEquals(
                ModelUtils.renameTable(cartesianProduct, "res"),
                ModelUtils.renameTable(resTable, "res")
        );
    }

    @Test
    public void indicesPresentsInProjection() {
        var table = resolver.resolve(
                new Query(projection).from("courses")
        );
        Assert.assertEquals(4, table.indices().size());
        table.indices().forEach(index -> Assert.assertEquals(BalancedTreeIndex.class, index.getClass()));
    }

    @Test
    public void aliasTest() {
        BiConsumer<Table, List<String>> check = (table, columnNames) -> IntStream.range(0, columnNames.size())
                .forEach(index -> Assert.assertEquals(columnNames.get(index), table.columns().get(index).toString()));

        Consumer<Table> printer = table -> table.columns().forEach(System.out::println);

        var table1 = resolver.resolve(new Query(projection).from("wikis").as("w"));
        check.accept(table1, List.of("w.id", "w.text"));

        var table2 = resolver.resolve(new Query(projection).from("wikis").innerJoin(new Query(projection).from("tag"), new BooleanValue(true), JoinStrategy.LOOP));
        check.accept(table2, List.of("wikis_tag.wikis.id", "wikis_tag.text", "wikis_tag.tag.id", "wikis_tag.label",
                "wikis_tag.discriminator", "wikis_tag.math_id", "wikis_tag.relevancy", "wikis_tag.type"));

        var table3 = resolver.resolve(new Query(projection).from("wikis").innerJoin(new Query(projection).from("tag"), new BooleanValue(true), JoinStrategy.LOOP).as("w"));
        check.accept(table3, List.of("w.wikis.id", "w.text", "w.tag.id", "w.label", "w.discriminator", "w.math_id", "w.relevancy", "w.type"));

        expectError(
                () -> resolver.resolve(new Query(projection).from("wikis").innerJoin(new Query(projection).from("wikis"), new BooleanValue(true), JoinStrategy.LOOP)),
                ValidationException.class
        );

        var table4 = resolver.resolve(new Query(projection).from("wikis").as("w1").innerJoin(new Query(projection).from("wikis").as("w2"), new BooleanValue(true), JoinStrategy.LOOP));
        check.accept(table4, List.of("w1_w2.w1.id", "w1_w2.w1.text", "w1_w2.w2.id", "w1_w2.w2.text"));

        var table5 = resolver.resolve(new Query(projection).from("wikis").as("w1").innerJoin(new Query(projection).from("wikis").as("w2"), new BooleanValue(true), JoinStrategy.LOOP).as("res"));
        check.accept(table5, List.of("res.w1.id", "res.w1.text", "res.w2.id", "res.w2.text"));
    }

    private void expectRowsCount(Query query, int count) {
        assertEquals(count, resolver.resolve(query).data().size());
    }
}
