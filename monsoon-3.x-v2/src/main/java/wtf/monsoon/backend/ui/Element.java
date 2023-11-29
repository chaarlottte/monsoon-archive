package wtf.monsoon.backend.ui;

import lombok.Data;
import lombok.experimental.Accessors;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import wtf.monsoon.Wrapper;
import wtf.monsoon.client.util.ui.NVGWrapper;

/**
 * this is for nanovg component system etc
 */

@Accessors(fluent = false, chain = true)
public abstract class Element {

    protected NVGWrapper ui = Wrapper.getMonsoon().nvg;

    protected float x,y,w,h;
    public abstract void render(float mx, float my);
    public abstract void click(float mx, float my, int button);
    public void key(int code, char c) {}
    public boolean hovered(float x, float y, float w, float h) {
        return Mouse.getX() >= x && Mouse.getX() <= x + w && (Display.getHeight() - Mouse.getY()) >= y && (Display.getHeight() - Mouse.getY()) <= y + h;
    }
    public boolean hovered() {
        return hovered(x,y,w,h);
    }

}
