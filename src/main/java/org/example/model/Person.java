package org.example.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Person implements Comparable<Person> {
    private int id;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private float height;
    private LocalDateTime birthday;
    private Color hairColor;
    private Country nationality;
    private Location location;

    private static int nextId = 1;

    public Person(String name, Coordinates coordinates, float height, LocalDateTime birthday,
                  Color hairColor, Country nationality, Location location) {
        this.id = 0;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date();
        this.height = height;
        this.birthday = birthday;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        validate();
    }

    public Person(int id, String name, Coordinates coordinates, Date creationDate, float height,
                  LocalDateTime birthday, Color hairColor, Country nationality, Location location) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.height = height;
        this.birthday = birthday;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        validate();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
        if (coordinates == null) throw new IllegalArgumentException("Coordinates cannot be null.");
        if (creationDate == null) throw new IllegalArgumentException("Creation date cannot be null.");
        if (height <= 0) throw new IllegalArgumentException("Height must be greater than 0.");
    }

    public void generateId() {
        if (id == 0) id = nextId++;
    }
    public static void updateNextId(int id){
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date date) { this.creationDate = date; }
    public float getHeight() { return height; }
    public LocalDateTime getBirthday() { return birthday; }
    public Color getHairColor() { return hairColor; }
    public Country getNationality() { return nationality; }
    public Location getLocation() { return location; }

    @Override
    public int compareTo(Person o) {
        int cmp = this.name.compareTo(o.name);
        if (cmp != 0) return cmp;
        cmp = Float.compare(this.height, o.height);
        if (cmp != 0) return cmp;
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", height=" + height +
                ", birthday=" + birthday +
                ", hairColor=" + hairColor +
                ", nationality=" + nationality +
                ", location=" + location +
                '}';
    }
}

