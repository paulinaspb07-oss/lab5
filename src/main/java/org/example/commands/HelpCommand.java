package org.example.commands;

import java.io.BufferedReader;

public class HelpCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        System.out.println("Available commands:");
        System.out.println("  help - show this help");
        System.out.println("  info - information about the collection");
        System.out.println("  show - display all elements");
        System.out.println("  add - add a new element (fields entered line by line)");
        System.out.println("  update id <id> - update element with given id");
        System.out.println("  remove_by_id <id> - remove element by id");
        System.out.println("  clear - clear collection");
        System.out.println("  save - save collection to file");
        System.out.println("  execute_script <file> - execute script from file");
        System.out.println("  exit - exit program (without saving)");
        System.out.println("  add_if_min - add element if it is less than the smallest");
        System.out.println("  remove_greater - remove all elements greater than given");
        System.out.println("  remove_lower - remove all elements lower than given");
        System.out.println("  filter_starts_with_name <name> - show elements whose name starts with substring");
        System.out.println("  print_ascending - show elements in ascending order");
        System.out.println("  print_descending - show elements in descending order");
    }
}
