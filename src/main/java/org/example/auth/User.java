package org.example.auth;

public class User {
    private int id;
    private String login;
    private String passwordHash;

    public User(int id, String login, String passwordHash) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
