package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.domain.Row;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.support.JoinStreams;

import java.util.List;
import java.util.stream.Stream;

public class LeftJoin extends Join {

    public LeftJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Stream<Row> run(JoinStreams joinStreams) {
        return Stream.concat(joinStreams.inner(), joinStreams.leftRemainder());
    }

}
