package org.example.auth;

public class Session {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static User reqireUser() {
        if (currentUser == null) {
            throw new IllegalStateException("Сначала нужно войти в систему");
        }

        return currentUser;
    }
}
