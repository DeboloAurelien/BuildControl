package fr.epsi.controllers;

import fr.epsi.App;
import fr.epsi.entities.User;
import fr.epsi.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    UserService userService = new UserService();

    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Label resultLogin;

    @FXML
    private void login() throws SQLException {
        User user = new User()
                .setUsername(username.getText())
                .setPasswordHash(password.getText());

        int response = userService.login(user);

        if (response == 1) {
            resultLogin.setText("Connexion établie.");
        } else if (response == -1) {
            resultLogin.setText("Votre compte a été bloqué suite à 3 tentatives de connexion. Veuillez contacter le support informatique");
        } else {
            resultLogin.setText("Nom d'utilisateur ou mot de passe érroné !");
        }
    }

    @FXML
    private void switchToIndex() throws IOException {
        App.setRoot("index");
    }
}
