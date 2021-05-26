package fr.epsi.entities;

import java.sql.Timestamp;
import java.util.List;

public class Demand {
    private String id;
    private Timestamp demandDate;
    private Float longitude;
    private Float latitude;
    private String gant;
    private String incident;
    private String step;
    private Project project;
    private List<User> users;
    private List<Subcontractor> subcontractors;

    public String getId() {
        return id;
    }

    public Demand setId(String id) {
        this.id = id;
        return this;
    }

    public Timestamp getDemandDate() {
        return demandDate;
    }

    public Demand setDemandDate(Timestamp demandDate) {
        this.demandDate = demandDate;
        return this;
    }

    public Float getLongitude() {
        return longitude;
    }

    public Demand setLongitude(Float longitude) {
        this.longitude = longitude;
        return this;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Demand setLatitude(Float latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getGant() {
        return gant;
    }

    public Demand setGant(String gant) {
        this.gant = gant;
        return this;
    }

    public String getIncident() {
        return incident;
    }

    public Demand setIncident(String incident) {
        this.incident = incident;
        return this;
    }

    public String getStep() {
        return step;
    }

    public Demand setStep(String step) {
        this.step = step;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public Demand setProject(Project project) {
        this.project = project;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Demand setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public List<Subcontractor> getSubcontractors() {
        return subcontractors;
    }

    public Demand setSubcontractors(List<Subcontractor> subcontractors) {
        this.subcontractors = subcontractors;
        return this;
    }
}
