package fr.epsi.entities;

import java.sql.Date;
import java.util.List;

public class Project {
    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public Project setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Project setEndDate(Date endDate) {
        this.endDate = endDate;
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
