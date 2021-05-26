package fr.epsi.entities;

import java.util.List;

public class Profession {
    private Long id;
    private String label;
    private List<Subcontractor> subcontractors;

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
}
