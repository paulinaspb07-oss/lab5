package org.example.auth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UserFileStorage {
    private final String fileName;

    public UserFileStorage(String fileName) {
        this.fileName = fileName;
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return users;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";", 3);

                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String login = parts[1];
                    String passwordHash = parts[2];

                    users.add(new User(id, login, passwordHash));
                }
            }
        } catch (Exception e) {
        System.out.println("Ошибка чтения users.csv: " + e.getMessage());
        }

        return users;
    }
    public User register(String login, String password) {
    login = login.trim();

    if (login.isEmpty()) {
        throw new IllegalArgumentException("Логин не может быть пустым");
    }

        if (password.isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        List<User> users = loadUsers();

        for (User user : users) {
            if (user.getLogin().equals(login)) {
                throw new IllegalArgumentException("Такой логин уже занят");
            }
        }

        int newId = 1;

        for (User user : users) {
            if (user.getId() >= newId) {
                newId = user.getId() + 1;
            }
        }

        String passwordHash = PasswordUtil.hash(password);
        User newUser = new User(newId, login, passwordHash);

        users.add(newUser);
        saveUsers(users);

        return newUser;
    }

    public User login(String login, String password) {
        String passwordHash = PasswordUtil.hash(password);

        for (User user : loadUsers()) {
            if (user.getLogin().equals(login.trim())
                    && user.getPasswordHash().equals(passwordHash)) {
                return user;
            }
        }

        return null;
    }

    private void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {

            for (User user : users) {
                writer.write(user.getId() + ";" + user.getLogin() + ";" + user.getPasswordHash());
                writer.newLine();
            }

        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения users.csv", e);
        }
    }
}
