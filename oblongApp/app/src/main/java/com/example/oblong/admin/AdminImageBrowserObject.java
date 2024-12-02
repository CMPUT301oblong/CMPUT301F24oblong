package com.example.oblong.admin;

/**
 * Represents an object in the admin image browser.
 * This object holds details about the image, including its type, image data,
 * unique identifier, and associated name.
 */
public class AdminImageBrowserObject {
    private String type;
    private String image;
    private String id;
    private String name;

    /**
     * Constructs an AdminImageBrowserObject.
     *
     * @param type  The type of the object.
     * @param image The Base64-encoded image string.
     * @param id    The unique identifier for the object.
     * @param name  The name associated with the object.
     */
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
