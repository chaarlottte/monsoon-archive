package wtf.monsoon.api.ui;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.NVGR;

/**
 * this is for nanovg component system etc
 */

@Accessors(fluent = false, chain = true)
@Data
public abstract class Comp {
    protected NVGR ui = Wrapper.getNVG();
    @NonNull protected float x,y,w,h;
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
