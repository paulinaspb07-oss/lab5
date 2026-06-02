package org.example.commands;

import org.example.Main;
import org.example.auth.Session;
import org.example.model.*;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddIfMinCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        var currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Login first.");
            return;
        }
        System.out.println("Enter new person (will be added only if it's the minimum).");
        try {
            // same reading as AddCommand
            System.out.print("Name: ");
            String name = input.readLine().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Name required");
            System.out.print("Coord X (long): ");
            long cx = Long.parseLong(input.readLine().trim());
            System.out.print("Coord Y (double): ");
            double cy = Double.parseDouble(input.readLine().trim());
            Coordinates coords = new Coordinates(cx, cy);
            System.out.print("Height (float): ");
            float h = Float.parseFloat(input.readLine().trim());
            System.out.print("Birthday (yyyy-MM-ddTHH:mm:ss, Enter to skip): ");
            String bd = input.readLine().trim();
            LocalDateTime bday = bd.isEmpty() ? null : LocalDateTime.parse(bd, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            System.out.print("Hair Color (e.g. BLACK, RED, ...): ");
            Color hair = Color.valueOf(input.readLine().trim().toUpperCase());
            System.out.print("Nationality (e.g. USA, FRANCE, ...): ");
            Country nat = Country.valueOf(input.readLine().trim().toUpperCase());
            System.out.print("Location X (double): ");
            double lx = Double.parseDouble(input.readLine().trim());
            System.out.print("Location Y (Double): ");
            Double ly = Double.parseDouble(input.readLine().trim());
            System.out.print("Location Z (Integer): ");
            Integer lz = Integer.parseInt(input.readLine().trim());
            Location loc = new Location(lx, ly, lz);

            Person p = new Person(name, coords, h, bday, hair, nat, loc);
            if (Main.collectionManager.isLessThanMin(p)) {
                Main.collectionManager.addPerson(p, currentUser.getId());
                System.out.println("Added (minimum).");
            } else {
                System.out.println("Not minimum, not added.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}