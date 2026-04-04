package org.example.commands;

import org.example.collection.CollectionManager;
import org.example.model.Person;
import java.io.BufferedReader;
import static org.example.Main.readPerson;

public class AddComand implements Command{

    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        Person p = readPerson(consoleReader, true, -1);
        if (p != null) {
            CollectionManager.addPerson(p);
            System.out.println("Element added. ID: " + p.getId());
        }
    }
}
