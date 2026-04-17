package org.example.commands;

import org.example.inputreader.ReadPerson;
import static org.example.Main.*;
import org.example.model.Person;
import java.io.BufferedReader;

public class RemoveLowerCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        Person p = ReadPerson.read(consoleReader, true, -1);
        if (p != null) {
            int removed = collectionManager.removeLower(p);
            System.out.println("Removed elements lower than the person provided: " + removed);
        }
    }
}
