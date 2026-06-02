package org.example.utils;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DbConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in resources folder");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUser() {
        return properties.getProperty("db.user");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}