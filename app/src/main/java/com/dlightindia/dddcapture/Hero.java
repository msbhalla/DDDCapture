package com.dlightindia.dddcapture;

public class Hero {
    String name, imageUrl;

    public Hero(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}