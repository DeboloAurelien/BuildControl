package fr.epsi.entities;

import java.util.List;

public class Project {
    private Long id;
    private String name;
    private Site site;
    private List<Demand> demands;

    public Long getId() {
        return id;
    }

    public Project setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Project setName(String name) {
        this.name = name;
        return this;
    }

    public Site getSite() {
        return site;
    }

    public Project setSite(Site site) {
        this.site = site;
        return this;
    }

    public List<Demand> getDemands() {
        return demands;
    }

    public Project setDemands(List<Demand> demands) {
        this.demands = demands;
        return this;
    }
}
