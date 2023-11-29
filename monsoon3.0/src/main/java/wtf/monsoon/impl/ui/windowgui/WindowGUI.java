package wtf.monsoon.impl.ui.windowgui;

import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.windowgui.window.Window;

import java.awt.*;
import java.io.IOException;

/**
 * @author Surge
 * @since 28/08/2022
 */
public class WindowGUI extends ScalableScreen {

    public static Color BACKGROUND = new Color(20, 20, 24);
    public static Color LAYER_ONE = new Color(30, 30, 34);
    public static Color INTERACTABLE = new Color(50, 50, 54);
    public static Color HOVER = new Color(60, 60, 64);

    private Window window;

    public WindowGUI() {
        window = new Window(50, 50, 500, 400);
    }

    @Override
    public void init() {
        if (window == null) window = new Window(50, 50, 500, 400);
    }

    @Override
    public void render(float mouseX, float mouseY) {
        int mouseDelta = Mouse.getDWheel();

        Wrapper.getMonsoon().getCharacterManager().draw((int) mouseX, (int) mouseY, mc.thePlayer.ticksExisted, this);

        if (window != null) window.render(mouseX, mouseY, mouseDelta);
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        Wrapper.getMonsoon().getCharacterManager().onClick((int) mouseX, (int) mouseY, mouseButton, this);
        if (window != null) window.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (window != null) window.mouseReleased(mouseX, mouseY, Click.getClick(state));

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (window != null) window.keyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        Wrapper.getMonsoon().getConfigSystem().save("current");
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
