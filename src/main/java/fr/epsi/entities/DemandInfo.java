package fr.epsi.entities;

import java.sql.Timestamp;

public class DemandInfo {
    private String id;
    private Timestamp demandDate;
    private String projectName;
    private String siteName;
    private String username;
    private String userProfession;
    private String subcontractorName;
    private String subcontractorProfession;
    private String path;

    public String getId() {
        return id;
    }

    public DemandInfo setId(String id) {
        this.id = id;
        return this;
    }

    public Timestamp getDemandDate() {
        return demandDate;
    }

    public DemandInfo setDemandDate(Timestamp demandDate) {
        this.demandDate = demandDate;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public DemandInfo setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public DemandInfo setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DemandInfo setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getUserProfession() {
        return userProfession;
    }

    public DemandInfo setUserProfession(String userProfession) {
        this.userProfession = userProfession;
        return this;
    }

    public String getSubcontractorName() {
        return subcontractorName;
    }

    public DemandInfo setSubcontractorName(String subcontractorName) {
        this.subcontractorName = subcontractorName;
        return this;
    }

    public String getSubcontractorProfession() {
        return subcontractorProfession;
    }

    public DemandInfo setSubcontractorProfession(String subcontractorProfession) {
        this.subcontractorProfession = subcontractorProfession;
        return this;
    }

    public String getPath() {
        return path;
    }

    public DemandInfo setPath(String path) {
        this.path = path;
        return this;
    }
}
