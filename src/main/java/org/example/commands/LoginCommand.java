package org.example.commands;

import org.example.auth.UserFileStorage;
import org.example.auth.Session;
import java.io.BufferedReader;

public class LoginCommand implements Command {
    private final UserFileStorage userStorage = new UserFileStorage("users.csv");

    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        System.out.print("Login: ");
        String login = input.readLine().trim();
        System.out.print("Password: ");
        String password = input.readLine().trim();

        var user = userStorage.login(login, password);
        if (user == null) {
            System.out.println("Invalid login or password.");
        } else {
            Session.setCurrentUser(user);
            System.out.println("Welcome, " + user.getLogin() + "!");
        }
    }
}