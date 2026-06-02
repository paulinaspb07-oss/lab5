package org.example.commands;

import org.example.Main;
import org.example.auth.Session;
import java.io.BufferedReader;

public class RemoveByIDCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        var currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("You must login first.");
            return;
        }
        if (args.length < 2) {
            System.out.println("Usage: remove_by_id <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }
        boolean removed = Main.collectionManager.removeById(id, currentUser.getId());
        if (removed) System.out.println("Removed.");
        else System.out.println("Not found or not yours.");
    }
}