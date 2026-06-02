package org.example.commands;

import org.example.auth.UserFileStorage;
import org.example.auth.Session;
import java.io.BufferedReader;

public class RegisterCommand implements Command {
    private final UserFileStorage userStorage = new UserFileStorage("users.csv");

    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        System.out.print("Login: ");
        String login = input.readLine().trim();
        System.out.print("Password: ");
        String password = input.readLine().trim();

        try {
            var user = userStorage.register(login, password);
            Session.setCurrentUser(user);
            System.out.println("Registration successful. Logged in as " + user.getLogin());
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}