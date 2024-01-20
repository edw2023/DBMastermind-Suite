package com.pw.dcp.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLValidator {

    public static final String WHERE_CLAUSE = "WHERE";

    /*
     * Validate SQL syntax
     * Database Type: Oracle, SQL Server, MySQL
     * */
    public String checkSyntax(String sql) throws JSQLParserException {
            Statement statement = CCJSqlParserUtil.parse(sql);
            return statement.toString();
    }

    public void checkSecurity(String sql) throws Exception {
        Statement statement = CCJSqlParserUtil.parse(sql);
        checkTableName(sql);
        checkStatementType(statement);
    }

    private void checkStatementType(Statement statement) throws Exception {
        if (statement instanceof Update || statement instanceof Delete) {
            checkClause(statement.toString(), WHERE_CLAUSE, true);
        } else if (statement instanceof Drop || statement instanceof Truncate) {
            // Forbidden
            throw new Exception("Operation forbidden");
        }
    }

    private void checkClause(String sql, String keyword, boolean required) {
        // 使用正则表达式进行检查
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find() != required) {
            System.out.println("Clause validation failed: " + keyword);
        }
    }

    private static void checkTableName(String tableName) throws Exception {
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new Exception("ContainsInvalid characters");
        }
    }
}
