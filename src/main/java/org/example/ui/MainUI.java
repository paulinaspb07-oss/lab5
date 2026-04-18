//mvn javafx:run
package org.example.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.collection.CollectionManager;
import org.example.model.*;
import org.example.storage.XmlFileStorage;
import java.io.*;
import java.util.List;

public class MainUI extends Application {

    private TableView<Person> tableView = new TableView<>();
    private ObservableList<Person> personList = FXCollections.observableArrayList();
    private CollectionManager collectionManager = new CollectionManager();
    private String fileName;
    private XmlFileStorage fileStorage = new XmlFileStorage();

    @Override
    public void start(Stage primaryStage) {
        fileName = System.getenv("FILE_NAME");
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "data.xml";
        }

        // Load data from XML at startup
        loadDataFromFile();

        // Build table columns
        buildTableColumns();

        // Create buttons
        Button addBtn = new Button("Add");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");
        Button saveBtn = new Button("Save");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        deleteBtn.setOnAction(e -> deleteSelected());
        refreshBtn.setOnAction(e -> refreshFromFile());
        saveBtn.setOnAction(e -> saveToFile());

        HBox buttonBar = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn, saveBtn);
        buttonBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(buttonBar);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 1100, 650);
        primaryStage.setTitle("Laboratory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void buildTableColumns() {
        tableView.getColumns().clear();

        TableColumn<Person, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, Float> heightCol = new TableColumn<>("Height");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("height"));

        TableColumn<Person, Color> hairCol = new TableColumn<>("Hair Color");
        hairCol.setCellValueFactory(new PropertyValueFactory<>("hairColor"));

        TableColumn<Person, Country> natCol = new TableColumn<>("Nationality");
        natCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));

        TableColumn<Person, String> coordsCol = new TableColumn<>("Coordinates");
        coordsCol.setCellValueFactory(c -> {
            Coordinates coord = c.getValue().getCoordinates();
            return new javafx.beans.property.SimpleStringProperty(coord == null ? "" : coord.toString());
        });

        TableColumn<Person, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(c -> {
            Location loc = c.getValue().getLocation();
            return new javafx.beans.property.SimpleStringProperty(loc == null ? "" : loc.toString());
        });

        tableView.getColumns().addAll(idCol, nameCol, heightCol, hairCol, natCol, coordsCol, locCol);
        tableView.setItems(personList);
    }

    private void loadDataFromFile() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found. Starting with empty collection.");
            collectionManager.clear();
            refreshTable();
            return;
        }
        try {
            List<Person> persons = fileStorage.load(fileName);
            collectionManager.clear();
            for (Person p : persons) {
                collectionManager.addPerson(p);
            }
            refreshTable();
        } catch (Exception e) {
            showAlert("Load Error", "Could not load from file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try {
            fileStorage.save(fileName, collectionManager.getAllPersons());
            showAlert("Saved", "Data saved to " + fileName);
        } catch (Exception e) {
            showAlert("Save Error", "Failed to save: " + e.getMessage());
        }
    }

    private void refreshFromFile() {
        loadDataFromFile();
    }

    private void refreshTable() {
        personList.clear();
        personList.addAll(collectionManager.getAllPersons());
        //Maybe try to change it so that it saves time 
    }

    private void showAddDialog() {
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Add Person");
        dialog.setHeaderText("Enter all fields");

        // Simple text fields for all properties
        TextField nameField = new TextField();
        TextField heightField = new TextField();
        TextField coordXField = new TextField();
        TextField coordYField = new TextField();
        TextField locXField = new TextField();
        TextField locYField = new TextField();
        TextField locZField = new TextField();
        ComboBox<Color> hairColorBox = new ComboBox<>(FXCollections.observableArrayList(Color.values()));
        ComboBox<Country> nationalityBox = new ComboBox<>(FXCollections.observableArrayList(Country.values()));

        VBox form = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Height (float > 0):"), heightField,
                new Label("Hair Color:"), hairColorBox,
                new Label("Nationality:"), nationalityBox,
                new Label("Coordinates X (long):"), coordXField,
                new Label("Coordinates Y (double):"), coordYField,
                new Label("Location X (double):"), locXField,
                new Label("Location Y (Double):"), locYField,
                new Label("Location Z (Integer):"), locZField
        );
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    float height = Float.parseFloat(heightField.getText());
                    long coordX = Long.parseLong(coordXField.getText());
                    double coordY = Double.parseDouble(coordYField.getText());
                    Coordinates coords = new Coordinates(coordX, coordY);
                    double locX = Double.parseDouble(locXField.getText());
                    Double locY = Double.parseDouble(locYField.getText());
                    Integer locZ = Integer.parseInt(locZField.getText());
                    Location location = new Location(locX, locY, locZ);
                    Person p = new Person(
                            nameField.getText(),
                            coords,
                            height,
                            null, 
                            hairColorBox.getValue(),
                            nationalityBox.getValue(),
                            location
                    );
                    return p;
                } catch (Exception e) {
                    showAlert("Input Error", "Invalid data: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(person -> {
            try {
                collectionManager.addPerson(person);
                refreshTable();
            } catch (IllegalArgumentException e) {
                showAlert("Validation Error", e.getMessage());
            }
        });
    }

    private void showEditDialog() {
        Person selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a person to edit.");
            return;
        }
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Edit Person");
        dialog.setHeaderText("Change values");

        TextField nameField = new TextField(selected.getName());
        TextField heightField = new TextField(String.valueOf(selected.getHeight()));
        ComboBox<Color> hairColorBox = new ComboBox<>(FXCollections.observableArrayList(Color.values()));
        hairColorBox.setValue(selected.getHairColor());
        ComboBox<Country> nationalityBox = new ComboBox<>(FXCollections.observableArrayList(Country.values()));
        nationalityBox.setValue(selected.getNationality());

        VBox form = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Height:"), heightField,
                new Label("Hair Color:"), hairColorBox,
                new Label("Nationality:"), nationalityBox
        );
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                try {
                    Person updated = new Person(
                            nameField.getText(),
                            selected.getCoordinates(),
                            Float.parseFloat(heightField.getText()),
                            selected.getBirthday(),
                            hairColorBox.getValue(),
                            nationalityBox.getValue(),
                            selected.getLocation()
                    );
                    updated.setId(selected.getId());
                    updated.setCreationDate(selected.getCreationDate());
                    return updated;
                } catch (Exception e) {
                    showAlert("Input Error", "Invalid data: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            try {
                collectionManager.updatePerson(updated.getId(), updated);
                refreshTable();
            } catch (IllegalArgumentException e) {
                showAlert("Validation Error", e.getMessage());
            }
        });
    }

    private void deleteSelected() {
        Person selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a person to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            collectionManager.removeById(selected.getId());
            refreshTable();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//mvn javafx:run

