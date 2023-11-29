package wtf.monsoon.impl.ui.panel.elements.config;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigPanel extends Drawable {

    @Getter
    private final Animation panelToggleAnim = new Animation(() -> 200F, false, () -> Easing.SINE_IN_OUT);

    @Getter
    private final CopyOnWriteArrayList<ConfigPane> panes = new CopyOnWriteArrayList<>();

    @Getter
    private final Animation sizeAnimation = new Animation(200f, false, () -> Easing.CUBIC_IN_OUT);

    @Getter
    private final NameField field = new NameField(getX(), getY(), getWidth() / 2 - 5, 8);

    @Getter
    private final Button save = new Button("Save", () -> {
        if (!field.text.isEmpty()) {
            Wrapper.getMonsoon().getConfigSystem().save(field.getText());
            field.text = "";
            for(ConfigPane pane : panes) {
                if(pane.getConfig().getPath().contains("monsoon/configs/" + field.getText() + ".json")) return;
            }
            panes.add(new ConfigPane(this, new File("monsoon/configs/" + field.getText() + ".json"), getX(), getY(), 70, 30));
        }
    }, getX(), getY(), getWidth() / 2 - 5, 8, false);

    private float scroll = 0;

    public ConfigPanel(float x, float y, float width, float height) {
        super(x, y, width, height);

        for (File config : Wrapper.getMonsoon().getConfigSystem().getDirectory("configs").listFiles()) {
            if (config.isFile() && config.getName().endsWith(".json")) {
                panes.add(new ConfigPane(this, config, getX(), getY(), 70, 30));
            }
        }
    }

    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RenderUtil.scaleXY(getX() + getWidth() / 2f, getY() + getHeight() / 2f, panelToggleAnim, () -> {
            GlStateManager.pushMatrix();
            render(mouseX, mouseY, mouseDelta);
            GlStateManager.popMatrix();
        });
    }

    public void render(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.shadow(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 6F, 15, Color.BLACK);
        RoundedUtils.shadow(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 6, 10, Color.BLACK);
        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 6, new Color(0x131313));
        RoundedUtils.round(getX() + 2, getY() + 15, getWidth() - 4, getHeight() - 17, 4, new Color(0x1F1F1F));

        Wrapper.getFont().drawCenteredString("Configs", getX() + (getWidth() / 2f), getY() + 2, Color.WHITE, false);

        scroll += mouseDelta * 0.1;
        scroll = MathHelper.clamp_float(scroll, -Math.max(0f, getPaneHeight() - (getHeight() - 20)), 0f);

        RenderUtil.pushScissor(getX(), getY() + 16, getWidth(), getHeight() - 18); {
            float y = getY() + 23 + scroll;

            for (ConfigPane pane : panes) {
                pane.setX(getX() + (getWidth() / 2) - (pane.getWidth() / 2));
                pane.setY(y);

                pane.draw(mouseX, mouseY, mouseDelta);

                y += (pane.getHeight() + 2) * pane.getDeleteScaleDown().getAnimationFactor();
            }

            RenderUtil.popScissor();
        }

        field.setX(getX() + ((getWidth() / 2) - field.getWidth()));
        field.setY(getY() + getHeight() - 13);
        save.setX(getX() + (getWidth() / 2));
        save.setY(getY() + getHeight() - 13);

        field.draw(mouseX, mouseY, mouseDelta);
        save.draw(mouseX, mouseY, mouseDelta);
    }

    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (panelToggleAnim.getState()) {
            panes.forEach(pane -> pane.mouseClicked(mouseX, mouseY, click));
        }

        save.mouseClicked(mouseX, mouseY, click);
        field.mouseClicked(mouseX, mouseY, click);

        return false;
    }

    public void mouseReleased(float mouseX, float mouseY, Click click) {}

    public void keyTyped(char typedChar, int keyCode) {
        field.keyTyped(typedChar, keyCode);
    }

    private float getPaneHeight() {
        float paneHeight = 11;

        for (ConfigPane pane : panes) {
            paneHeight += (pane.getHeight() + 2) * pane.getDeleteScaleDown().getAnimationFactor();
        }

        return paneHeight;
    }

    @Override
    public float getHeight() {
        return MathHelper.clamp_float(20 + getPaneHeight(), 0, 180) + 10;
    }

    public boolean dragHovered(float mouseX, float mouseY) {
        return MathUtils.within(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) && panelToggleAnim.getState();
    }

    public static class Button extends Drawable {

        private final String text;
        private final Animation hover = new Animation(() -> 200F, false, () -> Easing.LINEAR);
        private final Runnable clicked;
        private final boolean disabled;

        public Button(String text, Runnable clicked, float x, float y, float width, float height, boolean disabled) {
            super(x, y, width, height);
            this.clicked = clicked;
            this.text = text;
            this.disabled = disabled;
        }

        @Override
        public void draw(float mouseX, float mouseY, int mouseDelta) {
            hover.setState(hovered(mouseX, mouseY));

            RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 2, disabled ? new Color(0x212121) : ColorUtil.interpolate(new Color(0x343434), new Color(0x444444), hover.getAnimationFactor()));
            Wrapper.getFontUtil().productSansSmaller.drawCenteredString(text, getX() + getWidth() / 2f, getY(), Color.WHITE, false);
        }

        @Override
        public boolean mouseClicked(float mouseX, float mouseY, Click click) {
            if (hovered(mouseX, mouseY)) {
                clicked.run();
            }

            return false;
        }

        @Override
        public void mouseReleased(float mouseX, float mouseY, Click click) {}

        @Override
        public void keyTyped(char typedChar, int keyCode) {}
    }

    public static class NameField extends Drawable {

        @Getter
        @Setter
        private String text = "";

        private final Animation hover = new Animation(() -> 200F, false, () -> Easing.LINEAR);

        private boolean listening;

        public NameField(float x, float y, float width, float height) {
            super(x, y, width, height);
        }

        @Override
        public void draw(float mouseX, float mouseY, int mouseDelta) {
            hover.setState(hovered(mouseX, mouseY));

            Keyboard.enableRepeatEvents(true);

            RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 2, ColorUtil.interpolate(new Color(0x343434), new Color(0x444444), hover.getAnimationFactor()));

            if (!text.isEmpty() || listening) {
                Wrapper.getFontUtil().productSansSmaller.drawString(text + (listening ? "_" : ""), getX() + 2, getY(), Color.WHITE, false);
            } else {
                Wrapper.getFontUtil().productSansSmaller.drawString("Name", getX() + 2, getY(), Color.GRAY, false);
            }
        }

        @Override
        public boolean mouseClicked(float mouseX, float mouseY, Click click) {
            if (hovered(mouseX, mouseY)) {
                listening = !listening;
            } else {
                listening = false;
            }

            return false;
        }

        @Override
        public void mouseReleased(float mouseX, float mouseY, Click click) {}

        @Override
        public void keyTyped(char typedChar, int keyCode) {
            if (listening) {
                if (keyCode == Keyboard.KEY_RETURN) {
                    listening = false;
                } else if (listening && Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
                    if (!text.isEmpty()) {
                        text = text.substring(0, text.length() - 1);
                    }
                } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    text = text + typedChar;
                }

                if (GuiScreen.isCtrlKeyDown()) {
                    if (keyCode == Keyboard.KEY_V) {
                        try {
                            text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                        } catch (UnsupportedFlavorException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}