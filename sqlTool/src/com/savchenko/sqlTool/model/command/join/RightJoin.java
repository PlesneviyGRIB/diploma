package com.savchenko.sqlTool.model.command.join;

import com.savchenko.sqlTool.model.command.domain.Command;
import com.savchenko.sqlTool.model.expression.Expression;
import com.savchenko.sqlTool.model.expression.Value;
import com.savchenko.sqlTool.support.JoinStreams;

import java.util.List;
import java.util.stream.Stream;

public class RightJoin extends Join {

    public RightJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Stream<List<Value<?>>> run(JoinStreams joinStreams) {
        return Stream.concat(joinStreams.inner(), joinStreams.rightRemainder());
    }

}