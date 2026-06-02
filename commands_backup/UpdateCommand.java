package org.example.commands;

import org.example.Main;
import org.example.auth.Session;
import org.example.model.*;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpdateCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader input) throws Exception {
        var currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            System.out.println("You must login first.");
            return;
        }
        if (args.length < 2) {
            System.out.println("Usage: update <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }
        Person existing = Main.collectionManager.getPersonById(id);
        if (existing == null) {
            System.out.println("Person not found.");
            return;
        }
        if (existing.getOwnerID() != currentUser.getId()) {
            System.out.println("Ошибка: у вас нет прав на изменение этого объекта");
            return;
        }
        System.out.println("Editing ID " + id + ". Press Enter to keep current value.");
        try {
            System.out.print("Name [" + existing.getName() + "]: ");
            String name = input.readLine().trim();
            if (name.isEmpty()) name = existing.getName();

            System.out.print("Coord X (long) [" + existing.getCoordinates().getX() + "]: ");
            long coordX = input.readLine().trim().isEmpty() ? existing.getCoordinates().getX() : Long.parseLong(input.readLine().trim());
            System.out.print("Coord Y (double) [" + existing.getCoordinates().getY() + "]: ");
            double coordY = input.readLine().trim().isEmpty() ? existing.getCoordinates().getY() : Double.parseDouble(input.readLine().trim());
            Coordinates coords = new Coordinates(coordX, coordY);

            System.out.print("Height (float) [" + existing.getHeight() + "]: ");
            float height = input.readLine().trim().isEmpty() ? existing.getHeight() : Float.parseFloat(input.readLine().trim());

            System.out.print("Birthday (yyyy-MM-ddTHH:mm:ss) [" + (existing.getBirthday() != null ? existing.getBirthday() : "null") + "]: ");
            String bdayStr = input.readLine().trim();
            LocalDateTime birthday = bdayStr.isEmpty() ? existing.getBirthday() : LocalDateTime.parse(bdayStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            System.out.print("Hair Color [" + existing.getHairColor() + "]: ");
            String hairStr = input.readLine().trim();
            Color hair = hairStr.isEmpty() ? existing.getHairColor() : Color.valueOf(hairStr.toUpperCase());

            System.out.print("Nationality [" + existing.getNationality() + "]: ");
            String natStr = input.readLine().trim();
            Country nat = natStr.isEmpty() ? existing.getNationality() : Country.valueOf(natStr.toUpperCase());

            System.out.print("Location X (double) [" + existing.getLocation().getX() + "]: ");
            double locX = input.readLine().trim().isEmpty() ? existing.getLocation().getX() : Double.parseDouble(input.readLine().trim());
            System.out.print("Location Y (Double) [" + existing.getLocation().getY() + "]: ");
            Double locY = input.readLine().trim().isEmpty() ? existing.getLocation().getY() : Double.parseDouble(input.readLine().trim());
            System.out.print("Location Z (Integer) [" + existing.getLocation().getZ() + "]: ");
            Integer locZ = input.readLine().trim().isEmpty() ? existing.getLocation().getZ() : Integer.parseInt(input.readLine().trim());
            Location loc = new Location(locX, locY, locZ);

            Person updated = new Person(name, coords, height, birthday, hair, nat, loc);
            boolean success = Main.collectionManager.updatePerson(id, updated, currentUser.getId());
            if (success) System.out.println("Updated.");
            else System.out.println("Update failed.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}