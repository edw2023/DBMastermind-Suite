package com.pw.dcp.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.drop.Drop;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SqlAnalyzer {

    /*
    * 我的DBA团队现在工作有一个痛点，就是每一个开发周期中，需要上线的DDL和DML脚本大概有100个sql文件，这100个sql文件原则上需要按照DBA给的编号执行（文件名包含编号 + 概括性文字。编号为数字，升序排列），这些sql文件中有部分会涉及同一张数据的表。现在请给我一个方法，这个方法需要扫描一个目录文件夹下面所有的sql文件，并分析每个SQL文件涉及了哪些数据库表格，以及具体做了哪些改动。方法需要返回一个列表，我需要用该列表来到生成Excel表格。方法返回的列表中，每条记录需要包含：1.原文件名（类型String），2.文件涉及哪些数据库表格（类型List<String>）， 3.文件的大致内容（用于概括语句的类型，例如Update，Drop..等SQL type）（类型List<String>），4.文件可能和哪些其它文件有依赖关系（可基于文件涉及哪些数据库表格来判断，如果两个文件涉及了同一个数据库表格，就可以认为他们有依赖关系）（类型List<String>）
    * */

    public static void main(String[] args) throws IOException, JSQLParserException {
        String folderPath = "/path/to/sql/files"; // 替换为实际的文件夹路径
        List<SqlFileInfo> sqlFileInfos = analyzeSqlFiles(folderPath);

        // 打印分析结果
        for (SqlFileInfo fileInfo : sqlFileInfos) {
            System.out.println("File: " + fileInfo.getFileName());
            System.out.println("Tables: " + fileInfo.getTables());
            System.out.println("SQL Types: " + fileInfo.getSqlTypes());
            System.out.println("Dependencies: " + fileInfo.getDependencies());
            System.out.println("------------------------------");
        }
    }

    public static List<SqlFileInfo> analyzeSqlFiles(String folderPath) throws IOException, JSQLParserException {
        List<SqlFileInfo> sqlFileInfos = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".sql"));

        if (files != null) {
            for (File file : files) {
                String content = new String(Files.readAllBytes(file.toPath()));
                SqlFileInfo fileInfo = new SqlFileInfo(file.getName());
                analyzeSqlContent(content, fileInfo);
                sqlFileInfos.add(fileInfo);
            }
        }

        return sqlFileInfos;
    }

    private static void analyzeSqlContent(String content, SqlFileInfo fileInfo) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(content);

        // 使用自定义的 StatementVisitor 来获取表格和SQL类型
        SqlStatementVisitor visitor = new SqlStatementVisitor(fileInfo);
        statement.accept(visitor);
    }
}

class SqlFileInfo {
    private String fileName;
    private List<String> tables;
    private List<String> sqlTypes;
    private List<String> dependencies;

    public SqlFileInfo(String fileName) {
        this.fileName = fileName;
        this.tables = new ArrayList<>();
        this.sqlTypes = new ArrayList<>();
        this.dependencies = new ArrayList<>();
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getTables() {
        return tables;
    }

    public List<String> getSqlTypes() {
        return sqlTypes;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void addTable(String tableName) {
        tables.add(tableName);
    }

    public void addSqlType(String sqlType) {
        sqlTypes.add(sqlType);
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }
}

class SqlStatementVisitor extends StatementVisitorAdapter {
    private final SqlFileInfo fileInfo;

    public SqlStatementVisitor(SqlFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public void visit(Drop drop) {
        fileInfo.addSqlType("DROP");
    }

    // 可以根据需要添加其他 SQL 类型的处理方法

    @Override
    public void visit(net.sf.jsqlparser.statement.update.Update update) {
        fileInfo.addSqlType("UPDATE");
        // 提取表格信息
//        update.getTables().forEach(table -> fileInfo.addTable(table.getName()));
    }
}

