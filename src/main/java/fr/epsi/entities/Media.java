package fr.epsi.entities;

public class Media {
    private Long id;
    private String path;
    private Demand demand;
    private MediaType mediaType;

    public Long getId() {
        return id;
    }

    public Media setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Media setPath(String path) {
        this.path = path;
        return this;
    }

    public Demand getDemand() {
        return demand;
    }

    public Media setDemand(Demand demand) {
        this.demand = demand;
        return this;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Media setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
}
