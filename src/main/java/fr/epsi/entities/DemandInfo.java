package fr.epsi.entities;

import java.sql.Timestamp;

public class DemandInfo {
    private Timestamp demandDate;
    private String projectName;
    private String siteName;
    private String username;
    private String userProfessionLabel;
    private String subcontractorName;
    private String subcontractorProfessionLabel;
    private String path;

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

    public String getUserProfessionLabel() {
        return userProfessionLabel;
    }

    public DemandInfo setUserProfessionLabel(String userProfessionLabel) {
        this.userProfessionLabel = userProfessionLabel;
        return this;
    }

    public String getSubcontractorName() {
        return subcontractorName;
    }

    public DemandInfo setSubcontractorName(String subcontractorName) {
        this.subcontractorName = subcontractorName;
        return this;
    }

    public String getSubcontractorProfessionLabel() {
        return subcontractorProfessionLabel;
    }

    public DemandInfo setSubcontractorProfessionLabel(String subcontractorProfessionLabel) {
        this.subcontractorProfessionLabel = subcontractorProfessionLabel;
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
