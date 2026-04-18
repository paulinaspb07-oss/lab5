package org.example;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.example.commands.*;
import org.example.storage.XmlFileStorage;

import org.example.collection.CollectionManager;
import org.example.model.*;
import org.w3c.dom.*;



public class Main {
    public static CollectionManager collectionManager;
    public static String fileName;
    public static final int MAX_SCRIPT_DEPTH = 10;
    public static int scriptDepth = 0;

    private static final Map<String, Command> COMMANDS = new HashMap<>();
    private static final  XmlFileStorage fileStorage = new XmlFileStorage();

    static {
        COMMANDS.put("help", new HelpCommand());
        COMMANDS.put("info", new InfoCommand());
        COMMANDS.put("show", new ShowCommand());
        COMMANDS.put("add", new AddCommand());
        COMMANDS.put("update", new UpdateCommand());
        COMMANDS.put("remove_by_id", new RemoveByIDCommand());
        COMMANDS.put("clear", new ClearCommand());
        COMMANDS.put("save", new SaveCommand());
        COMMANDS.put("execute_script", new ExecuteScriptCommand());
        COMMANDS.put("exit", new ExitCommand());
        COMMANDS.put("add_if_min", new AddIfMinCommand());
        COMMANDS.put("remove_greater", new RemoveGreaterCommand());
        COMMANDS.put("remove_lower", new RemoveLowerCommand());
        COMMANDS.put("filter_starts_with_name", new FilterStartsWithNameCommand());
        COMMANDS.put("print_ascending", new PrintAscendingCommand());
        COMMANDS.put("print_descending", new PrintDescendingCommand());
        COMMANDS.put("load", new LoadCommand());
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
    public static void loadCollectionFromFile(String newFileName) {
        if ( newFileName != null && !newFileName.trim().isEmpty()){
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
    public static void saveCollectionToFile(String newFileName){
        if (newFileName != null && !newFileName.trim().isEmpty()){
            fileName = newFileName.trim();
        }
        saveCollectionToFile();
    }

    public static void interactiveMode() {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"))) {
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
