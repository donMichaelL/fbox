package org.jdbc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
	
	public static void close(ResultSet resultSet) {
		if(resultSet != null) {
			try {
				resultSet.close();
			} catch(SQLException e) {
				// Do some exception-logging here
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Statement statement) {
		if(statement != null) {
			try {
				statement.close();
			} catch(SQLException e) {
				// Do some exception-logging here
				e.printStackTrace();
			}
		}
	}
	
	public static void close(Connection connection) {
		if(connection != null) {
			try {
				connection.close();
			} catch(SQLException e) {
				// Do some exception-logging here
				e.printStackTrace();
			}
		}
	}

}
