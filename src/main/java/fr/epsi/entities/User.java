package fr.epsi.entities;

import java.sql.Timestamp;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Timestamp dateLastConnection;
    private Timestamp dateChangePassword;
    private Long numberConnectionAttempt;
    private String lastname;
    private String firstname;
    private Boolean archive;
    private Profession profession;
    private List<Role> roles;
    private List<Demand> demands;
    private List<Site> sites;

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public User setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public Timestamp getDateLastConnection() {
        return dateLastConnection;
    }

    public User setDateLastConnection(Timestamp dateLastConnection) {
        this.dateLastConnection = dateLastConnection;
        return this;
    }

    public Timestamp getDateChangePassword() {
        return dateChangePassword;
    }

    public User setDateChangePassword(Timestamp dateChangePassword) {
        this.dateChangePassword = dateChangePassword;
        return this;
    }

    public Long getNumberConnectionAttempt() {
        return numberConnectionAttempt;
    }

    public User setNumberConnectionAttempt(Long numberConnectionAttempt) {
        this.numberConnectionAttempt = numberConnectionAttempt;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public User setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public String getFirstname() {
        return firstname;
    }

    public User setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public Boolean getArchive() {
        return archive;
    }

    public User setArchive(Boolean archive) {
        this.archive = archive;
        return this;
    }

    public Profession getProfession() {
        return profession;
    }

    public User setProfession(Profession profession) {
        this.profession = profession;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public User setRoles(List<Role> roles) {
        this.roles = roles;
        return this;
    }

    public List<Demand> getDemands() {
        return demands;
    }

    public User setDemands(List<Demand> demands) {
        this.demands = demands;
        return this;
    }

    public List<Site> getSites() {
        return sites;
    }

    public User setSites(List<Site> sites) {
        this.sites = sites;
        return this;
    }
}
