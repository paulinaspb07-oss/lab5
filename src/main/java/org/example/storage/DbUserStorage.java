package org.example.storage;

import org.example.auth.User;
import org.example.auth.PasswordUtil;
import org.example.utils.DbConfig;
import java.sql.*;

public class DbUserStorage {
    private final String url;
    private final String user;
    private final String password;

    public DbUserStorage() {
        this.url = DbConfig.getUrl();
        this.user = DbConfig.getUser();
        this.password = DbConfig.getPassword();
        System.out.println("DbUserStorage initialized. URL: " + url);
    }

    public User register(String login, String password) throws SQLException {
        login = login.trim();
        if (login.isEmpty()) throw new IllegalArgumentException("Login empty");
        if (password.isEmpty()) throw new IllegalArgumentException("Password empty");

        // Check existing
        String checkSql = "SELECT id FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(url, this.user, this.password);
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                throw new IllegalArgumentException("Login already taken");
            }
        } catch (SQLException e) {
            System.err.println("Error checking existing user: " + e.getMessage());
            throw e;
        }

        String hash = PasswordUtil.hash(password);
        String insertSql = "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(url, this.user, this.password);
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, login);
            ps.setString(2, hash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("User registered: id=" + id + ", login=" + login);
                return new User(id, login, hash);
            } else {
                throw new SQLException("Insert failed, no ID returned");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            throw e;
        }
    }

    public User login(String login, String password) throws SQLException {
        login = login.trim();
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(url, this.user, this.password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = PasswordUtil.hash(password);
                System.out.println("Login attempt for " + login);
                System.out.println("Stored hash: " + storedHash);
                System.out.println("Input hash:  " + inputHash);
                if (storedHash.equals(inputHash)) {
                    int id = rs.getInt("id");
                    System.out.println("Login successful for " + login + " (id=" + id + ")");
                    return new User(id, rs.getString("login"), storedHash);
                } else {
                    System.out.println("Hash mismatch for " + login);
                }
            } else {
                System.out.println("No user found with login: " + login);
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            throw e;
        }
        return null;
    }
}