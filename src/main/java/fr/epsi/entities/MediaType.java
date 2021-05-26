package fr.epsi.entities;

public class MediaType {
    private Long id;
    private String label;

    public Long getId() {
        return id;
    }

    public MediaType setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public MediaType setLabel(String label) {
        this.label = label;
        return this;
    }
}
