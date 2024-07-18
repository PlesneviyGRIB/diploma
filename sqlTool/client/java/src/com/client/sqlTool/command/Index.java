package com.client.sqlTool.command;

import com.client.sqlTool.domain.IndexType;
import lombok.Getter;

@Getter
public class Index {

    private final String indexName;

    private final IndexType indexType;

    private Index(String indexName, IndexType indexType) {
        this.indexName = indexName;
        this.indexType = indexType;
    }

    public static Index of(String indexName, IndexType indexType) {
        return new Index(indexName, indexType);
    }

}
