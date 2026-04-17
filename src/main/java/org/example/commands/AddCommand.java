package org.example.commands;

import org.example.inputReader.ReadPerson;
import org.example.model.Person;
import java.io.BufferedReader;
import static org.example.Main.*;

public class AddCommand implements Command{

    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        Person p = ReadPerson.read(consoleReader, true, -1);
        if (p != null) {
            collectionManager.addPerson(p);
            System.out.println("Element added. ID: " + p.getId());
        }
    }
}
