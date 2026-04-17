package org.example.commands;


import org.example.inputreader.ReadPerson;
import org.example.model.Person;
import java.io.BufferedReader;
import static org.example.Main.*;

public class UpdateCommand implements Command{
    @Override
    public void execute(String[] args, BufferedReader consoleReader) throws Exception {
        if (args.length < 2) throw new IllegalArgumentException("Missing id.");
        String[] updParts = args[1].split("\\s+", 2);
        if (!updParts[0].equals("id")) throw new IllegalArgumentException("Format: update id <id>");
        int id = Integer.parseInt(updParts[1]);
        Person old = collectionManager.getPersonById(id);
        if (old == null) {
            System.out.println("Element with id " + id + " not found.");
            return;
        }
        Person updated = ReadPerson.read(consoleReader, true, id);
        if (updated != null) {
            updated.setId(old.getId());
            updated.setCreationDate(old.getCreationDate());
            collectionManager.updatePerson(id, updated);
            System.out.println("Element updated.");
        }
    }
}
