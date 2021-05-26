package fr.epsi.entities;

import java.util.List;

public class Role {
    private Long id;
    private String label;
    private List<Privilege> privileges;
    private List<User> users;

    public Long getId() {
        return id;
    }

    public Role setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Role setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public Role setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Role setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
