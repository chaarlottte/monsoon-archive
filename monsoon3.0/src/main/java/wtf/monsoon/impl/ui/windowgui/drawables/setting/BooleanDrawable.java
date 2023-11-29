package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.SettingDrawable;

import java.awt.*;

/**
 * @author Surge
 * @since 28/08/2022
 */
public class BooleanDrawable extends SettingDrawable<Boolean> {

    private final Animation enabled = new Animation(() -> 200f, false, () -> Easing.LINEAR);

    public BooleanDrawable(Drawable parent, Setting<Boolean> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        enabled.setState(this.getSetting().getValue());

        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 5, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, getHover().getAnimationFactor() / 2f + 0.3f));

        RoundedUtils.gradient(getX(), getY(), getWidth(), getHeight(), 5, (float) enabled.getAnimationFactor(),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 45, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 135, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255))
        );

        RoundedUtils.round(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 5, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, getHover().getAnimationFactor() / 2f + 0.3f));

        Wrapper.getFont().drawString(getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE, false);
        Wrapper.getFont().drawString(getSetting().getDescription(), (float) ((getX() + 5) - ((Wrapper.getFont().getStringWidth(getSetting().getDescription()) - (getWidth() - 10)) * getDescriptionHover().getAnimationFactor())), getY() + 15, Color.GRAY, false);

        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (getHover().getState()) {
            if (click.equals(Click.LEFT)) {
                getSetting().setValue(!getSetting().getValue());
            }
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        super.mouseReleased(mouseX, mouseY, click);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

}
