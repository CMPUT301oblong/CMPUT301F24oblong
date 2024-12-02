package com.example.oblong.admin;

public class AdminImageBrowserObject {
    private String type;
    private String image;
    private String id;
    private String name;

    public AdminImageBrowserObject(String type, String image, String id, String name) {
        this.type = type;
        this.image = image;
        this.id = id;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
