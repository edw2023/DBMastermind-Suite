package com.pw.dcp.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/*
* Sample code POC
* */
@Service
public class MigrationService {

    private static Map<String, Boolean> migrationCache = new HashMap<>();

    public static void main(String[] args) {
        // 数据库连接配置
        String url = "jdbc:mysql://localhost:3306/your_database";
        String user = "your_username";
        String password = "your_password";

        // 创建Flyway实例
        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations("classpath:db/migration") // 指定迁移脚本的位置
                .load();

        // 执行数据库迁移
        flyway.migrate();

        System.out.println("Database migration completed!");

//        String url = "jdbc:mysql://localhost:3306/your_database";
//        String user = "your_username";
//        String password = "your_password";
//
//        // 创建Flyway实例
//        Flyway flyway = Flyway.configure()
//                .dataSource(url, user, password)
//                .locations("classpath:db/migration") // 指定迁移脚本的位置
//                .load();
//
//        // 回滚到指定版本
//        flyway.undo();
//
//        System.out.println("Database rollback completed!");


        // 数据库连接配置
//        String url = "jdbc:mysql://localhost:3306/your_database";
//        String user = "your_username";
//        String password = "your_password";
//
//        // 创建Flyway实例
//        Flyway flyway = Flyway.configure()
//                .dataSource(url, user, password)
//                .locations("classpath:db/migration") // 指定迁移脚本的位置
//                .load();
//
//        // 获取所有已经执行的迁移脚本
//        flyway.info().all()
//                .forEach(migrationInfo -> migrationCache.put(migrationInfo.getScript(), true));
//
//        // 执行未执行过的迁移脚本
//        flyway.info().pending()
//                .forEach(migrationInfo -> {
//                    if (!migrationCache.containsKey(migrationInfo.getScript())) {
//                        // 执行迁移脚本
//                        flyway.migrate();
//                        // 更新缓存
//                        migrationCache.put(migrationInfo.getScript(), true);
//                    }
//                });
//
//        System.out.println("Database migration completed!");

    }

}
