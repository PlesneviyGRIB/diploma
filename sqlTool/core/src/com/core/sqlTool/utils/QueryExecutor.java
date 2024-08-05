package com.core.sqlTool.utils;

import com.client.sqlTool.query.Query;
import com.core.sqlTool.config.Constants;
import com.core.sqlTool.model.cache.CacheContext;
import com.core.sqlTool.model.cache.CacheStrategy;
import com.core.sqlTool.model.complexity.Calculator;
import com.core.sqlTool.model.domain.ExternalHeaderRow;
import com.core.sqlTool.model.domain.Table;
import com.core.sqlTool.model.resolver.Resolver;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.DriverManager;
import java.sql.SQLException;

public record QueryExecutor(Query query) {

    public Pair<Table, Calculator> execute() throws SQLException {

        // sqlite
        //var connection = DriverManager.getConnection("jdbc:sqlite:/sqlTool/database/sqlite/db_from_course.sqlite3");

        // postgres
        var connection = DriverManager.getConnection(String.format("jdbc:postgresql://localhost:%s/%s", Constants.DB_PORT, Constants.DB_NAME), Constants.DB_USER, Constants.DB_PASSWORD);
        var projection = new DatabaseReader(connection).read();

        var modelCommands = new DtoToModelConverter().convert(query.getCommands());
        var cacheContext = new CacheContext(CacheStrategy.NONE);

        var resolverResult = new Resolver(projection, cacheContext).resolve(modelCommands, ExternalHeaderRow.empty());
        // stream consumer
        var table = resolverResult.lazyTable().fetch();

        return Pair.of(table, resolverResult.calculator());
    }


}
