package wtf.monsoon.newcommon.store;


import java.awt.*;

public class File implements java.io.Serializable {
    public String name;
    public String description;
    public int vote = 0;
    public Image image;

    public File(String name, String description, Image image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public File(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
