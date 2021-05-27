package fr.epsi.entities;

import java.util.List;

public class Subcontractor {
    private Long id;
    private String name;
    private String address;
    private String postal;
    private String city;
    private List<Demand> demands;
    private List<Profession> professions;

    public Long getId() {
        return id;
    }

    public Subcontractor setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Subcontractor setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Subcontractor setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPostal() {
        return postal;
    }

    public Subcontractor setPostal(String postal) {
        this.postal = postal;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Subcontractor setCity(String city) {
        this.city = city;
        return this;
    }

    public List<Demand> getDemands() {
        return demands;
    }

    public Subcontractor setDemands(List<Demand> demands) {
        this.demands = demands;
        return this;
    }

    public List<Profession> getProfessions() {
        return professions;
    }

    public Subcontractor setProfessions(List<Profession> professions) {
        this.professions = professions;
        return this;
    }
}
