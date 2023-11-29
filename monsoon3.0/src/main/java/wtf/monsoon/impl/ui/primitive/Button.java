package wtf.monsoon.impl.ui.primitive;

import lombok.NonNull;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;

import java.awt.*;

public class Button extends Drawable {

    Animation animation = new Animation(() -> 200F, false, () -> Easing.SINE_IN_OUT);
    FontRenderer fontRenderer;
    String text;
    float padding;
    ButtonHandler handle;

    public Button(float x, float y, FontRenderer fontRenderer, String text, float padding, ButtonHandler handle) {
        super(x, y, 0, 0);
        this.fontRenderer = fontRenderer;
        this.text = text;
        this.padding = padding;
        this.handle = handle;
    }

    @Override
    public @NonNull float getWidth() {
        return fontRenderer.getStringWidth(text) + padding;
    }

    @Override
    public @NonNull float getHeight() {
        return fontRenderer.getHeight() + padding;
    }

    public void draw(float mouseX, float mouseY, int mouseDelta) {
        animation.setState((mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight()));

        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), new Color(0x131313));
        fontRenderer.drawCenteredString(text, getX() + this.getWidth() / 2f - 1, getY() + this.getHeight() / 2f - fontRenderer.getHeight() / 2f - 1, Color.WHITE, false);

        RenderUtil.rect(getX() + getWidth() / 2f - ((float) animation.getAnimationFactor()) * (getWidth() / 2f - 2), getY() + getHeight() - 2, ((float) animation.getAnimationFactor()) * (getWidth() - 4), 1F, ColorUtil.getClientAccentTheme()[0]);
    }

    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if ((mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight()))
            handle.onClick(click);
        return false;
    }

    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }
}