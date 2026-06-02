package org.example.utils;

import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
    private static final Properties props = new Properties();
    static {
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found");
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }
    public static String getUrl() { return props.getProperty("db.url"); }
    public static String getUser() { return props.getProperty("db.user"); }
    public static String getPassword() { return props.getProperty("db.password"); }
}