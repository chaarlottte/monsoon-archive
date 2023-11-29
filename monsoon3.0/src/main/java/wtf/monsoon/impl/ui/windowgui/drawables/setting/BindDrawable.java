package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
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
public class BindDrawable extends SettingDrawable<Bind> {

    private final Animation listening = new Animation(() -> 200f, false, () -> Easing.LINEAR);

    public BindDrawable(Drawable parent, Setting<Bind> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.gradient(getX(), getY(), getWidth(), getHeight(), 6, (float) listening.getAnimationFactor(),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 45, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 135, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255))
        );

        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 6, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, getHover().getAnimationFactor() / 2f + .3f));

        Wrapper.getFont().drawString(getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE, false);
        Wrapper.getFont().drawString(listening.getState() ? "..." : getSetting().getValue().getButtonName(), getX() + getWidth() - Wrapper.getFont().getStringWidth(listening.getState() ? "..." : getSetting().getValue().getButtonName()) - 5, getY() + 2, Color.GRAY.brighter(), false);

        Wrapper.getFont().drawString(getSetting().getDescription(), (float) ((getX() + 5) - ((Wrapper.getFont().getStringWidth(getSetting().getDescription()) - (getWidth() - 10)) * getDescriptionHover().getAnimationFactor())), getY() + 15, Color.GRAY, false);

        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (getHover().getState()) {
            if (click.equals(Click.LEFT)) {
                listening.setState(!listening.getState());
                return true;
            }
        }

        if (listening.getState()) {
            listening.setState(false);

            getSetting().setValue(new Bind(click.getButton(), Bind.Device.MOUSE));

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        super.mouseReleased(mouseX, mouseY, click);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (listening.getState()) {
            listening.setState(false);

            if (keyCode < 1) {
                return;
            }

            if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK) {
                getSetting().setValue(new Bind(0, Bind.Device.KEYBOARD));
                return;
            }

            getSetting().setValue(new Bind(keyCode, Bind.Device.KEYBOARD));
        }

        super.keyTyped(typedChar, keyCode);
    }

}
