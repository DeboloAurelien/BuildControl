package fr.epsi.entities;

public class ProjectSubcontractor {
    private String name;
    private String label;

    public String getName() {
        return name;
    }

    public ProjectSubcontractor setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public ProjectSubcontractor setLabel(String label) {
        this.label = label;
        return this;
    }

    public Long getNbSubcontractors() {
        return nbSubcontractors;
    }

    public ProjectSubcontractor setNbSubcontractors(Long nbSubcontractors) {
        this.nbSubcontractors = nbSubcontractors;
        return this;
    }

    private Long nbSubcontractors;
}
