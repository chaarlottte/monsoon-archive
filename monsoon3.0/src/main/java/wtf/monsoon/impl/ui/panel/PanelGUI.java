package wtf.monsoon.impl.ui.panel;

import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.opengui.TestOpenGuiScreen;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.panel.elements.config.ConfigPanel;
import wtf.monsoon.impl.ui.panel.elements.Panel;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.primitive.Button;

import java.io.IOException;
import java.util.ArrayList;

public class PanelGUI extends ScalableScreen {

    private final ArrayList<Drawable> drawables = new ArrayList<>();

    private Drawable draggingPanel;
    private float dragX, dragY;
    private boolean drag;

    public PanelGUI() {
        int x = 0;

        for (Category category : Category.values()) {
            drawables.add(new Panel(category, 20 + x, 20, 90, 17));
            x += 100;
        }

        drawables.add(new Button(0,0, Wrapper.getFontUtil().productSansSmallBold, "OpenGui test", 10, button -> {
            if (button.equals(Click.LEFT)) {
                mc.displayGuiScreen(new TestOpenGuiScreen());
            }
        }));

        ConfigPanel configPanel = new ConfigPanel(20, 20, 90, 180);
        drawables.add(configPanel);

        drawables.add(new Button(4, getScaledHeight() - 24, Wrapper.getFontUtil().productSansSmallBold, "Configs Window", 8, button -> {
            if (button.equals(Click.LEFT)) {
                ((ConfigPanel) drawables.get(drawables.size() - 2)).getPanelToggleAnim().setState(!((ConfigPanel) drawables.get(drawables.size() - 2)).getPanelToggleAnim().getState());
            }
        }));
    }

    @Override
    public void init() {
        ConfigPanel configPanel = new ConfigPanel(5, getScaledHeight() - 200, 90, 180);

        drawables.set(drawables.size() - 2, configPanel);
        drawables.get(drawables.size() - 1).setY(getScaledHeight() - 20);
    }

    @Override
    public void render(float mouseX, float mouseY) {
        if (draggingPanel != null) {
            drag(draggingPanel, mouseX, mouseY);
        }

        int mouseDelta = Mouse.getDWheel();

        drawDefaultBackground();

        Wrapper.getMonsoon().getCharacterManager().draw((int) mouseX, (int) mouseY, mc.thePlayer.ticksExisted, this);

        drawables.forEach(drawable -> drawable.draw(mouseX, mouseY, mouseDelta));
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        Wrapper.getMonsoon().getCharacterManager().onClick((int) mouseX, (int) mouseY, mouseButton, this);
        for (Drawable drawable : drawables) {
            if (drawable instanceof ConfigPanel && mouseButton == 0 && ((ConfigPanel) drawable).dragHovered(mouseX, mouseY)) {
                this.draggingPanel = drawable;
            } else if (mouseButton == 0 && drawable.hovered(mouseX, mouseY)) {
                this.draggingPanel = drawable;
            }

            if (drawable instanceof ConfigPanel || drawable instanceof Button) {
                drawable.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
            } else {
                if (drawables.get(drawables.size() - 2).hovered(mouseX, mouseY)) {
                    continue;
                }

                drawable.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        drag = false;
        draggingPanel = null;
        drawables.forEach(drawable -> drawable.mouseReleased(mouseX, mouseY, Click.getClick(state)));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        drawables.forEach(drawable -> drawable.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drag(Drawable drawable, float mouseX, float mouseY) {
        if (!this.drag && Mouse.isButtonDown(0)) {
            drag = false;
        }

        if (this.drag) {
            drawable.setX(mouseX + this.dragX);
            drawable.setY(mouseY + this.dragY);

            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
        }

        if (drawable.hovered(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (!this.drag) {
                this.dragX = (drawable.getX() - mouseX);
                this.dragY = (drawable.getY() - mouseY);
                this.drag = true;
            }
        }
    }
}
