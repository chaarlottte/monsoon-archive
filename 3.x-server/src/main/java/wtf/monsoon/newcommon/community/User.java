package wtf.monsoon.newcommon.community;

import java.io.Serializable;

public class User implements Serializable {

    public String username;
    public Color color = Color.DEFAULT;

    public User(String username) {
        this.username = username;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getUnformattedFullName() {
        return this.username;
    }

    public enum Color {
        DEFAULT,
        AQUA,
        GOLD,
        RED,
        GREEN,
        PURPLE,
        GRAY,
        POOP
    }
}
