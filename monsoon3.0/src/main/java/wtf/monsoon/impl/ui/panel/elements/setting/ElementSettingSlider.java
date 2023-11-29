package wtf.monsoon.impl.ui.panel.elements.setting;

import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;

public class ElementSettingSlider extends ElementSetting<Number> {

    private boolean dragging = false;

    public ElementSettingSlider(Setting<Number> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        super.draw(mouseX, mouseY, mouseDelta);

        if (!Mouse.isButtonDown(0)) {
            dragging = false;
        }

        float offset = 4;

        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(getX() + 1, getY(), getWidth() - 2, getHeight(), new Color(0x252525));
        Wrapper.getFontUtil().productSansSmall.drawString(getSetting().getName(), getX() + 5, getY() + 2, new Color(180, 180, 180), false);

        float sliderWidth = getWidth() - offset * 2;
        float diff = Math.min(sliderWidth, Math.max(0, mouseX - (getX() + offset)));

        float min = getSetting().getMinimum().floatValue();
        float max = getSetting().getMaximum().floatValue();
        float step = getSetting().getIncrementation().floatValue();
        float current = getSetting().getValue().floatValue();

        float renderWidth = (sliderWidth) * (current - min) / (max - min);

        Wrapper.getFontUtil().productSansSmaller.drawString("" + current, getX() + getWidth() - 4 - Wrapper.getFontUtil().productSansSmaller.getStringWidth("" + current), getY() + 2, new Color(180, 180, 180), false);

        RoundedUtils.round(getX() + offset, getY() + getHeight() - 3, sliderWidth, 2, 1f, new Color(0x1C1C1C));
        RoundedUtils.round(getX() + offset, getY() + getHeight() - 3, renderWidth, 2, 1f, ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));

        if (dragging) {
            float value = (float) MathUtils.round(((diff / sliderWidth) * (max - min) + min), 2);
            value = Math.round(Math.max(min, Math.min(max, value)) * (1 / step)) / (1 / step);

            float finalValue = diff == 0 ? min : value;

            if (getSetting().getValue() instanceof Double) {
                getSetting().setValue((double) finalValue);
            } else if (getSetting().getValue() instanceof Float) {
                getSetting().setValue(finalValue);
            } else if (getSetting().getValue() instanceof Integer) {
                getSetting().setValue((int) finalValue);
            } else if (getSetting().getValue() instanceof Long) {
                getSetting().setValue((long) finalValue);
            } else if (getSetting().getValue() instanceof Short) {
                getSetting().setValue((short) finalValue);
            } else if (getSetting().getValue() instanceof Byte) {
                getSetting().setValue((byte) finalValue);
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (sliderHovered(mouseX, mouseY) && click.equals(Click.LEFT)) {
            dragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }

    private boolean sliderHovered(float mouseX, float mouseY) {
        return mouseX >= getX() + 4 && mouseY >= getY() && mouseX <= getX() + getWidth() - 4 && mouseY <= getY() + getHeight();
    }

}
