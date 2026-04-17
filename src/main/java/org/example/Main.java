package org.example;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.example.commands.*;


import org.example.collection.CollectionManager;
import org.example.model.*;
import org.example.parser.XMLParser;
import org.w3c.dom.*;
import org.xml.sax.SAXException;



public class Main {
    public static CollectionManager collectionManager;
    public static String fileName;
    public static final int MAX_SCRIPT_DEPTH = 10;
    public static int scriptDepth = 0;

    private static final Map<String, Command> COMMANDS = new HashMap<>();

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
 //       COMMANDS.put("remove_greater", new RemoveGreaterCommand());
        COMMANDS.put("remove_lower", new RemoveLowerCommand());
        COMMANDS.put("filter_starts_with_name", new FilterStartsWithNameCommand());
 //       COMMANDS.put("print_ascending", new PrintAscendingCommand());
 //       COMMANDS.put("print_descending", new PrintDescendingCommand());
    }


    public static void main(String[] args) {
        fileName = System.getenv("FILE_NAME");
        if (fileName == null || fileName.trim().isEmpty()) {
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            List<Person> persons = XMLParser.readPersons(reader);
            for (Person p : persons) {
                collectionManager.addPerson(p);
            }
            System.out.println("Collection loaded from file.");
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            System.err.println("Error loading collection: " + e.getMessage());
        }
    }

    public static void saveCollectionToFile() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
            XMLParser.writePersons(writer, collectionManager.getAllPersons());
            System.out.println("Collection saved to file.");
        } catch (IOException | TransformerException | ParserConfigurationException e) {
            System.err.println("Error saving collection: " + e.getMessage());
        }
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
