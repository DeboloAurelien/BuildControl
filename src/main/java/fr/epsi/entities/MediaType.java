package fr.epsi.entities;

import java.util.List;

public class MediaType {
    private Long id;
    private String label;
    private List<Media> medias;

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

    public List<Media> getMedias() {
        return medias;
    }

    public MediaType setMedias(List<Media> medias) {
        this.medias = medias;
        return this;
    }
}
