package com.pw.dcp.utils;

import com.pw.dcp.model.DatabaseVO;

import java.sql.*;

public class DatabaseSchemaComparator {


    public static void compareSchemas(DatabaseVO database1, DatabaseVO database2) {
        try (Connection connectionDB1 = DriverManager.getConnection(database1.getJdbcUrl(), database1.getDbName(), database1.getPassword());
             Connection connectionDB2 = DriverManager.getConnection(database2.getJdbcUrl(), database2.getDbName(), database2.getPassword())) {

            String usernameDB1 = database1.getUserName();
            String usernameDB2 = database2.getUserName();
            compareTables(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareTriggers(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareSequences(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareProcedures(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareIndexes(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareForeignKeys(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareViews(connectionDB1, usernameDB1, connectionDB2, usernameDB2);
            compareConstraints(connectionDB1, usernameDB1, connectionDB2, usernameDB2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void compareTables(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT table_name, column_name, data_type, data_length, nullable FROM all_tab_columns WHERE owner = '" + usernameDB1 + "' ORDER BY table_name, column_id"),
                executeQuery(connectionDB2, "SELECT table_name, column_name, data_type, data_length, nullable FROM all_tab_columns WHERE owner = '" + usernameDB2 + "' ORDER BY table_name, column_id")
        );
    }


    private static void compareTriggers(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT trigger_name, table_name, trigger_type, triggering_event FROM all_triggers WHERE table_owner = '" + usernameDB1 + "' ORDER BY trigger_name"),
                executeQuery(connectionDB2, "SELECT trigger_name, table_name, trigger_type, triggering_event FROM all_triggers WHERE table_owner = '" + usernameDB2 + "' ORDER BY trigger_name")
        );
    }

    private static void compareSequences(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT sequence_name, min_value, max_value, increment_by, last_number FROM all_sequences WHERE sequence_owner = '" + usernameDB1 + "' ORDER BY sequence_name"),
                executeQuery(connectionDB2, "SELECT sequence_name, min_value, max_value, increment_by, last_number FROM all_sequences WHERE sequence_owner = '" + usernameDB2 + "' ORDER BY sequence_name")
        );
    }

    private static void compareProcedures(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT object_name, procedure_name, object_type, arguments FROM all_procedures WHERE owner = '" + usernameDB1 + "' ORDER BY object_name, procedure_name"),
                executeQuery(connectionDB2, "SELECT object_name, procedure_name, object_type, arguments FROM all_procedures WHERE owner = '" + usernameDB2 + "' ORDER BY object_name, procedure_name")
        );
    }

    private static void compareIndexes(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT index_name, table_name, uniqueness, column_name FROM all_ind_columns WHERE table_owner = '" + usernameDB1 + "' ORDER BY index_name"),
                executeQuery(connectionDB2, "SELECT index_name, table_name, uniqueness, column_name FROM all_ind_columns WHERE table_owner = '" + usernameDB2 + "' ORDER BY index_name")
        );
    }

    private static void compareForeignKeys(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT constraint_name, table_name, r_constraint_name, delete_rule FROM all_constraints WHERE constraint_type = 'R' AND owner = '" + usernameDB1 + "' ORDER BY table_name, constraint_name"),
                executeQuery(connectionDB2, "SELECT constraint_name, table_name, r_constraint_name, delete_rule FROM all_constraints WHERE constraint_type = 'R' AND owner = '" + usernameDB2 + "' ORDER BY table_name, constraint_name")
        );
    }

    private static void compareViews(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT view_name, text FROM all_views WHERE owner = '" + usernameDB1 + "' ORDER BY view_name"),
                executeQuery(connectionDB2, "SELECT view_name, text FROM all_views WHERE owner = '" + usernameDB2 + "' ORDER BY view_name")
        );
    }

    private static void compareConstraints(Connection connectionDB1, String usernameDB1, Connection connectionDB2, String usernameDB2) throws SQLException {
        compareResults(
                executeQuery(connectionDB1, "SELECT constraint_name, table_name, constraint_type FROM all_constraints WHERE owner = '" + usernameDB1 + "' ORDER BY table_name, constraint_name"),
                executeQuery(connectionDB2, "SELECT constraint_name, table_name, constraint_type FROM all_constraints WHERE owner = '" + usernameDB2 + "' ORDER BY table_name, constraint_name")
        );
    }


    private static void compareResults(ResultSet resultSet1, ResultSet resultSet2) throws SQLException {
        ResultSetMetaData metaData1 = resultSet1.getMetaData();
        ResultSetMetaData metaData2 = resultSet2.getMetaData();

        int columnCount1 = metaData1.getColumnCount();
        int columnCount2 = metaData2.getColumnCount();

        // Check if the number of columns is the same in both result sets
        if (columnCount1 != columnCount2) {
            System.out.println("Number of columns in the result sets is not the same.");
            return;
        }

        // Compare column names
        for (int i = 1; i <= columnCount1; i++) {
            String columnName1 = metaData1.getColumnName(i);
            String columnName2 = metaData2.getColumnName(i);

            if (!columnName1.equals(columnName2)) {
                System.out.println("Column names do not match: " + columnName1 + " and " + columnName2);
                return;
            }
        }

        // Compare data in the result sets
        while (resultSet1.next() && resultSet2.next()) {
            for (int i = 1; i <= columnCount1; i++) {
                Object value1 = resultSet1.getObject(i);
                Object value2 = resultSet2.getObject(i);

                // Add your comparison logic here
                // For simplicity, I'm using toString() for comparison
                if (!value1.toString().equals(value2.toString())) {
                    System.out.println("Data mismatch in column " + metaData1.getColumnName(i) +
                            ": " + value1 + " (DB1) and " + value2 + " (DB2)");
                    return;
                }
            }
        }

        // Check if one result set has more rows than the other
        if (resultSet1.next()) {
            System.out.println("More rows in the first result set.");
        } else if (resultSet2.next()) {
            System.out.println("More rows in the second result set.");
        } else {
            System.out.println("Result sets match.");
        }
    }


    private static ResultSet executeQuery(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            return statement.executeQuery(sql);
        }
    }
}
