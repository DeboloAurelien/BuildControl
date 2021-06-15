package fr.epsi.controllers;

import fr.epsi.App;
import fr.epsi.entities.Demand;
import fr.epsi.entities.DemandInfo;
import fr.epsi.entities.Project;
import fr.epsi.services.DemandService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

public class DemandController implements Initializable {
    DemandService demandService = new DemandService();

    @FXML private TableView<Demand> tableDemand;
    @FXML private TableView<DemandInfo> tableDemandInfo;
    @FXML private DatePicker calendar;
    @FXML private ComboBox<String> inputSelectDemandId;
    @FXML private TextField createDemandId;
    @FXML private DatePicker createDemandDate;
    @FXML private TextField createDemandLongitude;
    @FXML private TextField createDemandLatitude;
    @FXML private TextField createDemandGant;
    @FXML private TextField createDemandIncident;
    @FXML private TextField createDemandStep;
    @FXML private TextField createDemandProjectId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableDemand.setVisible(true);
        tableDemandInfo.setVisible(false);

        try {
            List<Demand> demands = demandService.getAllDemands();

            for (Demand demand : demands) {
                inputSelectDemandId.getItems().add(demand.getId());
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @FXML
    private void createDemand() throws SQLException {
        Demand demand = new Demand()
                .setId(createDemandId.getText())
                .setDemandDate(localDateToTimestamp(createDemandDate.getValue()))
                .setLongitude(Float.parseFloat(createDemandLongitude.getText()))
                .setLatitude(Float.parseFloat(createDemandLatitude.getText()))
                .setGant(createDemandGant.getText())
                .setIncident(createDemandIncident.getText())
                .setStep(createDemandStep.getText())
                .setProject(new Project().setId(Long.parseLong(createDemandProjectId.getText())));

        demandService.createDemand(demand);
        getAllDemands();
    }

    @FXML
    private void getDemandInfo() throws Exception {
        String demandId = inputSelectDemandId.getValue();

        tableDemand.setVisible(false);
        tableDemandInfo.setVisible(true);

        if (demandId != null) {
            List<DemandInfo> demands = demandService.getDemandInfo(demandId);

            TableColumn<DemandInfo, String> ids = new TableColumn<>("Référence");
            ids.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId()));

            TableColumn<DemandInfo, String> demandDates = new TableColumn<>("Date de demande");
            demandDates.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDemandDate().toString()));

            TableColumn<DemandInfo, String> projects = new TableColumn<>("Projet");
            projects.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProjectName()));

            TableColumn<DemandInfo, String> sites = new TableColumn<>("Chantier");
            sites.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSiteName()));

            TableColumn<DemandInfo, String> usernames = new TableColumn<>("Utilisateur");
            usernames.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUsername()));

            TableColumn<DemandInfo, String> userProfessions = new TableColumn<>("Corps métier utilisateur");
            userProfessions.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUserProfession()));

            TableColumn<DemandInfo, String> subcontractors = new TableColumn<>("Sous-traitant");
            subcontractors.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSubcontractorName()));

            TableColumn<DemandInfo, String> subcontractorProfessions = new TableColumn<>("Corps métier sous-traitant");
            subcontractorProfessions.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSubcontractorProfession()));

            TableColumn<DemandInfo, String> paths = new TableColumn<>("Capture");
            paths.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPath()));

            ObservableList<DemandInfo> demandData = FXCollections.observableArrayList(demands);

            tableDemandInfo.getColumns().setAll(ids,demandDates,projects,sites,usernames,userProfessions,subcontractors,subcontractorProfessions,paths);
            tableDemandInfo.setItems(demandData);
        }
    }

    @FXML
    private void getDemandAfterDate() throws SQLException {
        if (calendar.getValue() != null) {
            showDemands(demandService.getDemandAfterDate(localDateToTimestamp(calendar.getValue())));
        } else {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            showDemands(demandService.getDemandAfterDate(now));
        }
    }

    @FXML
    private void getAllDemands() throws SQLException {
        showDemands(demandService.getAllDemands());
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

    private void showDemands(List<Demand> demands) {
        tableDemand.setVisible(true);
        tableDemandInfo.setVisible(false);

        TableColumn<Demand, String> ids = new TableColumn<>("Référence");
        ids.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId()));

        TableColumn<Demand, String> demandDates = new TableColumn<>("Date de demande");
        demandDates.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDemandDate().toString()));

        TableColumn<Demand, String> longitudes = new TableColumn<>("Longitude");
        longitudes.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLongitude().toString()));

        TableColumn<Demand, String> latitudes = new TableColumn<>("Latitude");
        latitudes.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLatitude().toString()));

        TableColumn<Demand, String> gants = new TableColumn<>("Gant");
        gants.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGant()));

        TableColumn<Demand, String> incidents = new TableColumn<>("Incident");
        incidents.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getIncident()));

        TableColumn<Demand, String> steps = new TableColumn<>("Étape");
        steps.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStep()));

        TableColumn<Demand, String> projects = new TableColumn<>("Id du projet");
        projects.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getProject().getId().toString()));

        TableColumn<Demand, String> users = new TableColumn<>("NB de utilisateurs");
        users.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getUsers().size())));

        TableColumn<Demand, String> subcontractors = new TableColumn<>("NB de sous-traitans");
        subcontractors.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getSubcontractors().size())));

        TableColumn<Demand, String> medias = new TableColumn<>("NB de captures");
        medias.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getMedias().size())));

        ObservableList<Demand> demandData = FXCollections.observableArrayList(demands);

        tableDemand.getColumns().setAll(ids,demandDates,longitudes,latitudes,gants,incidents,steps,projects);
        tableDemand.setItems(demandData);
    }

    @FXML
    private void switchToIndex() throws IOException {
        App.setRoot("index");
    }
}
