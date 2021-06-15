package fr.epsi.controllers;

import fr.epsi.App;
import fr.epsi.entities.*;
import fr.epsi.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserController {
    private final UserService userService = new UserService();

    @FXML
    private TableView<User> tableUser;

    @FXML
    private void getAllUsers() throws SQLException {
        List<User> users = userService.getAll();

        TableColumn<User, String> ids = new TableColumn<>("Id");
        ids.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId().toString()));

        TableColumn<User, String> usernames = new TableColumn<>("Nom d'utilisateur");
        usernames.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUsername()));

        TableColumn<User, String> passwords = new TableColumn<>("Mot de passe");
        passwords.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPasswordHash()));

        TableColumn<User, String> dateLastConnections = new TableColumn<>("Dernière connexion");

        dateLastConnections.setCellValueFactory(cd -> {
            if (cd.getValue().getDateLastConnection() != null) {
                return new SimpleStringProperty(cd.getValue().getDateLastConnection().toString());
            }
            return null;
        });

        TableColumn<User, String> numberConnectionAttempts = new TableColumn<>("Nom");
        numberConnectionAttempts.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getNumberConnectionAttempt())));

        TableColumn<User, String> lastnames = new TableColumn<>("Nom");
        lastnames.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLastname()));

        TableColumn<User, String> firstnames = new TableColumn<>("Prénom");
        firstnames.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFirstname()));

        TableColumn<User, String> archives = new TableColumn<>("Archivé");
        archives.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getArchive().toString()));

        TableColumn<User, String> professions = new TableColumn<>("Corps de métier");
        professions.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getProfession().getId())));

        TableColumn<User, String> roles = new TableColumn<>("Nb roles");
        roles.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getRoles().size())));

        TableColumn<User, String> demands = new TableColumn<>("Nb demandes");
        demands.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getDemands().size())));

        TableColumn<User, String> sites = new TableColumn<>("Nb chantiers");
        sites.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getSites().size())));

        ObservableList<User> userData = FXCollections.observableArrayList(users);

        tableUser.getColumns().setAll(
                ids,usernames,passwords,dateLastConnections,
                numberConnectionAttempts,lastnames,firstnames,
                archives,professions,roles,demands,sites);
        tableUser.setItems(userData);
    }

    @FXML
    private void switchToIndex() throws IOException {
        App.setRoot("index");
    }
}
