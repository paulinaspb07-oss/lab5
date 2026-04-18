package org.example.storage;
import org.example.model.Person;
import org.example.parser.XMLParser;

import java.io.*;
import java.util.Collection;
import java.util.List;

public class XmlFileStorage {
    public List<Person> load(String fileName) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
            return XMLParser.readPersons(reader);
        }
    }
    public void save(String fileName, Collection<Person> persons) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"))) {
            XMLParser.writePersons(writer, persons);
        }
    }
}
