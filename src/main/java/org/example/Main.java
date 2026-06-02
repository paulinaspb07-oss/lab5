package org.example;

import org.example.auth.Session;
import org.example.auth.User;
import org.example.collection.CollectionManager;
import org.example.commands.*;
import org.example.storage.DbStorage;
import org.example.storage.DbUserStorage;
import org.example.utils.DbConfig;

import java.io.*;
import java.util.*;

public class Main {
    public static CollectionManager collectionManager;
    public static final int MAX_SCRIPT_DEPTH = 10;
    public static int scriptDepth = 0;

    private static final Map<String, Command> COMMANDS = new HashMap<>();
    private static DbUserStorage userStorage = new DbUserStorage();

    static {
        // Read-only commands
        COMMANDS.put("help", new HelpCommand());
        COMMANDS.put("info", new InfoCommand());
        COMMANDS.put("show", new ShowCommand());
        COMMANDS.put("print_ascending", new PrintAscendingCommand());
        COMMANDS.put("print_descending", new PrintDescendingCommand());
        COMMANDS.put("filter_starts_with_name", new FilterStartsWithNameCommand());

        // Modifying commands
        COMMANDS.put("add", new AddCommand());
        COMMANDS.put("update", new UpdateCommand());
        COMMANDS.put("remove_by_id", new RemoveByIDCommand());
        COMMANDS.put("clear", new ClearCommand());
        COMMANDS.put("add_if_min", new AddIfMinCommand());
        COMMANDS.put("remove_greater", new RemoveGreaterCommand());
        COMMANDS.put("remove_lower", new RemoveLowerCommand());

        // Auth commands
        COMMANDS.put("register", new RegisterCommand());
        COMMANDS.put("login", new LoginCommand());
        COMMANDS.put("logout", new LogoutCommand());

        // Utility
        COMMANDS.put("execute_script", new ExecuteScriptCommand());
        COMMANDS.put("exit", new ExitCommand());
    }

    public static void main(String[] args) {
        // Explicitly load PostgreSQL driver
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: PostgreSQL JDBC driver not found. " + e.getMessage());
        }

        String url = DbConfig.getUrl();
        String dbUser = DbConfig.getUser();
        String dbPassword = DbConfig.getPassword();
        System.out.println("Connecting to DB: " + url + " user: " + dbUser);
        
        DbStorage dbStorage = new DbStorage(url, dbUser, dbPassword);
        collectionManager = new CollectionManager(dbStorage);
        
        interactiveMode();
    }

    public static void interactiveMode() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))) {
            System.out.println("Welcome to the Collection Manager (PostgreSQL version).");
            System.out.println("Type 'login' or 'register' to start, or 'help' for commands.");
            while (true) {
                System.out.print("> ");
                String line = consoleReader.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;
                processCommand(line, consoleReader);
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }

    public static void processCommand(String command, BufferedReader input) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        Command commandObj = COMMANDS.get(cmd);
        if (commandObj != null) {
            try {
                commandObj.execute(parts, input);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Unknown command. Type 'help' for list.");
        }
    }

    // Helper methods for auth commands
    public static User login(String login, String password) {
        try {
            return userStorage.login(login, password);
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
        }
    }

    public static User register(String login, String password) {
        try {
            return userStorage.register(login, password);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return null;
        }
    }
}