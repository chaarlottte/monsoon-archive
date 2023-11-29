package wtf.opengui;

import lombok.Getter;

import java.awt.*;

public class CStyle {
    public Color fill_color = new Color(0x00000000, true);
    public float radius;
    public float width,height;

    public CStyle set(Object in, Object setter) {
        in = setter;
        return this;
    }
}
