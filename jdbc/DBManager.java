package it.centro.iot.shelly.mqtt.jdbc;

import java.sql.*;
import java.util.TimeZone;

/**
 * Class for managing DB connections.
 * Supports any JDBC Connection as long as the proper driver and connection string are provided.
 *
 * @author Riccardo Prevedi
 * @created 02/10/2022 - 15:17
 * @project db-manager
 */

public class DBManager {

    public static final String JDBC_Driver_SQLite = "org.sqlite.JDBC";
    public static final String JDBCURLSQLite = "jdbc:sqlite:test.db";

    public static final String JDBCDriverMySQL = "com.mysql.cj.jdbc.Driver";
    public static final String JDBCURLMySQL = "jdbc:mysql://<your_ip>:3306/<db_name>?user=<user>&password=<password>" ;

    protected Statement statement;
    protected Connection connection;

    public DBManager(String JDBCDriver, String JDBCURL) throws ClassNotFoundException, SQLException {

        Class.forName(JDBCDriver);
        connection = DriverManager.getConnection(JDBCURL);
        statement = connection.createStatement();
        //statement.setQueryTimeout(30);
    }

    public DBManager(String JDBCDriver, String JDBCURL, int resultSetType, int resultSetConcurrency) throws SQLException, ClassNotFoundException {

        Class.forName(JDBCDriver);
        connection = DriverManager.getConnection(JDBCURL);
        statement = connection.createStatement(resultSetType, resultSetConcurrency);
        //statement.setQueryTimeout(30);
    }

    public ResultSet executeQuery(String query) throws SQLException {
        return statement.executeQuery(query);
    }

    public int executeUpdate(String query) throws SQLException {
        return statement.executeUpdate(query);
    }

    public void close() throws SQLException {
        if (connection != null) {
            statement.close();
            connection.close();
        }
    }
}
