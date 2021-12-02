package org.ga4gh.starterkit.refget.model;

public class RefgetMetadataResponse {

    private RefgetData metadata;

    /* Constructors */

    public RefgetMetadataResponse(RefgetData metadata) {
        this.metadata = metadata;
    }

    /* Setters and Getters */

    public RefgetData getMetadata() {
        return metadata;
    }

    public void setMetadata(RefgetData metadata) {
        this.metadata = metadata;
    }
}
