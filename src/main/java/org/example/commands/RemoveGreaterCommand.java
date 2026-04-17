package org.example.commands;

import org.example.model.Person;
import org.example.inputreader.ReadPerson;
import java.io.BufferedReader;
import static org.example.Main.collectionManager;

public class RemoveGreaterCommand implements Command {
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: remove_greater {element}");
            return;
        }
        // Read a Person object from console (similar to add command)
        Person p = ReadPerson.read(consoleReader, true, -1);
        if (p != null) {
            int removed = collectionManager.removeGreater(p);
            System.out.println("Removed " + removed + " elements greater than the specified one.");
        }
    }
}