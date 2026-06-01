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
import org.example.auth.User;
import org.example.auth.UserFileStorage;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainUI extends Application {

    private TableView<Person> tableView = new TableView<>();
    private ObservableList<Person> personList = FXCollections.observableArrayList();
    private CollectionManager collectionManager = new CollectionManager();
    private String fileName;
    private XmlFileStorage fileStorage = new XmlFileStorage();
    private UserFileStorage userFileStorage = new UserFileStorage("users.csv");
    private User currentUser;
    private Button editBtn;
    private Button deleteBtn;

    @Override
    public void start(Stage primaryStage) {
        fileName = System.getenv("FILE_NAME");
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "data.xml";
        }

        showLoginWindow(primaryStage);
    }

    private void showLoginWindow(Stage stage) {
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        loginBtn.setOnAction(e -> {
            User user = userFileStorage.login(loginField.getText(), passwordField.getText());

            if (user == null) {
                showAlert("Ошибка входа", "Неверный логин или пароль");
                return;
            }

            currentUser = user;
            openMainWindow(stage);
        });

        registerBtn.setOnAction(e -> {
            try {
                currentUser = userFileStorage.register(loginField.getText(), passwordField.getText());
                showAlert("Регистрация", "Пользователь создан: ");
                openMainWindow(stage);
            } catch (Exception ex) {
                showAlert("Ошибка регистрации", ex.getMessage());
            }
        });

        VBox root = new VBox(10,
                new Label("Логин:"), loginField,
                new Label("Пароль"), passwordField,
                new HBox(10, loginBtn, registerBtn)
        );

        root.setPadding(new Insets(20));

        stage.setTitle("Авторизация");
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

    private void openMainWindow(Stage primaryStage) {
        loadDataFromFile();
        buildTableColumns();

     
        Button addBtn = new Button("Add");
        editBtn = new Button("Edit");
        deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");
        Button saveBtn = new Button("Save");

        editBtn.setDisable(true);
        deleteBtn.setDisable(true);

        // Neeeew
        Button clearAllBtn = new Button("Clear All");
        Button removeMyBtn = new Button("Remove My");
        Button removeGreaterBtn = new Button("Remove Greater");
        Button removeLowerBtn = new Button("Remove Lower");
        Button filterBtn = new Button("Filter by Prefix");
        Button infoBtn = new Button("Info");
        Button sortAscBtn = new Button("Sort ↑");
        Button sortDescBtn = new Button("Sort ↓");

  
        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        deleteBtn.setOnAction(e -> deleteSelected());
        refreshBtn.setOnAction(e -> refreshFromFile());
        saveBtn.setOnAction(e -> saveToFile());

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            boolean canEdit = selected != null && selected.getOwnerID() == currentUser.getId();
            editBtn.setDisable(!canEdit);
            deleteBtn.setDisable(!canEdit);
        });


        clearAllBtn.setOnAction(e -> {
            Stage adminStage = new Stage();
            adminStage.setTitle("Admin Authentication");

            Label userLabel = new Label("Username:");
            TextField userField = new TextField();
            Label passLabel = new Label("Password:");
            PasswordField passField = new PasswordField();
            Button loginBtn = new Button("Login");
            Button cancelBtn = new Button("Cancel");

            VBox adminRoot = new VBox(10, userLabel, userField, passLabel, passField,
                    new HBox(10, loginBtn, cancelBtn));
            adminRoot.setPadding(new Insets(20));

            adminStage.setScene(new Scene(adminRoot, 300, 200));

            loginBtn.setOnAction(ev -> {
                if ("admin".equals(userField.getText()) && "admin123".equals(passField.getText())) {
                    collectionManager.clear();
                    refreshTable();
                    showAlert("Cleared", "Collection cleared by admin.");
                    adminStage.close();
                } else {
                    showAlert("Authentication Failed", "Invalid admin credentials.");
                }
            });

            cancelBtn.setOnAction(ev -> adminStage.close());
            adminStage.show();
        });

     
        removeMyBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Remove all your own elements?", ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Remove My Items");
            if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                int removed = collectionManager.clearByOwner(currentUser.getId());
                refreshTable();
                showAlert("Removed", removed + " element(s) deleted.");
            }
        });

     
        removeGreaterBtn.setOnAction(e -> {
            Dialog<Person> dialog = new Dialog<>();
            dialog.setTitle("Remove Greater");
            dialog.setHeaderText("Enter fields for comparison (all fields used in compareTo)");

            TextField nameField = new TextField();
            TextField heightField = new TextField();
            ComboBox<Color> hairColorBox = new ComboBox<>(FXCollections.observableArrayList(Color.values()));
            ComboBox<Country> nationalityBox = new ComboBox<>(FXCollections.observableArrayList(Country.values()));

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
                        return new Person(
                                nameField.getText(),
                                new Coordinates(0, 0),
                                Float.parseFloat(heightField.getText()),
                                null,
                                hairColorBox.getValue(),
                                nationalityBox.getValue(),
                                new Location(0.0, 0.0, 0)
                        );
                    } catch (Exception ex) {
                        showAlert("Input Error", ex.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(reference -> {
                int count = collectionManager.removeGreater(reference);
                refreshTable();
                showAlert("Removed Greater", count + " element(s) removed.");
            });
        });

  
        removeLowerBtn.setOnAction(e -> {
            Dialog<Person> dialog = new Dialog<>();
            dialog.setTitle("Remove Lower");
            dialog.setHeaderText("Enter fields for comparison");

            TextField nameField = new TextField();
            TextField heightField = new TextField();
            ComboBox<Color> hairColorBox = new ComboBox<>(FXCollections.observableArrayList(Color.values()));
            ComboBox<Country> nationalityBox = new ComboBox<>(FXCollections.observableArrayList(Country.values()));

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
                        return new Person(
                                nameField.getText(),
                                new Coordinates(0, 0),
                                Float.parseFloat(heightField.getText()),
                                null,
                                hairColorBox.getValue(),
                                nationalityBox.getValue(),
                                new Location(0.0, 0.0, 0)
                        );
                    } catch (Exception ex) {
                        showAlert("Input Error", ex.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(reference -> {
                int count = collectionManager.removeLower(reference);
                refreshTable();
                showAlert("Removed Lower", count + " element(s) removed.");
            });
        });

        // Filter by Name Prefix
        filterBtn.setOnAction(e -> {
            TextInputDialog input = new TextInputDialog();
            input.setTitle("Filter by Name Prefix");
            input.setHeaderText("Enter prefix (case‑sensitive)");
            input.showAndWait().ifPresent(prefix -> {
                List<Person> matches = collectionManager.getAllPersons().stream()
                        .filter(p -> p.getName().startsWith(prefix))
                        .collect(Collectors.toList());

                if (matches.isEmpty()) {
                    showAlert("Filter Result", "No elements found.");
                } else {
                    Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                    resultAlert.setTitle("Matching Elements");
                    resultAlert.setHeaderText(matches.size() + " element(s) found");

                    ListView<String> listView = new ListView<>();
                    matches.forEach(p -> listView.getItems().add(p.toString()));
                    resultAlert.getDialogPane().setContent(listView);
                    resultAlert.showAndWait();
                }
            });
        });

        // Show collection info
        infoBtn.setOnAction(e -> {
            String info = collectionManager.getInfo();
            showAlert("Collection Info", info);
        });

        // Sorting
        sortAscBtn.setOnAction(e -> FXCollections.sort(personList));
        sortDescBtn.setOnAction(e -> FXCollections.sort(personList, Comparator.reverseOrder()));

        // Layout setup
        Label userLabel = new Label("Current user: " + currentUser.getLogin() + " | id = " + currentUser.getId());

        HBox buttonBar = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn, saveBtn);
        buttonBar.setPadding(new Insets(10));

        HBox extraButtonBar = new HBox(10,
                clearAllBtn, removeMyBtn, removeGreaterBtn, removeLowerBtn, filterBtn, infoBtn
        );
        extraButtonBar.setPadding(new Insets(10));

        HBox sortBar = new HBox(10, sortAscBtn, sortDescBtn);
        sortBar.setPadding(new Insets(5));

        VBox topPanel = new VBox(5, userLabel, buttonBar, extraButtonBar, sortBar);

        BorderPane root = new BorderPane();
        root.setTop(topPanel);
        root.setCenter(tableView);

        Scene scene = new Scene(root, 1100, 650);
        primaryStage.setTitle("Laboratory Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private void buildTableColumns() {
        tableView.getColumns().clear();
        tableView.setItems(personList);

        TableColumn<Person, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Person, Integer> ownerCol = new TableColumn<>("Owner ID");
        ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerID"));

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, Float> heightCol = new TableColumn<>("Height");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("height"));

        TableColumn<Person, Color> hairCol = new TableColumn<>("Hair Color");
        hairCol.setCellValueFactory(new PropertyValueFactory<>("hairColor"));

        TableColumn<Person, Country> natCol = new TableColumn<>("Nationality");
        natCol.setCellValueFactory(new PropertyValueFactory<>("nationality"));

        TableColumn<Person, Coordinates> coordsCol = new TableColumn<>("Coordinates");
        coordsCol.setCellValueFactory(new PropertyValueFactory<>("coordinates"));

        TableColumn<Person, Location> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        tableView.getColumns().addAll(
                idCol, ownerCol, nameCol, heightCol, hairCol, natCol, coordsCol, locCol
        );
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
    }

    private void showAddDialog() {
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Add Person");
        dialog.setHeaderText("Enter all fields");

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
                collectionManager.addPerson(person, currentUser.getId());
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
        if (selected.getOwnerID() != currentUser.getId()) {
            showAlert("Access denied", "Вы не можете изменять чужой объект");
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
                boolean success = collectionManager.updatePerson(updated.getId(), updated, currentUser.getId());

                if (!success) {
                    showAlert("Access denied", "Вы не можете изменять чужой объект");
                    return;
                }

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
        if (selected.getOwnerID() != currentUser.getId()) {
            showAlert("Access denied", "Вы не можете удалять чужой объект");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            boolean success = collectionManager.removeById(selected.getId(), currentUser.getId());

            if (!success) {
                showAlert("Access denied", "Вы не можете удалять чужой объект");
                return;
            }

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