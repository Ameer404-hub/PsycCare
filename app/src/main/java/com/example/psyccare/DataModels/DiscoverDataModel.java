package com.example.psyccare.DataModels;

public class DiscoverDataModel {
    String title, imageUri, description;

    public DiscoverDataModel() {

    }

    public DiscoverDataModel(String title, String imageUri, String description) {
        this.title = title;
        this.imageUri = imageUri;
        this.description = description;
    }

    public DiscoverDataModel(String title, String imageUri) {
        this.title = title;
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
