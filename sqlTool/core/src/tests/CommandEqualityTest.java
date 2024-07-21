package tests;

import com.core.sqlTool.model.command.*;
import com.core.sqlTool.model.command.function.*;
import com.core.sqlTool.model.command.join.*;
import com.core.sqlTool.model.domain.Column;
import com.core.sqlTool.model.expression.BooleanValue;
import com.core.sqlTool.model.expression.NumberValue;
import com.core.sqlTool.model.expression.UnaryOperation;
import com.core.sqlTool.model.index.BitmapIndex;
import com.core.sqlTool.model.index.HashIndex;
import com.core.sqlTool.model.index.TreeIndex;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.List;

import static com.client.sqlTool.expression.Operator.NOT;
import static org.junit.Assert.assertEquals;

public class CommandEqualityTest {

    @Test
    public void commandEquality() {

        var commands1 = List.of(
                new TableAliasCommand("table"),
                new ConstructIndexCommand(new HashIndex("hash", List.of())),
                new DistinctCommand(),
                new FromCommand("table"),
                new GroupByCommand(List.of(Pair.of(new Column("table", "id", NumberValue.class), new Sum()))),
                new LimitCommand(1),
                new OffsetCommand(1),
                new OrderByCommand(List.of(Pair.of(new Column("table", "id", NumberValue.class), false))),
                new SelectCommand(List.of(new Column("table", "id", NumberValue.class), new BooleanValue(true))),
                new WhereCommand(new UnaryOperation(NOT, new BooleanValue(false))),
                new FullJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy()),
                new InnerJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy()),
                new LeftJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy()),
                new RightJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy())
        );

        var commands2 = List.of(
                new TableAliasCommand("table"),
                new ConstructIndexCommand(new HashIndex("hash", List.of())),
                new DistinctCommand(),
                new FromCommand("table"),
                new GroupByCommand(List.of(Pair.of(new Column("table", "id", NumberValue.class), new Sum()))),
                new LimitCommand(1),
                new OffsetCommand(1),
                new OrderByCommand(List.of(Pair.of(new Column("table", "id", NumberValue.class), false))),
                new SelectCommand(List.of(new Column("table", "id", NumberValue.class), new BooleanValue(true))),
                new WhereCommand(new UnaryOperation(NOT, new BooleanValue(false))),
                new FullJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy()),
                new InnerJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy()),
                new LeftJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy()),
                new RightJoin(List.of(new FromCommand("table")), new BooleanValue(true), new LoopJoinStrategy())
        );

        assertEquals(commands1, commands2);
    }

    @Test
    public void hashIndexEquality() {

        var index1 = new HashIndex("hash", List.of(new Column("entity", "id", NumberValue.class)));
        var index2 = new HashIndex("hash", List.of(new Column("entity", "id", NumberValue.class)));

        assertEquals(index1, index2);
    }

    @Test
    public void treeIndexEquality() {

        var index1 = new TreeIndex("tree", List.of(new Column("entity", "id", NumberValue.class)));
        var index2 = new TreeIndex("tree", List.of(new Column("entity", "id", NumberValue.class)));

        assertEquals(index1, index2);
    }

    @Test
    public void bitmapIndexEquality() {

        var index1 = new BitmapIndex("bitmap", List.of(new Column("entity", "id", NumberValue.class)));
        var index2 = new BitmapIndex("bitmap", List.of(new Column("entity", "id", NumberValue.class)));

        assertEquals(index1, index2);
    }

    @Test
    public void joinStrategyEquality() {

        var list1 = List.of(new HashJoinStrategy(), new MergeJoinStrategy(), new LoopJoinStrategy());
        var list2 = List.of(new HashJoinStrategy(), new MergeJoinStrategy(), new LoopJoinStrategy());

        assertEquals(list1, list2);
    }

    @Test
    public void aggregationFunctionEquality() {

        var list1 = List.of(new Average(), new Count(), new Max(), new Min(), new Sum());
        var list2 = List.of(new Average(), new Count(), new Max(), new Min(), new Sum());

        assertEquals(list1, list2);
    }

}
