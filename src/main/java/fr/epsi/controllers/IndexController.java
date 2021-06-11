package fr.epsi.controllers;

import java.io.IOException;
import fr.epsi.App;
import javafx.fxml.FXML;

public class IndexController {

    @FXML
    private void switchToUser() throws IOException {
        App.setRoot("user");
    }

    @FXML
    private void switchToDemand() throws IOException {
        App.setRoot("demand");
    }

    @FXML
    private void switchToProject() throws IOException {
        App.setRoot("project");
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
}
