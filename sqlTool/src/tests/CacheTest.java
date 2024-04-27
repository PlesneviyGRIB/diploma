package tests;

import com.savchenko.sqlTool.model.cache.CacheContext;
import com.savchenko.sqlTool.model.cache.CacheStrategy;
import com.savchenko.sqlTool.model.command.join.JoinStrategy;
import com.savchenko.sqlTool.model.expression.LongNumber;
import com.savchenko.sqlTool.model.expression.SubTable;
import com.savchenko.sqlTool.model.resolver.Resolver;
import com.savchenko.sqlTool.query.Q;
import com.savchenko.sqlTool.query.Query;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.savchenko.sqlTool.model.operator.Operator.*;

public class CacheTest extends TestBase {

    private final List<Query> testQueries = List.of(
            Query
                    .from("courses")
                    .as("c")
                    .where(Q.op(EXISTS, new SubTable(
                            Query
                                    .from("courses")
                                    .as("c1")
                                    .where(Q.op(OR, Q.op(OR,
                                                            Q.op(EXISTS, new SubTable(
                                                                    Query
                                                                            .from("courses")
                                                                            .as("c2")
                                                                            .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                            .where(Q.op(AND,
                                                                                    Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                                                                                    Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
                                                                            ))
                                                                            .build())),
                                                            Q.op(NOT,
                                                                    Q.op(EXISTS, new SubTable(
                                                                            Query
                                                                                    .from("courses")
                                                                                    .as("c2")
                                                                                    .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                                    .where(Q.op(AND,
                                                                                            Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                                                                                            Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
                                                                                    ))
                                                                                    .build()))
                                                            )
                                                    ),
                                                    Q.op(EXISTS, new SubTable(
                                                            Query
                                                                    .from("courses")
                                                                    .as("c2")
                                                                    .where(Q.op(GREATER, Q.column("c2", "id"), new LongNumber(152L)))
                                                                    .where(Q.op(AND,
                                                                            Q.op(EQ, Q.op(MINUS, Q.column("c1", "id"), new LongNumber(2L)), Q.column("c1", "id")),
                                                                            Q.op(EQ, Q.column("c1", "id"), Q.op(MINUS, Q.column("c2", "id"), new LongNumber(4L)))
                                                                    ))
                                                                    .build()))
                                            )
                                    )
                                    .build()))
                    )
                    .orderBy(List.of(Pair.of(Q.column("c", "id"), false)))
                    .select(Q.column("c", "id")),
            Query
                    .from("math_elements")
                    .fullJoin(
                            Query.from("expression"),
                            Q.op(EQ, Q.column("math_elements", "id"), Q.column("expression", "id")),
                            JoinStrategy.LOOP
                    )
    );

    @Test
    public void cache() {

        for (Query query : testQueries) {
            var resolverWithProperCache = new Resolver(projection, new CacheContext(CacheStrategy.PROPER));
            var resolverWithPhonyCache = new Resolver(projection, new CacheContext(CacheStrategy.PHONY));
            var resolverWithoutCache = new Resolver(projection, new CacheContext(CacheStrategy.NONE));

            var properResolverResult = resolverWithProperCache.resolve(query);
            var phonyResolverResult = resolverWithPhonyCache.resolve(query);
            var noneCachedResolverResult = resolverWithoutCache.resolve(query);

            var properCalculator = properResolverResult.calculator();
            var phonyCalculator = phonyResolverResult.calculator();
            var noneCachedCalculator = noneCachedResolverResult.calculator();

            Assert.assertEquals(properCalculator.getFullComplexity(), phonyCalculator.getFullComplexity());
            Assert.assertEquals(properCalculator.getTotalComplexity(), phonyCalculator.getTotalComplexity());
            Assert.assertEquals(0, noneCachedCalculator.getFullComplexity() - noneCachedCalculator.getTotalComplexity());
        }

    }

}