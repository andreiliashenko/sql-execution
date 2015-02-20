package com.anli.sqlexecution.transformation;

public interface SqlTransformer<J, S> {

    S toSql(J source);

    J toJava(S source);

    Class<? extends J> getJavaClass();

    Class<? extends S> getSqlClass();
}
