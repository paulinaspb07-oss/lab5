package org.example.commands;

import static org.example.Main.*;
import org.example.model.Person;
import java.io.BufferedReader;
import org.example.inputReader.ReadPerson;

public class AddIfMinCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        Person p = ReadPerson.read(consoleReader, true, -1);
        if (p != null && collectionManager.isLessThanMin(p)) {
            collectionManager.addPerson(p);
            System.out.println("Element added as minimum.");
        } else {
            System.out.println("Element not added: it is not less than the minimum.");
        }
    }
}
