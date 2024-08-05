package com.core.sqlTool.model.command;

import com.core.sqlTool.model.command.join.JoinStrategy;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.support.JoinStreams;

import java.util.List;
import java.util.stream.Stream;

public final class FullJoin extends JoinCommand {

    public FullJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Stream<Row> run(JoinStreams joinStreams) {
        return Stream.concat(joinStreams.inner(), Stream.concat(joinStreams.leftRemainder(), joinStreams.rightRemainder()));
    }

}
