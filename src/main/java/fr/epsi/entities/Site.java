package fr.epsi.entities;

import java.util.List;

public class Site {
    private Long id;
    private String name;
    private List<User> users;
    private List<Site> sites;

    public Long getId() {
        return id;
    }

    public Site setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Site setName(String name) {
        this.name = name;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Site setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public List<Site> getSites() {
        return sites;
    }

    public Site setSites(List<Site> sites) {
        this.sites = sites;
        return this;
    }
}
