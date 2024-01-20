package com.pw.dcp.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.comment.Comment;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLValidator {

    public static final String SQL_INJECTION_PATTERN = "\\b(\\s*insert\\s+into\\s+|\\s*delete\\s+from\\s+|\\s*update\\s+|\\s*drop\\s+table\\s+|\\s*alter\\s+table\\s+|\\s*select\\s+.*\\s+from\\s+|\\s*truncate\\s+table\\s+|\\s*exec\\s*\\()\\b";
    public static final String SENSITIVE_PATTERN = "\\b(password|user|secret|apikey|creditcard|ssn)\\b";
    public static final String WHERE_CLAUSE = "WHERE";
    public static final boolean REQUIRED = true, FORBIDDEN = false;

    /*
    * Validate SQL syntax
    * Database Type: Oracle, SQL Server, MySQL
    * */
    public String validateSyntax(String sql) {
        Statement statement = null;
        try {
            // Parse SQL
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            System.out.println("SQL Syntax Validation Error: " + e.getMessage());
        }
        return statement.toString();
    }

    public void validateSafety(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        checkSqlInjection(sql);
        checkTableName(sql);
        checkSensitiveInfo(sql);
        checkDropAndTruncateStatement(sql);
        checkUpdateAndDeleteStatement(sql);
        if (statement instanceof Merge) {

        } else if (statement instanceof Replace) {

        } else if (statement instanceof Upsert) {

        }
    }

    private void checkUpdateAndDeleteStatement(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Update ||statement instanceof Delete) {
            checkWhereClause(sql);
        }
    }

    private void checkDropAndTruncateStatement(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Drop || statement instanceof Truncate) {
            //Forbidden
        }
    }

    private void checkWhereClause(String sql) {
        checkKeywords(sql, WHERE_CLAUSE, REQUIRED);
    }

    private void checkSensitiveInfo(String sql) {
        checkKeywords(sql, SENSITIVE_PATTERN, FORBIDDEN);
    }

    private static boolean checkTableName(String tableName){
        // 检查表名是否为空
        if (tableName == null || tableName.trim().isEmpty()) {
            System.out.println("表名不能为空");
            return false;
        }

        // 检查表名长度是否合理
        if (tableName.length() > 255) {
            System.out.println("表名长度过长");
            return false;
        }

        // 检查表名是否包含特殊字符
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            System.out.println("表名包含非法字符");
            return false;
        }
        return true;
    }

    private static boolean checkKeywords(String sql, String keyword,boolean required) {
        // 使用正则表达式进行检查
        Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);

        return matcher.find();
    }
}

