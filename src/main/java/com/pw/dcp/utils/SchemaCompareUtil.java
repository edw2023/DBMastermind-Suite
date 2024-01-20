package com.pw.dcp.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchemaCompareUtil {

    private static final String JDBC_URL = "jdbc:oracle:thin:@your_oracle_host:your_oracle_port:your_sid";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        compareOracleSchemas("source_schema", "target_schema");
    }

    public static void compareOracleSchemas(String sourceSchema, String targetSchema) {
        try (Connection sourceConnection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Connection targetConnection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            List<SchemaCompareUtil.TableSchema> sourceTables = getTablesMetadata(sourceConnection, sourceSchema);
            List<SchemaCompareUtil.TableSchema> targetTables = getTablesMetadata(targetConnection, targetSchema);

            compareTables(sourceTables, targetTables);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<SchemaCompareUtil.TableSchema> getTablesMetadata(Connection connection, String schema) throws SQLException {
        List<SchemaCompareUtil.TableSchema> tables = new ArrayList<>();

        try (ResultSet resultSet = connection.getMetaData().getTables(null, schema, null, new String[]{"TABLE"})) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                List<SchemaCompareUtil.ColumnSchema> columns = getColumnsMetadata(connection, schema, tableName);
                SchemaCompareUtil.TableSchema tableSchema = new SchemaCompareUtil.TableSchema(tableName, columns);
                tables.add(tableSchema);
            }
        }

        return tables;
    }

    private static List<SchemaCompareUtil.ColumnSchema> getColumnsMetadata(Connection connection, String schema, String tableName) throws SQLException {
        List<SchemaCompareUtil.ColumnSchema> columns = new ArrayList<>();

        try (ResultSet resultSet = connection.getMetaData().getColumns(null, schema, tableName, null)) {
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("TYPE_NAME");
                SchemaCompareUtil.ColumnSchema columnSchema = new SchemaCompareUtil.ColumnSchema(columnName, dataType);
                columns.add(columnSchema);
            }
        }

        return columns;
    }

    private static void compareTables(List<SchemaCompareUtil.TableSchema> sourceTables, List<SchemaCompareUtil.TableSchema> targetTables) {
        for (SchemaCompareUtil.TableSchema sourceTable : sourceTables) {
            SchemaCompareUtil.TableSchema matchingTargetTable = findMatchingTable(sourceTable, targetTables);
            if (matchingTargetTable != null) {
                SchemaCompareUtil.compareTables(sourceTable, matchingTargetTable);
            } else {
                System.out.println("Table " + sourceTable.getTableName() + " not found in target schema");
            }
        }

        // Check for tables in target schema that are not present in source schema
        for (SchemaCompareUtil.TableSchema targetTable : targetTables) {
            SchemaCompareUtil.TableSchema matchingSourceTable = findMatchingTable(targetTable, sourceTables);
            if (matchingSourceTable == null) {
                System.out.println("Table " + targetTable.getTableName() + " not found in source schema");
            }
        }
    }

    private static SchemaCompareUtil.TableSchema findMatchingTable(SchemaCompareUtil.TableSchema table, List<SchemaCompareUtil.TableSchema> tables) {
        return tables.stream().filter(t -> t.getTableName().equalsIgnoreCase(table.getTableName())).findFirst().orElse(null);
    }
}

