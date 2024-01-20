package com.pw.dcp.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigrationService {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    private static final String MIGRATION_PATH = "src/main/resources/db/migration";

    public void migrateDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Files.list(Paths.get(MIGRATION_PATH))
                    .sorted()
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            try {
                                migrateVersion(connection, path);
                            } catch (IOException | SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void rollbackDatabase(String targetVersion) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Files.list(Paths.get(MIGRATION_PATH))
                    .sorted()
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            try {
                                String version = path.getFileName().toString();
                                if (version.compareTo(targetVersion) > 0) {
                                    rollbackVersion(connection, path);
                                }
                            } catch (IOException | SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private void migrateVersion(Connection connection, Path versionPath) throws IOException, SQLException {
        executeScript(connection, versionPath.resolve("rollforward.sql"));
    }

    private void rollbackVersion(Connection connection, Path versionPath) throws IOException, SQLException {
        executeScript(connection, versionPath.resolve("rollback.sql"));
    }

    private void executeScript(Connection connection, Path scriptPath) throws IOException, SQLException {
        if (Files.exists(scriptPath)) {
            String scriptContent = new String(Files.readAllBytes(scriptPath));
            try (Statement statement = connection.createStatement()) {
                statement.execute(scriptContent);
            }
        }
    }
}

