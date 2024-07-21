package tests;

import com.core.sqlTool.model.cache.CacheContext;
import com.core.sqlTool.model.cache.CacheStrategy;
import com.client.sqlTool.domain.JoinStrategy;
import com.core.sqlTool.model.expression.SubTable;
import com.core.sqlTool.model.resolver.Resolver;
import com.client.sqlTool.query.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CacheTest extends TestBase {

//    private final List<Query> testQueries = List.of(
//            Query
//                    .from("courses")
//                    .as("c")
//                    .where(Q.op(EXISTS, new SubTable(
//                            Query
//                                    .from("courses")
//                                    .as("c1")
//                                    .where(Q.op(OR, Q.op(OR,
//                                                            Q.op(EXISTS, new SubTable(
//                                                                    Query
//                                                                            .from("courses")
//                                                                            .as("c2")
//                                                                            .where(Q.op(GREATER, Q.columnName("c2", "id"), new LongNumber(152L)))
//                                                                            .where(Q.op(AND,
//                                                                                    Q.op(EQ, Q.op(MINUS, Q.columnName("c1", "id"), new LongNumber(2L)), Q.columnName("c1", "id")),
//                                                                                    Q.op(EQ, Q.columnName("c1", "id"), Q.op(MINUS, Q.columnName("c2", "id"), new LongNumber(4L)))
//                                                                            ))
//                                                                            .build())),
//                                                            Q.op(NOT,
//                                                                    Q.op(EXISTS, new SubTable(
//                                                                            Query
//                                                                                    .from("courses")
//                                                                                    .as("c2")
//                                                                                    .where(Q.op(GREATER, Q.columnName("c2", "id"), new LongNumber(152L)))
//                                                                                    .where(Q.op(AND,
//                                                                                            Q.op(EQ, Q.op(MINUS, Q.columnName("c1", "id"), new LongNumber(2L)), Q.columnName("c1", "id")),
//                                                                                            Q.op(EQ, Q.columnName("c1", "id"), Q.op(MINUS, Q.columnName("c2", "id"), new LongNumber(4L)))
//                                                                                    ))
//                                                                                    .build()))
//                                                            )
//                                                    ),
//                                                    Q.op(EXISTS, new SubTable(
//                                                            Query
//                                                                    .from("courses")
//                                                                    .as("c2")
//                                                                    .where(Q.op(GREATER, Q.columnName("c2", "id"), new LongNumber(152L)))
//                                                                    .where(Q.op(AND,
//                                                                            Q.op(EQ, Q.op(MINUS, Q.columnName("c1", "id"), new LongNumber(2L)), Q.columnName("c1", "id")),
//                                                                            Q.op(EQ, Q.columnName("c1", "id"), Q.op(MINUS, Q.columnName("c2", "id"), new LongNumber(4L)))
//                                                                    ))
//                                                                    .build()))
//                                            )
//                                    )
//                                    .build()))
//                    )
//                    .orderBy(List.of(Pair.of(Q.columnName("c", "id"), false)))
//                    .select(Q.columnName("c", "id")),
//            Query
//                    .from("math_elements")
//                    .fullJoin(
//                            Query.from("expression"),
//                            Q.op(EQ, Q.columnName("math_elements", "id"), Q.columnName("expression", "id")),
//                            JoinStrategy.LOOP
//                    )
//    );
//
//    @Test
//    public void cache() {
//
//        for (Query query : testQueries) {
//            var resolverWithProperCache = new Resolver(projection, new CacheContext(CacheStrategy.PROPER));
//            var resolverWithPhonyCache = new Resolver(projection, new CacheContext(CacheStrategy.PHONY));
//            var resolverWithoutCache = new Resolver(projection, new CacheContext(CacheStrategy.NONE));
//
//            var properResolverResult = resolverWithProperCache.resolve(query);
//            var phonyResolverResult = resolverWithPhonyCache.resolve(query);
//            var noneCachedResolverResult = resolverWithoutCache.resolve(query);
//
//            var properCalculator = properResolverResult.calculator();
//            var phonyCalculator = phonyResolverResult.calculator();
//            var noneCachedCalculator = noneCachedResolverResult.calculator();
//
//            Assert.assertEquals(properCalculator.getFullComplexity(), phonyCalculator.getFullComplexity());
//            Assert.assertEquals(properCalculator.getTotalComplexity(), phonyCalculator.getTotalComplexity());
//            Assert.assertEquals(0, noneCachedCalculator.getFullComplexity() - noneCachedCalculator.getTotalComplexity());
//        }
//
//    }

}