package fr.epsi.entities;

import java.util.List;

public class Privilege {
    private Long id;
    private String label;
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public Privilege setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Privilege setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Privilege setRoles(List<Role> roles) {
        this.roles = roles;
        return this;
    }
}
