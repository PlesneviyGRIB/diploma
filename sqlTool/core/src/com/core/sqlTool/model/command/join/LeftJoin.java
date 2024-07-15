package com.core.sqlTool.model.command.join;

import com.core.sqlTool.model.command.domain.Command;
import com.core.sqlTool.model.domain.Row;
import com.core.sqlTool.model.expression.Expression;
import com.core.sqlTool.support.JoinStreams;

import java.util.List;
import java.util.stream.Stream;

public class LeftJoin extends JoinCommand {

    public LeftJoin(List<Command> commands, Expression expression, JoinStrategy strategy) {
        super(commands, expression, strategy);
    }

    @Override
    public Stream<Row> run(JoinStreams joinStreams) {
        return Stream.concat(joinStreams.inner(), joinStreams.leftRemainder());
    }

}
