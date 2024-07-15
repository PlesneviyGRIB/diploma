package tests;

import com.core.sqlTool.exception.ComputedTypeException;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.LazyTable;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.expression.Number;
import com.core.sqlTool.model.visitor.ExpressionValidator;
import com.client.sqlTool.query.Query;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ExpressionTest extends TestBase {

//    @Test
//    public void expectSingleTypeInList() {
//        Consumer<ExpressionList> test = list -> Q.op(IN, new Number(1), list).accept(new ExpressionValidator(List.of(), ExternalHeaderRow.empty()));
//
//        test.accept(new ExpressionList(List.of(new Number(1), new Number(2)), Number.class));
//
//        expectError(
//                () -> test.accept(new ExpressionList(List.of(new Number(1), new LongNumber(2L)), Number.class)),
//                ComputedTypeException.class
//        );
//    }
//
//    @Test
//    public void inSubTable() {
//        var subTable = new SubTable(Query
//                .from("course_users")
//                .select(Q.column("course_users", "course_id"))
//                .build()
//        );
//
//        var res = resolver.resolve(
//                Query
//                        .from("courses")
//                        .where(Q.op(IN,
//                                Q.column("courses", "id"),
//                                subTable
//                        ))
//                        .select(Q.column("courses", "id"))
//        ).lazyTable();
//
//        var data = res.dataStream().toList();
//
//        Assert.assertEquals(13, data.size());
//
//        Assert.assertEquals(
//                List.of(101L, 1L, 153L, 154L, 155L, 151L, 156L, 157L, 158L, 159L, 160L, 2L, 3L),
//                retrieveIds(data)
//        );
//    }
//
//    @Test
//    public void notInSubTable() {
//        var subTable = new SubTable(Query
//                .from("course_users")
//                .select(Q.column("course_users", "course_id"))
//                .build()
//        );
//
//        var res = resolver.resolve(
//                Query
//                        .from("courses")
//                        .where(Q.op(
//                                NOT,
//                                Q.op(IN,
//                                        Q.column("courses", "id"),
//                                        subTable
//                                )
//                        ))
//                        .select(Q.column("courses", "id"))
//        ).lazyTable();
//
//        var data = res.dataStream().toList();
//
//        Assert.assertEquals(1, data.size());
//
//        Assert.assertEquals(List.of(152L), retrieveIds(data));
//    }
//
//    @Test
//    public void existsInSubTable() {
//        var res = resolver.resolve(
//                Query
//                        .from("expression")
//                        .as("ex")
//                        .where(Q.op(EXISTS,
//                                new SubTable(Query
//                                        .from("expression")
//                                        .select(Q.column("expression", "id"))
//                                        .where(Q.op(EQ,
//                                                Q.column("expression", "id"),
//                                                Q.op(MINUS,
//                                                        Q.column("ex", "id"),
//                                                        new LongNumber(43L)
//                                                )))
//                                        .build()
//                                )
//                        ))
//                        .select(Q.column("ex", "id"))
//        ).lazyTable();
//
//        Assert.assertEquals(550, res.dataStream().toList().size());
//
//    }
//
//    @Test
//    public void like() {
//        Function<String, LazyTable> resultProvider = pattern -> resolver.resolve(
//                Query
//                        .from("actions")
//                        .where(Q.op(LIKE, Q.column("actions", "parameters"), new StringValue(pattern)))
//        ).lazyTable();
//
//        Assert.assertEquals(435, resultProvider.apply("%final%").dataStream().toList().size());
//        Assert.assertEquals(418, resultProvider.apply("\\{\\}").dataStream().toList().size());
//        Assert.assertEquals(409, resultProvider.apply("%finalAnswerPlaceholderId%").dataStream().toList().size());
//        Assert.assertEquals(50, resultProvider.apply("%c5666703-4b9f-40f6-9268-8f92619d1199%").dataStream().toList().size());
//    }

}
