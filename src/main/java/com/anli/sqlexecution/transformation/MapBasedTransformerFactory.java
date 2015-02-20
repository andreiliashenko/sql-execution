package com.anli.sqlexecution.transformation;

import java.util.HashMap;
import java.util.Map;

public class MapBasedTransformerFactory implements TransformerFactory {

    protected final Map<Class, SqlTransformer> transformerMap;

    public MapBasedTransformerFactory(Map<Class, SqlTransformer> map) {
        this.transformerMap = map;
    }

    public MapBasedTransformerFactory() {
        this.transformerMap = new HashMap<>();
    }

    public void add(Class<?> javaClass, SqlTransformer<?, ?> transformer) {
        transformerMap.put(javaClass, transformer);
    }

    @Override
    public <J> SqlTransformer<J, ?> getTransformer(Class<? extends J> javaClass) {
        return transformerMap.get(javaClass);
    }
}
