package com.grouk.schoolmark.util;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper for result set of SQL queries
 * Created by Alena on 12.02.2017.
 */
public interface ResultSetMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
