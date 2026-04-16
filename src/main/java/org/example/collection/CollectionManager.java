package org.example.collection;

import org.example.model.Person;
import java.util.*;

public class CollectionManager {
    private static final Map<Integer, Person> collection = new HashMap<>();
    private static final Date initDate = new Date();

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

    public void addPerson(Person p) {
        if (p.getId() == 0) p.generateId();
        collection.put(p.getId(), p);
    }

    public Person getPersonById(int id) {
        return collection.get(id);
    }

    public void updatePerson(int id, Person newPerson) {
        if (collection.containsKey(id)) {
            newPerson.setId(id);
            collection.put(id, newPerson);
        }
    }

    public void removeById(int id) {
        if (collection.remove(id) != null) System.out.println("Element removed.");
        else System.out.println("Element not found.");
    }

    public void clear() {
        collection.clear();
        System.out.println("Collection cleared.");
    }

    public Collection<Person> getAllPersons() {
        return collection.values();
    }

    public static boolean isLessThanMin(Person p) {
        return collection.values().stream().min(Person::compareTo).map(min -> p.compareTo(min) < 0).orElse(true);
    }

    public int removeGreater(Person p) {
        List<Integer> toRemove = new ArrayList<>();
        for (Person person : collection.values()) {
            if (person.compareTo(p) > 0) toRemove.add(person.getId());
        }
        toRemove.forEach(collection::remove);
        return toRemove.size();
    }

    public int removeLower(Person p) {
        List<Integer> toRemove = new ArrayList<>();
        for (Person person : collection.values()) {
            if (person.compareTo(p) < 0) toRemove.add(person.getId());
        }
        toRemove.forEach(collection::remove);
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
