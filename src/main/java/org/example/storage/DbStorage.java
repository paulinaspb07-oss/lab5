package org.example.storage;

import org.example.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DbStorage {
    private final String url;
    private final String user;
    private final String password;

    public DbStorage(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public List<Person> loadAllPersons() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT id, name, coord_x, coord_y, height, birthday, hair_color, " +
                     "nationality, location_x, location_y, location_z, creation_date, owner_id " +
                     "FROM person";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                persons.add(mapResultSetToPerson(rs));
            }
        }
        return persons;
    }

    public void insertPerson(Person person, int ownerId) throws SQLException {
        String sql = "INSERT INTO person (name, coord_x, coord_y, height, birthday, hair_color, " +
                     "nationality, location_x, location_y, location_z, creation_date, owner_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, person.getName());
            pstmt.setLong(2, person.getCoordinates().getX());
            pstmt.setDouble(3, person.getCoordinates().getY());
            pstmt.setFloat(4, person.getHeight());
            pstmt.setObject(5, person.getBirthday());
            pstmt.setString(6, person.getHairColor().name());
            pstmt.setString(7, person.getNationality().name());
            pstmt.setDouble(8, person.getLocation().getX());
            pstmt.setDouble(9, person.getLocation().getY());
            pstmt.setInt(10, person.getLocation().getZ());
            pstmt.setTimestamp(11, new Timestamp(person.getCreationDate().getTime()));
            pstmt.setInt(12, ownerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                person.setId(rs.getInt(1));
            }
        }
    }

    public void updatePerson(Person person) throws SQLException {
        String sql = "UPDATE person SET name=?, coord_x=?, coord_y=?, height=?, birthday=?, " +
                     "hair_color=?, nationality=?, location_x=?, location_y=?, location_z=?, " +
                     "creation_date=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, person.getName());
            pstmt.setLong(2, person.getCoordinates().getX());
            pstmt.setDouble(3, person.getCoordinates().getY());
            pstmt.setFloat(4, person.getHeight());
            pstmt.setObject(5, person.getBirthday());
            pstmt.setString(6, person.getHairColor().name());
            pstmt.setString(7, person.getNationality().name());
            pstmt.setDouble(8, person.getLocation().getX());
            pstmt.setDouble(9, person.getLocation().getY());
            pstmt.setInt(10, person.getLocation().getZ());
            pstmt.setTimestamp(11, new Timestamp(person.getCreationDate().getTime()));
            pstmt.setInt(12, person.getId());
            pstmt.executeUpdate();
        }
    }

    public boolean deletePerson(int id, int ownerId) throws SQLException {
        String sql = "DELETE FROM person WHERE id = ? AND owner_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, ownerId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        }
    }

    // Clear all persons owned by user
    public void clearByOwner(int ownerId) throws SQLException {
        String sql = "DELETE FROM person WHERE owner_id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            pstmt.executeUpdate();
        }
    }

    private Person mapResultSetToPerson(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Coordinates coords = new Coordinates(
            rs.getLong("coord_x"),
            rs.getDouble("coord_y")
        );
        float height = rs.getFloat("height");
        LocalDateTime birthday = rs.getObject("birthday", LocalDateTime.class);
        Color hairColor = Color.valueOf(rs.getString("hair_color"));
        Country nationality = Country.valueOf(rs.getString("nationality"));
        Location location = new Location(
            rs.getDouble("location_x"),
            rs.getDouble("location_y"),
            rs.getInt("location_z")
        );
        Date creationDate = new Date(rs.getTimestamp("creation_date").getTime());
        int ownerId = rs.getInt("owner_id");

        Person p = new Person(id, name, coords, creationDate, height, birthday, hairColor, nationality, location);
        p.setOwnerID(ownerId);
        return p;
    }
}