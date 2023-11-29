package wtf.monsoon.impl.ui.panel.elements.setting;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;

public class ElementSettingBoolean extends ElementSetting<Boolean> {

    private final Animation toggle = new Animation(() -> 150f, getSetting().getValue(), () -> Easing.LINEAR);

    public ElementSettingBoolean(Setting<Boolean> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        super.draw(mouseX, mouseY, mouseDelta);

        toggle.setState(getSetting().getValue());

        Color toggleColor = ColorUtil.interpolate(new Color(0x1E1E1E), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), toggle.getAnimationFactor());

        RoundedUtils.round(getX() + getWidth() - 14, getY() + 2, 10, 10, 2, new Color(0x1E1E1E));

        if (toggle.getAnimationFactor() > 0) {
            Wrapper.getFontUtil().entypo14.drawCenteredString(FontUtil.UNICODES_UI.YES, getX() + getWidth() - 9, getY() + getHeight() / 2f - Wrapper.getFontUtil().entypo14.getHeight() / 2f - 1.5f, ColorUtil.integrateAlpha(toggleColor, (float) (toggle.getAnimationFactor() * 255)), false);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hovered(mouseX, mouseY) && click.equals(Click.LEFT)) {
            getSetting().setValue(!getSetting().getValue());
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }
}
