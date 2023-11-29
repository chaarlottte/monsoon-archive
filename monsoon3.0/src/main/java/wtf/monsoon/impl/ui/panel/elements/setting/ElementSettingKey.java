package wtf.monsoon.impl.ui.panel.elements.setting;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;

public class ElementSettingKey extends ElementSetting<Bind> {

    private final Animation listening = new Animation(() -> 200f, false, () -> Easing.LINEAR);

    public ElementSettingKey(Setting<Bind> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        String bindName = listening.getState() ? "Listening" : getSetting().getValue().getButtonName();

        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(getX() + 1, getY(), getWidth() - 2, getHeight(), new Color(0x252525));

        Wrapper.getFontUtil().productSansSmall.drawString(getSetting().getName(), getX() + 4f, getY() + getHeight() / 2f - Wrapper.getFont().getHeight() / 2f, new Color(0xff8f8f8f), false);
        Wrapper.getFontUtil().productSansSmall.drawString("[" + bindName + "]", getX() + getWidth() - Wrapper.getFontUtil().productSansSmall.getStringWidth(bindName) - 12, getY() + getHeight() / 2f - Wrapper.getFont().getHeight() / 2f, ColorUtil.interpolate(new Color(0xff8f8f8f), new Color(0x525252), listening.getAnimationFactor()), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hovered(mouseX, mouseY) && click.equals(Click.LEFT)) {
            listening.setState(!listening.getState());

            return false;
        }

        if (listening.getState()) {
            listening.setState(false);
            getSetting().setValue(new Bind(click.getButton(), Bind.Device.MOUSE));

            return false;
        }

        return false;
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
