package com.anli.sqlexecution.transformation;

public interface TransformerFactory {

    <J> SqlTransformer<J, ?> getTransformer(Class<? extends J> javaClass);
}
