package org.example;

import org.example.auth.Session;
import org.example.auth.User;
import org.example.auth.UserFileStorage;
import org.example.collection.CollectionManager;
import org.example.commands.*;
import org.example.model.Person;
import org.example.storage.XmlFileStorage;

import java.io.*;
import java.util.*;

public class Main {
    public static CollectionManager collectionManager;
    public static String fileName;
    public static final int MAX_SCRIPT_DEPTH = 10;
    public static int scriptDepth = 0;

    private static final Map<String, Command> COMMANDS = new HashMap<>();
    private static final XmlFileStorage fileStorage = new XmlFileStorage();
    private static final UserFileStorage userStorage = new UserFileStorage("users.csv");

    static {
        // Read-only commands (available to guests)
        COMMANDS.put("help", new HelpCommand());
        COMMANDS.put("info", new InfoCommand());
        COMMANDS.put("show", new ShowCommand());
        COMMANDS.put("print_ascending", new PrintAscendingCommand());
        COMMANDS.put("print_descending", new PrintDescendingCommand());
        COMMANDS.put("filter_starts_with_name", new FilterStartsWithNameCommand());

        // Modifying commands (require authentication)
        COMMANDS.put("add", new AddCommand());
        COMMANDS.put("update", new UpdateCommand());
        COMMANDS.put("remove_by_id", new RemoveByIDCommand());
        COMMANDS.put("clear", new ClearCommand());
        COMMANDS.put("add_if_min", new AddIfMinCommand());
        COMMANDS.put("remove_greater", new RemoveGreaterCommand());
        COMMANDS.put("remove_lower", new RemoveLowerCommand());

        // Authentication commands
        COMMANDS.put("register", new RegisterCommand());
        COMMANDS.put("login", new LoginCommand());
        COMMANDS.put("logout", new LogoutCommand());

        // Utility commands
        COMMANDS.put("save", new SaveCommand());
        COMMANDS.put("load", new LoadCommand());
        COMMANDS.put("execute_script", new ExecuteScriptCommand());
        COMMANDS.put("exit", new ExitCommand());
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            fileName = args[0].trim();
            System.out.println("Using file from args: " + fileName);
        } else {
            fileName = "data.xml";
            System.out.println("FILE_NAME not set. Using default: " + fileName);
        }

        collectionManager = new CollectionManager();
        loadCollectionFromFile();
        interactiveMode();
    }

    public static void loadCollectionFromFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found. Starting with empty collection.");
            return;
        }
        try {
            List<Person> persons = fileStorage.load(fileName);
            for (Person p : persons) {
                collectionManager.addPerson(p);
            }
            System.out.println("Collection loaded from file.");
        } catch (Exception e) {
            System.err.println("Error loading collection: " + e.getMessage());
        }
    }

    // Overloaded method to support LoadCommand with argument
    public static void loadCollectionFromFile(String newFileName) {
        if (newFileName != null && !newFileName.trim().isEmpty()) {
            fileName = newFileName.trim();
        }
        loadCollectionFromFile();
    }

    public static void saveCollectionToFile() {
        try {
            fileStorage.save(fileName, collectionManager.getAllPersons());
            System.out.println("Collection saved to file.");
        } catch (Exception e) {
            System.err.println("Error saving collection: " + e.getMessage());
        }
    }

    // Overloaded method to support SaveCommand with argument
    public static void saveCollectionToFile(String newFileName) {
        if (newFileName != null && !newFileName.trim().isEmpty()) {
            fileName = newFileName.trim();
        }
        saveCollectionToFile();
    }

    public static void interactiveMode() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))) {
            boolean authenticated = false;
            while (!authenticated) {
                System.out.println("Welcome to the Collection Manager.");
                System.out.print("Do you want to (1) Login, (2) Register, or (3) Continue as Guest? (1/2/3): ");
                String choice = consoleReader.readLine();
                if (choice == null) break;
                choice = choice.trim();
                switch (choice) {
                    case "1":
                        System.out.print("Login: ");
                        String login = consoleReader.readLine().trim();
                        System.out.print("Password: ");
                        String password = consoleReader.readLine().trim();
                        User user = userStorage.login(login, password);
                        if (user == null) {
                            System.out.println("Invalid login or password.");
                        } else {
                            Session.setCurrentUser(user);
                            authenticated = true;
                            System.out.println("Logged in as: " + user.getLogin());
                        }
                        break;
                    case "2":
                        System.out.print("Login: ");
                        login = consoleReader.readLine().trim();
                        System.out.print("Password: ");
                        password = consoleReader.readLine().trim();
                        try {
                            user = userStorage.register(login, password);
                            Session.setCurrentUser(user);
                            authenticated = true;
                            System.out.println("Registered and logged in as: " + user.getLogin());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Registration failed: " + e.getMessage());
                        }
                        break;
                    case "3":
                        authenticated = true;
                        System.out.println("Continuing as guest. You can only view data. Use 'login' or 'register' later if needed.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            }

            System.out.println("Enter 'help' for list of commands.");
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
            }
        } else {
            System.out.println("Unknown command. Type 'help' for list.");
        }
    }
}