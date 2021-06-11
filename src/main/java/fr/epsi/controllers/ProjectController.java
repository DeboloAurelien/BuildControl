package fr.epsi.controllers;

import fr.epsi.App;
import fr.epsi.entities.ProjectSubcontractor;
import fr.epsi.services.ProjectService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.GregorianCalendar;

public class ProjectController {
    ProjectService projectService = new ProjectService();

    @FXML private TableView<ProjectSubcontractor> tableProjectSubcontractor;
    @FXML private DatePicker projectDate;
    @FXML private Label nbFinishedProject;
    @FXML private DatePicker projectStartDate;
    @FXML private DatePicker projectEndDate;
    @FXML private Label nbFinishedProjectPeriod;

    @FXML
    private void getNbFinishedProject() throws SQLException {
        int year = projectDate.getValue().getYear();
        int month = projectDate.getValue().getMonthValue();

        Long nbProject = projectService.getNbProjectsFinished(year, month);

        nbFinishedProject.setText("Il y a " + nbProject + " projet(s) terminé(s) depuis le " + month + "/" + year + ".");
    }

    @FXML
    private void getNbFinishedProjectPeriod() throws SQLException {
        Timestamp start = localDateToTimestamp(projectStartDate.getValue());
        Timestamp end = localDateToTimestamp(projectEndDate.getValue());

        Long nbProject = projectService.getNbProjectsFinishedPeriod(start, end);

        nbFinishedProjectPeriod.setText("Il y a " + nbProject + " projet(s) terminé(s) depuis entre " + start + " et " + end + ".");
    }

    @FXML
    private void getNbSubcontractorByProject() throws SQLException {
        TableColumn<ProjectSubcontractor, String> names = new TableColumn<>("Nom projet");
        names.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));

        TableColumn<ProjectSubcontractor, String> labels = new TableColumn<>("Libellé corps de métier");
        labels.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLabel()));

        TableColumn<ProjectSubcontractor, String> subcontractors = new TableColumn<>("Nb de sous-traitants");
        subcontractors.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getNbSubcontractors())));

        ObservableList<ProjectSubcontractor> projectData = FXCollections.observableArrayList(projectService.getNbSubcontractorByProject());

        tableProjectSubcontractor.getColumns().setAll(names,labels,subcontractors);
        tableProjectSubcontractor.setItems(projectData);
    }

    private Timestamp localDateToTimestamp(LocalDate localDate) {
        String[] dateItems = localDate.toString().split("-");

        assert dateItems.length == 3;

        int year = Integer.parseInt(dateItems[0]);
        int month = Integer.parseInt(dateItems[1]) - 1;
        int day = Integer.parseInt(dateItems[2]);

        GregorianCalendar cal = new GregorianCalendar(year, month, day);
        long millis = cal.getTimeInMillis();
        return new Timestamp(millis);
    }

    @FXML
    private void switchToIndex() throws IOException {
        App.setRoot("index");
    }
}
