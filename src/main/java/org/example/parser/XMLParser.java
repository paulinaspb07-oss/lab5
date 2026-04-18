package org.example.parser;

import org.example.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class XMLParser {
    public static List<Person> readPersons(BufferedReader reader) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(reader));
        doc.getDocumentElement().normalize();

        NodeList personNodes = doc.getElementsByTagName("person");
        List<Person> persons = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();
        for (int i = 0; i < personNodes.getLength(); i++) {
            Element personElem = (Element) personNodes.item(i);
                int id = Integer.parseInt(getTagValue("id", personElem));
                if (!usedIds.add(id)) {
                    throw new IllegalArgumentException("Duplicate id in XML: " + id);
                }
                String name = getTagValue("name", personElem);
                Coordinates coords = readCoordinates(getChildElement(personElem, "coordinates"));
                Date creationDate = new Date(Long.parseLong(getTagValue("creationDate", personElem)));
                float height = Float.parseFloat(getTagValue("height", personElem));
                LocalDateTime birthday = null;
                Element bdayElem = getChildElement(personElem, "birthday");
                if (bdayElem != null) birthday = LocalDateTime.parse(bdayElem.getTextContent(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                Color hairColor = null;
                Element hairElem = getChildElement(personElem, "hairColor");
                if (hairElem != null) hairColor = Color.valueOf(hairElem.getTextContent());
                Country nationality = null;
                Element natElem = getChildElement(personElem, "nationality");
                if (natElem != null) nationality = Country.valueOf(natElem.getTextContent());
                Location location = null;
                Element locElem = getChildElement(personElem, "location");
                if (locElem != null) location = readLocation(locElem);

                Person p = new Person(id, name, coords, creationDate, height, birthday, hairColor, nationality, location);
                persons.add(p);
        }
        return persons;
    }

    public static void writePersons(BufferedWriter writer, Collection<Person> persons) throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("persons");
        doc.appendChild(root);

        for (Person p : persons) {
            Element personElem = doc.createElement("person");
            root.appendChild(personElem);

            addElement(doc, personElem, "id", String.valueOf(p.getId()));
            addElement(doc, personElem, "name", p.getName());
            Element coordsElem = doc.createElement("coordinates");
            personElem.appendChild(coordsElem);
            addElement(doc, coordsElem, "x", String.valueOf(p.getCoordinates().getX()));
            addElement(doc, coordsElem, "y", String.valueOf(p.getCoordinates().getY()));
            addElement(doc, personElem, "creationDate", String.valueOf(p.getCreationDate().getTime()));
            addElement(doc, personElem, "height", String.valueOf(p.getHeight()));
            if (p.getBirthday() != null) addElement(doc, personElem, "birthday", p.getBirthday().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            if (p.getHairColor() != null) addElement(doc, personElem, "hairColor", p.getHairColor().name());
            if (p.getNationality() != null) addElement(doc, personElem, "nationality", p.getNationality().name());
            if (p.getLocation() != null) {
                Element locElem = doc.createElement("location");
                personElem.appendChild(locElem);
                addElement(doc, locElem, "x", String.valueOf(p.getLocation().getX()));
                addElement(doc, locElem, "y", String.valueOf(p.getLocation().getY()));
                addElement(doc, locElem, "z", String.valueOf(p.getLocation().getZ()));
            }
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
    }

    private static String getTagValue(String tag, Element element) {
        NodeList list = element.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        Node node = list.item(0);
        String text = node.getTextContent();
        return text == null ? null : text.trim();
    }

    private static Element getChildElement(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        return (Element) list.item(0);
    }

    private static Coordinates readCoordinates(Element coordsElem) {
        long x = Long.parseLong(getTagValue("x", coordsElem));
        double y = Double.parseDouble(getTagValue("y", coordsElem));
        return new Coordinates(x, y);
    }

    private static Location readLocation(Element locElem) {
        double x = Double.parseDouble(getTagValue("x", locElem));
        Double y = Double.parseDouble(getTagValue("y", locElem));
        Integer z = Integer.parseInt(getTagValue("z", locElem));
        return new Location(x, y, z);
    }

    private static void addElement(Document doc, Element parent, String tag, String value) {
        Element elem = doc.createElement(tag);
        elem.appendChild(doc.createTextNode(value));
        parent.appendChild(elem);
    }
}
