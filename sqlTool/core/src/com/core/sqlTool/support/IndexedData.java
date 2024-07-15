package com.core.sqlTool.support;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public record IndexedData<Data>(Integer index, Data data) {

    public static <Data> Stream<IndexedData<Data>> indexed(Stream<Data> dataStream) {
        var index = new AtomicInteger();
        return dataStream.map(data -> new IndexedData<>(index.getAndIncrement(), data));
    }

}

