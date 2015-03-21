package com.anli.sqlexecution.execution;

import com.anli.sqlexecution.handling.ResultSetHandler;
import com.anli.sqlexecution.exceptions.DatabaseException;
import com.anli.sqlexecution.handling.TransformingResultSet;
import com.anli.sqlexecution.transformation.SqlTransformer;
import com.anli.sqlexecution.transformation.TransformerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SqlExecutor.class);

    protected final DataSource source;
    protected final TransformerFactory transformerFactory;

    public SqlExecutor(DataSource source, TransformerFactory transformerFactory) {
        this.source = source;
        this.transformerFactory = transformerFactory;
    }

    public <T> T executeSelect(String query, List parameters, ResultSetHandler<T> resultSetHandler) {
        try {
            T result;
            try (Connection connection = getConnection();
                    PreparedStatement statement = connection.prepareStatement(query)) {
                setParameters(statement, parameters);
                try (ResultSet resultSet = statement.executeQuery()) {
                    result = resultSetHandler.handle(wrapResultSet(resultSet));
                }
            }
            return result;
        } catch (SQLException sqlException) {
            LOG.error("Sql exception for query " + query + "and parameters " + parameters, sqlException);
            throw new DatabaseException(sqlException);
        }
    }

    public int executeUpdate(String query, List parameters) {
        try {
            int result;
            try (Connection connection = getConnection();
                    PreparedStatement statement = connection.prepareStatement(query)) {
                setParameters(statement, parameters);
                result = statement.executeUpdate();
            }
            return result;
        } catch (SQLException sqlException) {
            LOG.error("Sql exception for query " + query + "and parameters " + parameters, sqlException);
            throw new DatabaseException(sqlException);
        }
    }

    protected Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    protected TransformingResultSet wrapResultSet(ResultSet resultSet) {
        return new TransformingResultSet(resultSet, transformerFactory);
    }

    protected void setParameters(PreparedStatement statement, List parameters) throws SQLException {
        if (parameters == null) {
            return;
        }
        int index = 1;
        for (Object parameter : parameters) {
            statement.setObject(index, transformValue(parameter));
            index++;
        }
    }

    protected Object transformValue(Object javaValue) {
        if (javaValue == null) {
            return null;
        }
        SqlTransformer transformer = transformerFactory != null
                ? transformerFactory.getTransformer(javaValue.getClass()) : null;
        return transformer != null ? transformer.toSql(javaValue) : javaValue;
    }
}
