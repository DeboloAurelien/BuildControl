package fr.epsi.entities;

import java.util.List;

public class Profession {
    private Long id;
    private String label;
    private List<Subcontractor> subcontractors;
    private List<User> users;

    public Long getId() {
        return id;
    }

    public Profession setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Profession setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<Subcontractor> getSubcontractors() {
        return subcontractors;
    }

    public Profession setSubcontractors(List<Subcontractor> subcontractors) {
        this.subcontractors = subcontractors;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Profession setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
