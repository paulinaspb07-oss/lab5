package org.example.commands;

import org.example.auth.Session;
import java.io.BufferedReader;

public class LogoutCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        if (Session.getCurrentUser() == null) {
            System.out.println("No user is logged in.");
        } else {
            Session.setCurrentUser(null);
            System.out.println("Logged out successfully.");
        }
    }
}