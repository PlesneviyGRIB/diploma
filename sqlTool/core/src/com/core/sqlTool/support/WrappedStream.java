package com.core.sqlTool.support;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class WrappedStream<Data> {

    private final Stream<Data> stream;

    private final List<Data> cachedData = new LinkedList<>();

    private boolean consumed;

    public WrappedStream(Stream<Data> stream) {
        this.stream = stream;
    }

    public Stream<Data> getStream() {
        if (consumed) {
            return cachedData.stream();
        }
        consumed = true;
        return stream.peek(cachedData::add);
    }
}