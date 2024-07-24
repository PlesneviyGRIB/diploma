package com.core.sqlTool.utils;

import com.client.sqlTool.command.*;
import com.client.sqlTool.domain.AggregationType;
import com.client.sqlTool.domain.JoinStrategy;
import com.client.sqlTool.expression.Number;
import com.client.sqlTool.expression.*;
import com.core.sqlTool.exception.UnexpectedException;
import com.core.sqlTool.exception.UnnamedExpressionException;
import com.core.sqlTool.model.command.Command;
import com.core.sqlTool.model.command.*;
import com.core.sqlTool.model.command.aggregation.*;
import com.core.sqlTool.model.command.join.*;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.model.expression.*;
import com.core.sqlTool.model.index.BitmapIndex;
import com.core.sqlTool.model.index.HashIndex;
import com.core.sqlTool.model.index.Index;
import com.core.sqlTool.model.index.TreeIndex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record DtoToModelConverter() {

    public List<Command> convert(List<com.client.sqlTool.command.Command> dtoCommands) {
        return dtoCommands.stream()
                .map(dtoCommand -> {

                    if (dtoCommand instanceof Alias dtoAlias) {
                        return new TableAliasCommand(dtoAlias.alias());
                    }

                    if (dtoCommand instanceof ConstructIndex dtoConstructIndex) {
                        return new ConstructIndexCommand(convertIndex(dtoConstructIndex.index(), dtoConstructIndex.columns()));
                    }

                    if (dtoCommand instanceof Distinct) {
                        return new DistinctCommand();
                    }

                    if (dtoCommand instanceof From dtoFrom) {
                        return new FromCommand(dtoFrom.tableName());
                    }

                    if (dtoCommand instanceof GroupBy dtoGroupBy) {
                        return new GroupByCommand(
                                dtoGroupBy.expressions().stream()
                                        .map(this::convertExpression)
                                        .toList(),
                                dtoGroupBy.aggregations().stream()
                                        .map(group -> Pair.of(convertExpression(group.getExpression()), convertAggregation(group.getAggregationType())))
                                        .toList()
                        );
                    }

                    if (dtoCommand instanceof Join dtoJoin) {
                        return convertJoin(dtoJoin);
                    }

                    if (dtoCommand instanceof Limit dtoLimit) {
                        return new LimitCommand(dtoLimit.limit());
                    }

                    if (dtoCommand instanceof Offset dtoOffset) {
                        return new OffsetCommand(dtoOffset.offset());
                    }

                    if (dtoCommand instanceof OrderBy dtoOrderBy) {
                        return new OrderByCommand(dtoOrderBy.orders().stream()
                                .map(order -> Pair.of(convertColumn(order.getColumn()), order.isAsc()))
                                .toList()
                        );
                    }

                    if (dtoCommand instanceof Select dtoSelect) {
                        return new SelectCommand(dtoSelect.expressions().stream().map(this::convertExpression).toList());
                    }

                    if (dtoCommand instanceof Where dtoWhere) {
                        return new WhereCommand(convertExpression(dtoWhere.expression()));
                    }

                    throw new UnexpectedException();

                })
                .toList();
    }

    private Index convertIndex(com.client.sqlTool.command.Index dtoIndex, List<com.client.sqlTool.domain.Column> dtoColumns) {

        var columns = dtoColumns.stream().map(this::convertColumn).toList();

        return switch (dtoIndex.getIndexType()) {
            case HASH -> new HashIndex(dtoIndex.getIndexName(), columns);
            case BALANCED_TREE -> new TreeIndex(dtoIndex.getIndexName(), columns);
            case BITMAP -> new BitmapIndex(dtoIndex.getIndexName(), columns);
        };
    }

    private AggregationFunction convertAggregation(AggregationType aggregationType) {
        return switch (aggregationType) {
            case MAX -> new Max();
            case MIN -> new Min();
            case AVERAGE -> new Average();
            case COUNT -> new Count();
            case SUM -> new Sum();
        };
    }

    private Column convertColumn(com.client.sqlTool.domain.Column dtoColumn) {
        var pair = DtoUtils.parseTableAndColumnNames(dtoColumn);
        return new Column(pair.getLeft(), pair.getRight(), null);
    }

    private JoinCommand convertJoin(Join dtoJoin) {

        var commands = this.convert(dtoJoin.commands());
        var expression = convertExpression(dtoJoin.expression());

        Function<JoinStrategy, com.core.sqlTool.model.command.join.JoinStrategy> strategyResolver = strategy -> switch (strategy) {
            case HASH -> new HashJoinStrategy();
            case MERGE -> new MergeJoinStrategy();
            case LOOP -> new LoopJoinStrategy();
        };

        return switch (dtoJoin.type()) {
            case INNER -> new InnerJoin(commands, expression, strategyResolver.apply(dtoJoin.strategy()));
            case LEFT -> new LeftJoin(commands, expression, strategyResolver.apply(dtoJoin.strategy()));
            case RIGHT -> new RightJoin(commands, expression, strategyResolver.apply(dtoJoin.strategy()));
            case FULL -> new FullJoin(commands, expression, strategyResolver.apply(dtoJoin.strategy()));
        };
    }

    private Expression convertExpression(com.client.sqlTool.expression.Expression dtoExpression) {

        if (Objects.isNull(dtoExpression.getExpressionName())) {
            throw new UnnamedExpressionException(convertExpressionInternal(dtoExpression));
        }

        return new NamedExpression(convertExpressionInternal(dtoExpression), dtoExpression.getExpressionName());
    }

    private Expression convertExpressionInternal(com.client.sqlTool.expression.Expression dtoExpression) {

        if (dtoExpression instanceof Bool dtoBool) {
            return new BooleanValue(dtoBool.isValue());
        }

        if (dtoExpression instanceof FloatNumber dtoFloatNumber) {
            return new FloatNumberValue(dtoFloatNumber.getValue());
        }

        if (dtoExpression instanceof Null) {
            return new NullValue();
        }

        if (dtoExpression instanceof Number dtoNumber) {
            return new NumberValue(dtoNumber.getValue());
        }

        if (dtoExpression instanceof Str dtoStr) {
            return new StringValue(dtoStr.getValue());
        }

        if (dtoExpression instanceof TimeStamp dtoTimeStamp) {
            return new TimestampValue(dtoTimeStamp.getTimestamp());
        }

        if (dtoExpression instanceof Unary dtoUnary) {
            return new UnaryOperation(dtoUnary.getOperator(), convertExpressionInternal(dtoUnary.getExpression()));
        }

        if (dtoExpression instanceof Binary dtoBinary) {
            return new BinaryOperation(dtoBinary.getOperator(), convertExpressionInternal(dtoBinary.getLeft()), convertExpressionInternal(dtoBinary.getRight()));
        }

        if (dtoExpression instanceof Ternary dtoTernary) {
            return new TernaryOperation(dtoTernary.getOperator(),
                    convertExpressionInternal(dtoTernary.getFirst()),
                    convertExpressionInternal(dtoTernary.getSecond()),
                    convertExpressionInternal(dtoTernary.getThird())
            );
        }

        if (dtoExpression instanceof com.client.sqlTool.domain.Column dtoColumn) {
            return convertColumn(dtoColumn);
        }

        throw new UnexpectedException();

    }

}
