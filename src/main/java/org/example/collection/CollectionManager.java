package org.example.collection;

import org.example.model.Person;
import org.example.storage.DbStorage;
import java.sql.SQLException;
import java.util.*;

public class CollectionManager {
    private final Map<Integer, Person> collection = new HashMap<>();
    private final DbStorage dbStorage;
    private static final Date initDate = new Date();

    public CollectionManager(DbStorage dbStorage) {
        this.dbStorage = dbStorage;
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        try {
            List<Person> persons = dbStorage.loadAllPersons();
            for (Person p : persons) {
                collection.put(p.getId(), p);
                Person.updateNextId(p.getId());
            }
            System.out.println("Loaded " + persons.size() + " persons from database.");
        } catch (SQLException e) {
            System.err.println("ERROR: Could not load data from database: " + e.getMessage());
            // Continue with empty collection
        }
    }

    public String getInfo() {
        return "Type: HashMap<Person>\nInitialization date: " + initDate + "\nNumber of elements: " + collection.size();
    }

    public void show() {
        if (collection.isEmpty()) {
            System.out.println("Collection is empty.");
            return;
        }
        collection.values().forEach(System.out::println);
    }

    public void addPerson(Person p, int ownerId) {
        try {
            dbStorage.insertPerson(p, ownerId);  // p.id gets set by DB
            p.setOwnerID(ownerId);
            collection.put(p.getId(), p);
            System.out.println("Person added with ID: " + p.getId());
        } catch (SQLException e) {
            System.err.println("Database error while adding person: " + e.getMessage());
        }
    }

    public Person getPersonById(int id) {
        return collection.get(id);
    }

    public boolean updatePerson(int id, Person newPerson, int userId) {
        if (!canModify(id, userId)) return false;
        Person oldPerson = collection.get(id);
        newPerson.setId(id);
        newPerson.setOwnerID(oldPerson.getOwnerID());
        newPerson.setCreationDate(oldPerson.getCreationDate());
        try {
            dbStorage.updatePerson(newPerson);
            collection.put(id, newPerson);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while updating: " + e.getMessage());
            return false;
        }
    }

    public boolean canModify(int id, int userId) {
        Person p = collection.get(id);
        return p != null && p.getOwnerID() == userId;
    }

    public boolean removeById(int id, int userId) {
        if (!canModify(id, userId)) return false;
        try {
            if (dbStorage.deletePerson(id, userId)) {
                collection.remove(id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database error while deleting: " + e.getMessage());
        }
        return false;
    }

    public int clearByOwner(int userId) {
        List<Integer> toRemove = new ArrayList<>();
        for (Person p : collection.values()) {
            if (p.getOwnerID() == userId) {
                toRemove.add(p.getId());
            }
        }
        try {
            dbStorage.clearByOwner(userId);
            for (int id : toRemove) {
                collection.remove(id);
            }
            return toRemove.size();
        } catch (SQLException e) {
            System.err.println("Database error while clearing: " + e.getMessage());
            return 0;
        }
    }

    public Collection<Person> getAllPersons() {
        return collection.values();
    }

    public boolean isLessThanMin(Person p) {
        return collection.values().stream()
                .min(Person::compareTo)
                .map(min -> p.compareTo(min) < 0)
                .orElse(true);
    }

    public int removeGreater(Person reference, int userId) {
        List<Integer> toRemove = new ArrayList<>();
        for (Person p : collection.values()) {
            if (p.getOwnerID() == userId && p.compareTo(reference) > 0) {
                toRemove.add(p.getId());
            }
        }
        for (int id : toRemove) {
            try {
                dbStorage.deletePerson(id, userId);
                collection.remove(id);
            } catch (SQLException e) {
                System.err.println("Error deleting greater element: " + e.getMessage());
            }
        }
        return toRemove.size();
    }

    public int removeLower(Person reference, int userId) {
        List<Integer> toRemove = new ArrayList<>();
        for (Person p : collection.values()) {
            if (p.getOwnerID() == userId && p.compareTo(reference) < 0) {
                toRemove.add(p.getId());
            }
        }
        for (int id : toRemove) {
            try {
                dbStorage.deletePerson(id, userId);
                collection.remove(id);
            } catch (SQLException e) {
                System.err.println("Error deleting lower element: " + e.getMessage());
            }
        }
        return toRemove.size();
    }

    public void filterStartsWithName(String prefix) {
        boolean found = false;
        for (Person p : collection.values()) {
            if (p.getName().startsWith(prefix)) {
                System.out.println(p);
                found = true;
            }
        }
        if (!found) System.out.println("No elements found.");
    }

    public void printAscending() {
        collection.values().stream().sorted().forEach(System.out::println);
    }

    public void printDescending() {
        collection.values().stream().sorted(Comparator.reverseOrder()).forEach(System.out::println);
    }
}