package org.example.inputreader;

import org.example.model.*;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReadPerson {

    public static Person read(BufferedReader reader, boolean generateId, int existingId) throws Exception {
        System.out.print("Enter name: ");
        String name = reader.readLine();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        System.out.print("Enter height (float, > 0): ");
        float height = Float.parseFloat(reader.readLine());

        System.out.print("Enter coordinates X (long): ");
        long coordX = Long.parseLong(reader.readLine());
        System.out.print("Enter coordinates Y (double): ");
        double coordY = Double.parseDouble(reader.readLine());
        Coordinates coords = new Coordinates(coordX, coordY);

        System.out.print("Enter birthday (YYYY-MM-DDTHH:MM:SS, or leave empty): ");
        String bdayStr = reader.readLine();
        LocalDateTime birthday = null;
        if (bdayStr != null && !bdayStr.trim().isEmpty()) {
            birthday = LocalDateTime.parse(bdayStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        System.out.print("Enter hair color (GREEN, RED, BLUE, YELLOW, ORANGE, WHITE): ");
        Color hairColor = Color.valueOf(reader.readLine().toUpperCase());

        System.out.print("Enter nationality (RUSSIA, FRANCE, CHINA, ITALY): ");
        Country nationality = Country.valueOf(reader.readLine().toUpperCase());

        System.out.print("Enter location X (double): ");
        double locX = Double.parseDouble(reader.readLine());
        System.out.print("Enter location Y (Double): ");
        Double locY = Double.parseDouble(reader.readLine());
        System.out.print("Enter location Z (Integer): ");
        Integer locZ = Integer.parseInt(reader.readLine());
        Location location = new Location(locX, locY, locZ);

        Person p = new Person(name, coords, height, birthday, hairColor, nationality, location);
        if (generateId) {
            p.generateId();
        }
        if (existingId > 0) {
            p.setId(existingId);
        }
        return p;
    }
}