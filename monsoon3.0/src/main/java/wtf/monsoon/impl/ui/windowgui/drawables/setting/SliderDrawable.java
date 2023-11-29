package wtf.monsoon.impl.ui.windowgui.drawables.setting;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
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
public class SliderDrawable extends SettingDrawable<Number> {

    private boolean dragging = false;

    private float renderWidth = 0f;

    private final Animation sliderAnimation = new Animation(() -> 600f, false, () -> Easing.LINEAR);

    public SliderDrawable(Drawable parent, Setting<Number> setting, float x, float y, float width, float height) {
        super(parent, setting, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 5, ColorUtil.interpolate(WindowGUI.INTERACTABLE.darker(), WindowGUI.HOVER, getHover().getAnimationFactor() / 2f + .3f));
        Wrapper.getFont().drawString(getSetting().getName(), getX() + 5, getY() + 2, Color.WHITE, false);

        Wrapper.getFont().drawString(getSetting().getDescription(), (float) ((getX() + 5) - ((Wrapper.getFont().getStringWidth(getSetting().getDescription()) - (getWidth() - 10)) * getDescriptionHover().getAnimationFactor())), getY() + 15, Color.GRAY, false);

        // Set values
        double diff = Math.min(getWidth(), Math.max(0, mouseX - (getX() + 8)));

        float localWidth = 0f;

        if (getSetting().getValue() instanceof Double) {
            double min = getSetting().getMinimum().doubleValue();
            double max = getSetting().getMaximum().doubleValue();

            localWidth = (float) ((getWidth() - 16) * (getSetting().getValue().doubleValue() - min) / (max - min));

            if (!Mouse.isButtonDown(0)) {
                dragging = false;
            }

            if (dragging) {
                if (diff == 0) {
                    getSetting().setValue(min);
                } else {
                    double newValue = MathUtils.round(((diff / (getWidth() - 16)) * (max - min) + min), 2);

                    double precision = 1 / getSetting().getIncrementation().doubleValue();
                    newValue = Math.round(Math.max(min, Math.min(max, newValue)) * precision) / precision;

                    getSetting().setValue(newValue);
                }
            }
        } else if (getSetting().getValue() instanceof Float) {
            float min = getSetting().getMinimum().floatValue();
            float max = getSetting().getMaximum().floatValue();

            localWidth = (getWidth() - 16) * (getSetting().getValue().floatValue() - min) / (max - min);

            if (!Mouse.isButtonDown(0)) {
                dragging = false;
            }

            if (dragging) {
                if (diff == 0) {
                    getSetting().setValue(min);
                } else {
                    float newValue = (float) MathUtils.round(((diff / (getWidth() - 16)) * (max - min) + min), 2);

                    float precision = 1 / getSetting().getIncrementation().floatValue();
                    newValue = Math.round(Math.max(min, Math.min(max, newValue)) * precision) / precision;

                    getSetting().setValue(newValue);
                }
            }
        }

        sliderAnimation.setState(renderWidth != localWidth);

        if (localWidth > renderWidth) {
            float widthDifference = renderWidth - localWidth;
            renderWidth -= widthDifference * (float) sliderAnimation.getAnimationFactor();
        }

        if (localWidth < renderWidth) {
            float widthDifference = localWidth - renderWidth;
            renderWidth += widthDifference * (float) sliderAnimation.getAnimationFactor();
        }

        RoundedUtils.round(getX() + 8, getY() + 30, getWidth() - 16, 6, 2, WindowGUI.INTERACTABLE.darker());

        RoundedUtils.gradient(getX() + 8, getY() + 30, renderWidth, 6, 2, 1,
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255))
        );

        RoundedUtils.round(getX() + 8 + renderWidth - 6, getY() + 27, 12, 12, 5, ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)));

        Wrapper.getFont().drawString(getSetting().getValue().toString(), getX() + getWidth() - Wrapper.getFont().getStringWidth(getSetting().getValue().toString()) - 5, getY() + 4, Color.GRAY.brighter(), false);

        super.draw(mouseX, mouseY, mouseDelta);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (getHover().getState() && click.equals(Click.LEFT)) {
            dragging = true;
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        dragging = false;

        super.mouseReleased(mouseX, mouseY, click);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

}
