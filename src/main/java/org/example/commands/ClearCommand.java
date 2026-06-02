package org.example.commands;

import org.example.Main;
import org.example.auth.Session;
import java.io.BufferedReader;

public class ClearCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        var currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("You must login first.");
            return;
        }
        System.out.print("Delete ALL your objects? (y/N): ");
        String confirm = input.readLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("Cancelled.");
            return;
        }
        int removed = Main.collectionManager.clearByOwner(currentUser.getId());
        System.out.println("Removed " + removed + " of your objects.");
    }
}