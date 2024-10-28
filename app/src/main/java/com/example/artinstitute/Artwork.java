package com.example.artinstitute;

import java.io.Serializable;

public class Artwork implements Serializable {
    public String title;
    public String dateDisplay;
    public String artist;
    public String artistDetails;
    public String mediumDisplay;
    public String artworkTypeTitle;
    public String imageId;
    public String dimensions;
    public String departmentTitle;
    public String creditLine;
    public String placeOfOrigin;
    public String galleryTitle;
    public String galleryId;
    public String apiLink;
    public String id;

    public Artwork()
    {}
    public Artwork(String id, String title, String dateDisplay, String artistDisplay, String mediumDisplay,
                   String artworkTypeTitle, String imageId,
                   String dimensions, String departmentTitle, String creditLine,
                   String placeOfOrigin, String galleryTitle, String galleryId, String apiLink) {
        this.id = id;
        this.title = title;
        this.dateDisplay = dateDisplay;

        if (artistDisplay.contains("\n")) {
            String[] parts = artistDisplay.split("\n");
            this.artist = parts[0];
            this.artistDetails = parts[1];
        } else {
            this.artist = artistDisplay;
            this.artistDetails = "";
        }

        this.mediumDisplay = mediumDisplay;
        this.artworkTypeTitle = artworkTypeTitle;
        this.imageId = imageId;
        this.dimensions = dimensions;
        this.departmentTitle = departmentTitle;
        this.creditLine = creditLine;
        this.placeOfOrigin = placeOfOrigin;
        this.galleryTitle = galleryTitle != null ? galleryTitle : "Not on Display";
        this.galleryId = galleryId;
        this.apiLink = apiLink;
    }
}
