package com.anli.sqlexecution.handling;

import java.sql.SQLException;

public interface ResultSetHandler<T> {

    T handle(TransformingResultSet resultSet) throws SQLException;
}
