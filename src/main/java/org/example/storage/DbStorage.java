package org.example.storage;

import org.example.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DbStorage {
    private final String url, user, password;
    public DbStorage(String url, String user, String password) {
        this.url = url; this.user = user; this.password = password;
    }
    public List<Person> loadAllPersons() throws SQLException {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT id, name, coord_x, coord_y, height, birthday, hair_color, nationality, location_x, location_y, location_z, creation_date, owner_id FROM person";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    public void insertPerson(Person p, int ownerId) throws SQLException {
        String sql = "INSERT INTO person (name, coord_x, coord_y, height, birthday, hair_color, nationality, location_x, location_y, location_z, creation_date, owner_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setLong(2, p.getCoordinates().getX());
            ps.setDouble(3, p.getCoordinates().getY());
            ps.setFloat(4, p.getHeight());
            ps.setObject(5, p.getBirthday());
            ps.setString(6, p.getHairColor().name());
            ps.setString(7, p.getNationality().name());
            ps.setDouble(8, p.getLocation().getX());
            ps.setDouble(9, p.getLocation().getY());
            ps.setInt(10, p.getLocation().getZ());
            ps.setTimestamp(11, new Timestamp(p.getCreationDate().getTime()));
            ps.setInt(12, ownerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) p.setId(rs.getInt(1));
        }
    }
    public void updatePerson(Person p) throws SQLException {
        String sql = "UPDATE person SET name=?, coord_x=?, coord_y=?, height=?, birthday=?, hair_color=?, nationality=?, location_x=?, location_y=?, location_z=?, creation_date=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setLong(2, p.getCoordinates().getX());
            ps.setDouble(3, p.getCoordinates().getY());
            ps.setFloat(4, p.getHeight());
            ps.setObject(5, p.getBirthday());
            ps.setString(6, p.getHairColor().name());
            ps.setString(7, p.getNationality().name());
            ps.setDouble(8, p.getLocation().getX());
            ps.setDouble(9, p.getLocation().getY());
            ps.setInt(10, p.getLocation().getZ());
            ps.setTimestamp(11, new Timestamp(p.getCreationDate().getTime()));
            ps.setInt(12, p.getId());
            ps.executeUpdate();
        }
    }
    public boolean deletePerson(int id, int ownerId) throws SQLException {
        String sql = "DELETE FROM person WHERE id=? AND owner_id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, ownerId);
            return ps.executeUpdate() > 0;
        }
    }
    public void clearByOwner(int ownerId) throws SQLException {
        String sql = "DELETE FROM person WHERE owner_id=?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.executeUpdate();
        }
    }
    private Person map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Coordinates coords = new Coordinates(rs.getLong("coord_x"), rs.getDouble("coord_y"));
        float height = rs.getFloat("height");
        LocalDateTime birthday = rs.getObject("birthday", LocalDateTime.class);
        Color hair = Color.valueOf(rs.getString("hair_color"));
        Country nation = Country.valueOf(rs.getString("nationality"));
        Location loc = new Location(rs.getDouble("location_x"), rs.getDouble("location_y"), rs.getInt("location_z"));
        Date creation = new Date(rs.getTimestamp("creation_date").getTime());
        int ownerId = rs.getInt("owner_id");
        Person p = new Person(id, name, coords, creation, height, birthday, hair, nation, loc);
        p.setOwnerID(ownerId);
        return p;
    }
}