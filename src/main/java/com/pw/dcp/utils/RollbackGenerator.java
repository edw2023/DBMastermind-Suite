package com.pw.dcp.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RollbackGenerator {

    private static List<String> executedDDLStatements = new ArrayList<>();

    public static void main(String[] args) {
        String originalAlterTableAddColumnSql = "ALTER TABLE your_table ADD COLUMN new_column INT";
        // Add more test cases...

        try {
            String rollbackAlterTableAddColumnSql = generateRollbackSQL(originalAlterTableAddColumnSql);
            // Add more test cases...

            System.out.println("Original SQL: " + originalAlterTableAddColumnSql);
            System.out.println("Rollback SQL: " + rollbackAlterTableAddColumnSql);
            // Add more print statements for other test cases...

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }

    private static String generateRollbackSQL(String originalSql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(originalSql);

        if (statement instanceof Alter) {
            Alter alterStatement = (Alter) statement;
            executedDDLStatements.add(originalSql);
            return getRollbackForAlterType(alterStatement);
        } else if (statement instanceof Drop) {
            Drop dropStatement = (Drop) statement;
            executedDDLStatements.add(originalSql);
            return getRollbackForDropType(dropStatement);
        } else if (statement instanceof Truncate) {
            Truncate truncateStatement = (Truncate) statement;
            executedDDLStatements.add(originalSql);
            return generateRollbackForTruncate(truncateStatement);
        } else if (statement instanceof CreateTable) {
            CreateTable createStatement = (CreateTable) statement;
            executedDDLStatements.add(originalSql);
            return getRollbackForCreateType(createStatement);
        } else if (statement instanceof Update) {
            Update updateStatement = (Update) statement;
            executedDDLStatements.add(originalSql);
            return getRollbackForUpdateType(updateStatement);
        }

        return "Unsupported DDL statement for rollback";
    }

    private static String getRollbackForAlterType(Alter alterStatement) {
        String alterType = alterStatement.getAlterExpressions().get(0).toString();
        switch (alterType.toUpperCase()) {
            case "ADD COLUMN":
                return generateRollbackForAddColumn(alterStatement);
            case "DROP COLUMN":
                return generateRollbackForDropColumn(alterStatement);
            case "MODIFY COLUMN":
                return generateRollbackForModifyColumn(alterStatement);
            case "RENAME COLUMN":
                return generateRollbackForRenameColumn(alterStatement);
            case "ADD CONSTRAINT":
                return generateRollbackForAddConstraint(alterStatement);
            case "DROP CONSTRAINT":
                return generateRollbackForDropConstraint(alterStatement);
            case "ADD INDEX":
                return generateRollbackForAddIndex(alterStatement);
            case "DROP INDEX":
                return generateRollbackForDropIndex(alterStatement);
            case "RENAME TABLE":
                return generateRollbackForRenameTable(alterStatement);
            case "MODIFY COLUMN TYPE":
                return generateRollbackForModifyColumnType(alterStatement);
            // Add more cases for other ALTER types...
            default:
                return "Unsupported ALTER type for rollback";
        }
    }

    private static String generateRollbackForAddColumn(Alter alterStatement) {
        // Implement rollback logic for adding a column
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " DROP COLUMN " + alterStatement.getAlterExpressions().get(0) + ";";
    }

    private static String generateRollbackForDropColumn(Alter alterStatement) {
        // Implement rollback logic for dropping a column
        // You might need to store the column definition before dropping for rollback
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " ADD COLUMN " + getRollbackColumnDefinition(alterStatement) + ";";
    }

    private static String generateRollbackForModifyColumn(Alter alterStatement) {
        // Implement rollback logic for modifying a column
        // You might need to store the original column definition before modifying for rollback
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " MODIFY COLUMN " + getRollbackColumnDefinition(alterStatement) + ";";
    }

    private static String generateRollbackForRenameColumn(Alter alterStatement) {
        // Implement rollback logic for renaming a column
        // You might need to store the original column name before renaming for rollback
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " RENAME COLUMN " + alterStatement.getAlterExpressions().get(0) + " TO " +
                getRollbackColumnRename(alterStatement) + ";";
    }

    private static String getRollbackColumnRename(Alter alterStatement) {
        // Extract and return the original column name from the ALTER statement
        return "original_column_name";
    }

    private static String generateRollbackForAddConstraint(Alter alterStatement) {
        // Implement rollback logic for adding a constraint
        // You might need to store the original constraint definition before adding for rollback
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " DROP CONSTRAINT " + alterStatement.getAlterExpressions().get(0) + ";";
    }

    private static String getRollbackColumnDefinition(Alter alterStatement) {
        // Extract and return the column definition from the ALTER statement
        // This is a simplified example, you might need to handle data types, constraints, etc.
        return alterStatement.getAlterExpressions().get(0).toString();
    }

    private static String getRollbackForDropType(Drop dropStatement) {
        // Implement rollback logic for DROP TABLE and DROP INDEX
        if (dropStatement.getType().equalsIgnoreCase("TABLE")) {
            return generateRollbackForDropTable(dropStatement);
        } else if (dropStatement.getType().equalsIgnoreCase("INDEX")) {
            return generateRollbackForDropIndex(dropStatement);
        }
        return "Unsupported DROP type for rollback";
    }

    private static String generateRollbackForDropTable(Drop dropStatement) {
        // Implement rollback logic for dropping a table
        // You might need to store the table definition before dropping for rollback
        return "CREATE TABLE " + dropStatement.getName() + ";";
    }

    private static String generateRollbackForDropIndex(Drop dropStatement) {
        // Implement rollback logic for dropping an index
        // You might need to store the index definition before dropping for rollback
        return "CREATE INDEX " + dropStatement.getName() + ";";
    }

    private static String generateRollbackForTruncate(Truncate truncateStatement) {
        // Implement rollback logic for truncating a table
        // You might need to store the table data before truncating for rollback
        return "INSERT INTO " + truncateStatement.getTable().getName() +
                " SELECT * FROM backup_" + truncateStatement.getTable().getName() + ";";
    }

    private static String generateRollbackForDropConstraint(Alter alterStatement) {
        // Implement rollback logic for dropping a constraint
        // You might need to store the original constraint definition before dropping for rollback
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " ADD CONSTRAINT " + alterStatement.getAlterExpressions().get(0) + ";";
    }

    private static String generateRollbackForAddIndex(Alter alterStatement) {
        // Implement rollback logic for adding an index
        // You might need to store the original index definition before adding for rollback
        return "DROP INDEX " + alterStatement.getAlterExpressions().get(0) +
                " ON " + alterStatement.getTable().getName() + ";";
    }

    private static String generateRollbackForDropIndex(Alter alterStatement) {
        // Implement rollback logic for dropping an index
        // You might need to store the original index definition before dropping for rollback
        return "CREATE INDEX " + alterStatement.getAlterExpressions().get(0) +
                " ON " + alterStatement.getTable().getName() + ";";
    }

    private static String generateRollbackForRenameTable(Alter alterStatement) {
        // Implement rollback logic for renaming a table
        // You might need to store the original table name before renaming for rollback
        return "RENAME TABLE " + alterStatement.getTable().getName() +
                " TO " + getRollbackTableRename(alterStatement) + ";";
    }

    private static String getRollbackTableRename(Alter alterStatement) {
        // Extract and return the original table name from the ALTER statement
        return "original_table_name";
    }

    private static String generateRollbackForModifyColumnType(Alter alterStatement) {
        // Implement rollback logic for modifying column type
        // You might need to store the original column type before modifying for rollback
        return "ALTER TABLE " + alterStatement.getTable().getName() +
                " MODIFY COLUMN " + alterStatement.getAlterExpressions().get(0) + ";";
    }


    private static String getRollbackForCreateType(CreateTable createStatement) {
        String createType = createStatement.getTable().getName();
        switch (createType.toUpperCase()) {
            case "TABLE":
                return generateRollbackForCreateTable(createStatement);
            case "INDEX":
                return generateRollbackForCreateIndex(createStatement);
            // Add more cases for other CREATE types...
            default:
                return "Unsupported CREATE type for rollback";
        }
    }

    private static String generateRollbackForCreateTable(CreateTable createStatement) {
        // Implement rollback logic for creating a table
        // You might need to store the original table definition before creating for rollback
        return "DROP TABLE " + createStatement.getTable().getName() + ";";
    }

    private static String generateRollbackForCreateIndex(CreateTable createStatement) {
        // Implement rollback logic for creating an index
        // You might need to store the original index definition before creating for rollback
        return "DROP INDEX " + createStatement.getTable().getName() +
                "." + createStatement.getIndexes().get(0).getName() + ";";
    }

    private static String getRollbackForUpdateType(Update updateStatement) {
        Table table = updateStatement.getTable();
        if (table != null ) {
            String tableName = table.getName();
            List<Column> columns = updateStatement.getColumns();
            if (columns != null && !columns.isEmpty()) {
                return generateRollbackForUpdate(updateStatement, tableName, columns);
            }
        }
        return "Unsupported UPDATE type for rollback";
    }

    private static String generateRollbackForUpdate(Update updateStatement, String tableName, List<Column> columns) {
        StringBuilder rollbackSql = new StringBuilder("UPDATE " + tableName + " SET ");
        rollbackSql.append(
                columns.stream()
                        .map(column -> String.format("%s = %s", column.getColumnName(), getRollbackColumnValue(column)))
                        .collect(Collectors.joining(", "))
        );
        rollbackSql.append(" WHERE ").append(updateStatement.getWhere().toString()).append(";");
        return rollbackSql.toString();
    }

    private static String getRollbackColumnValue(Column column) {
        // Implement logic to get the rollback value for the column
        // You might need to store the original values before updating for rollback
        return "original_" + column.getColumnName();
    }





}
