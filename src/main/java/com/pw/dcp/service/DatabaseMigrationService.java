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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseMigrationService {

    private static String JDBC_URL = "jdbc:mysql://localhost:3306/your_database";
    private static String USER = "your_username";
    private static String PASSWORD = "your_password";
    private static String MIGRATION_PATH = "src/main/resources/db/migration";
    private static final String ROLLFORWARD_SCRIPT = "rollforward.sql";
    private static final String ROLLBACK_SCRIPT = "rollback.sql";

    public DatabaseMigrationService(String jdbcUrl, String user, String password, String migrationPath) {
        this.JDBC_URL = jdbcUrl;
        this.USER = user;
        this.PASSWORD = password;
        this.MIGRATION_PATH = migrationPath;
    }

    public void migrateDatabase(String currentVersion, String targetVersion) throws SQLException, IOException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            List<Path> migrationPaths = Files.list(Paths.get(MIGRATION_PATH))
                    .filter(Files::isDirectory)
                    .sorted()
                    .collect(Collectors.toList());

            int currentIndex = migrationPaths.indexOf(Paths.get(MIGRATION_PATH, currentVersion));
            int targetIndex = migrationPaths.indexOf(Paths.get(MIGRATION_PATH, targetVersion));

            if (currentIndex >= 0 && targetIndex >= 0 && currentIndex < targetIndex) {
                for (int i = currentIndex + 1; i <= targetIndex; i++) {
                    List<Path> rollforwardPaths = Files.list(migrationPaths.get(i))
                            .filter(p -> p.endsWith(ROLLFORWARD_SCRIPT))
                            .collect(Collectors.toList());

                    rollforwardPaths.forEach(path -> migrateVersion(connection, path));
                }
            } else if (currentIndex >= 0 && targetIndex >= 0 && currentIndex > targetIndex) {
                for (int i = targetIndex; i < currentIndex; i++) {
                    List<Path> rollbackPaths = Files.list(migrationPaths.get(i))
                            .filter(p -> p.endsWith(ROLLBACK_SCRIPT))
                            .collect(Collectors.toList());

                    rollbackPaths.forEach(path -> rollbackVersion(connection, path));
                }
            }
        }
    }

    private void migrateVersion(Connection connection, Path scriptPath) {
        try {
            executeScript(connection, scriptPath);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error migrating version " + scriptPath.getParent().getFileName(), e);
        }
    }

    private void rollbackVersion(Connection connection, Path scriptPath) {
        try {
            executeScript(connection, scriptPath);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error rolling back version " + scriptPath.getParent().getFileName(), e);
        }
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
