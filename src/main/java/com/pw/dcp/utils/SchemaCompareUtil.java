package com.pw.dcp.utils;

import java.util.List;

public class SchemaCompareUtil {

    public static class TableSchema {
        private String tableName;
        private List<ColumnSchema> columns;

        public TableSchema(String tableName, List<ColumnSchema> columns) {
            this.tableName = tableName;
            this.columns = columns;
        }

        public String getTableName() {
            return tableName;
        }

        public List<ColumnSchema> getColumns() {
            return columns;
        }
    }

    public static class ColumnSchema {
        private String columnName;
        private String dataType;

        public ColumnSchema(String columnName, String dataType) {
            this.columnName = columnName;
            this.dataType = dataType;
        }

        public String getColumnName() {
            return columnName;
        }

        public String getDataType() {
            return dataType;
        }
    }

    public static void compareTables(TableSchema sourceTable, TableSchema targetTable) {
        // Compare table name
        compareProperty("Table Name", sourceTable.getTableName(), targetTable.getTableName());

        // Compare columns
        List<ColumnSchema> sourceColumns = sourceTable.getColumns();
        List<ColumnSchema> targetColumns = targetTable.getColumns();
        compareColumns(sourceColumns, targetColumns);

        // Add more comparisons as needed
    }

    private static void compareColumns(List<ColumnSchema> sourceColumns, List<ColumnSchema> targetColumns) {
    }

    private static void compareProperty(String propertyName, String sourceValue, String targetValue) {
        if (!sourceValue.equals(targetValue)) {
            System.out.println(propertyName + " does not match: Source - " + sourceValue + ", Target - " + targetValue);
        }
    }
}
