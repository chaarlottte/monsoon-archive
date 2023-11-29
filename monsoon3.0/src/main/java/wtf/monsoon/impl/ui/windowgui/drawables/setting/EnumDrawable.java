package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
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
public class EnumDrawable extends SettingDrawable<Enum<?>> {

    public EnumDrawable(Drawable parent, Setting<Enum<?>> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 5, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, getHover().getAnimationFactor() / 2f + .3f));
        Wrapper.getFont().drawString(getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE, false);
        Wrapper.getFont().drawString(StringUtil.formatEnum(getSetting().getValue()), getX() + getWidth() - Wrapper.getFont().getStringWidth(StringUtil.formatEnum(getSetting().getValue())) - 5, getY() + 2, Color.GRAY.brighter(), false);

        Wrapper.getFont().drawString(getSetting().getDescription(), (float) ((getX() + 5) - ((Wrapper.getFont().getStringWidth(getSetting().getDescription()) - (getWidth() - 10)) * getDescriptionHover().getAnimationFactor())), getY() + 15, Color.GRAY, false);

        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (getHover().getState()) {
            if (click.equals(Click.LEFT)) {
                getSetting().setValue(getSetting().getMode(false));
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
