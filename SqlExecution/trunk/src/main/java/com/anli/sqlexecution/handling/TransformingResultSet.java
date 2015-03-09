package com.anli.sqlexecution.handling;

import com.anli.sqlexecution.transformation.SqlTransformer;
import com.anli.sqlexecution.transformation.TransformerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransformingResultSet {

    protected ResultSet resultSet;
    protected TransformerFactory transformerFactory;

    public TransformingResultSet(ResultSet resultSet, TransformerFactory transformerFactory) {
        this.resultSet = resultSet;
        this.transformerFactory = transformerFactory;
    }

    public boolean next() throws SQLException {
        return resultSet.next();
    }

    public boolean previous() throws SQLException {
        return resultSet.previous();
    }

    public <T> T getValue(int columnIndex, Class<T> javaClass) throws SQLException {
        SqlTransformer transformer = transformerFactory != null
                ? transformerFactory.getTransformer(javaClass) : null;
        Class<?> sqlClass = transformer != null ? transformer.getSqlClass() : javaClass;
        Object sqlValue = resultSet.getObject(columnIndex, sqlClass);
        if (resultSet.wasNull()) {
            sqlValue = null;
        }
        return (T) (transformer != null ? transformer.toJava(sqlValue) : sqlValue);
    }

    public <T> T getValue(String columnLabel, Class<T> javaClass) throws SQLException {
        SqlTransformer transformer = transformerFactory != null
                ? transformerFactory.getTransformer(javaClass) : null;
        Class<?> sqlClass = transformer != null ? transformer.getSqlClass() : javaClass;
        Object sqlValue = resultSet.getObject(columnLabel, sqlClass);
        if (resultSet.wasNull()) {
            return null;
        }
        return (T) (transformer != null ? transformer.toJava(sqlValue) : sqlValue);
    }
}
