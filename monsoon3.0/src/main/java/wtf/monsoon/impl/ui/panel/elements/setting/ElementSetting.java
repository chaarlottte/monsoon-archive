package wtf.monsoon.impl.ui.panel.elements.setting;

import lombok.Getter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ElementSetting<T> extends Drawable {

    @Getter
    private final Setting<T> setting;

    private final Animation expandAnimation = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT);
    private final List<ElementSetting<?>> subsettings = new ArrayList<>();

    public ElementSetting(Setting<T> setting, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.setting = setting;

        for (Setting<?> subsetting : setting.getChildren()) {
            if (subsetting.getValue() instanceof Enum) {
                subsettings.add(new ElementSettingEnum((Setting<Enum<?>>) subsetting, getX(), getY(), getWidth() - 2, getHeight()));
            } else if (subsetting.getValue() instanceof Boolean) {
                subsettings.add(new ElementSettingBoolean((Setting<Boolean>) subsetting, getX(), getY(), getWidth() - 2, getHeight()));
            } else if (subsetting.getValue() instanceof Number) {
                subsettings.add(new ElementSettingSlider((Setting<Number>) subsetting, getX(), getY(), getWidth() - 2, getHeight()));
            } else if (subsetting.getValue() instanceof Bind) {
                subsettings.add(new ElementSettingKey((Setting<Bind>) subsetting, getX(), getY(), getWidth() - 2, getHeight()));
            } else if (subsetting.getValue() instanceof Color) {
                subsettings.add(new ElementSettingColor((Setting<Color>) subsetting, getX(), getY(), getWidth() - 2, getHeight() * 4));
            } else if (subsetting.getValue() != null) {
                subsettings.add(new ElementSetting<>(subsetting, getX(), getY(), getWidth() - 2, getHeight()));
            }
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        defaultRender(mouseX, mouseY, mouseDelta);
    }

    protected void defaultRender(float mouseX, float mouseY, int mouseDelta) {
        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(getX() + 1, getY(), getWidth() - 2, getHeight(), new Color(0x252525));

        float offset = 0f;

        if (subsettings.stream().anyMatch(subsetting -> subsetting.getSetting().isVisible())) {
            offset = 8f;

            RenderUtil.rotate(getX() + 6.5f, getY() + 8.5f, -90 * expandAnimation.getAnimationFactor(), () -> {
                Wrapper.getFontUtil().entypo14.drawString(FontUtil.UNICODES_UI.RIGHT, getX() + 5.5f, getY() + 5f, new Color(255, 255, 255, 150), false);
            });
        }

        Wrapper.getFontUtil().productSansSmall.drawString(setting.getName(), getX() + 4f + offset, getY() + 4f, new Color(0xff8f8f8f), false);

        if (expandAnimation.getAnimationFactor() > 0 && subsettings.stream().anyMatch(subsetting -> subsetting.getSetting().isVisible())) {
            RenderUtil.rect(getX(), getY() + getHeight(), getWidth(), getOffset(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));

            float settingOffset = getY() + getHeight();

            for (ElementSetting<?> settingElement : subsettings) {
                if (settingElement.getSetting().isVisible()) {
                    settingElement.setX(getX() + 1);
                    settingElement.setY(settingOffset);

                    settingElement.draw(mouseX, mouseY, mouseDelta);

                    settingOffset += settingElement.getOffset();
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hovered(mouseX, mouseY) && click.equals(Click.RIGHT)) {
            expandAnimation.setState(!expandAnimation.getState());
        }

        if (expandAnimation.getState()) {
            subsettings.forEach(subsetting -> {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.mouseClicked(mouseX, mouseY, click);
                }
            });
        }

        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        if (expandAnimation.getState()) {
            subsettings.forEach(subsetting -> {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.mouseReleased(mouseX, mouseY, click);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (expandAnimation.getState()) {
            subsettings.forEach(subsetting -> {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    private float getSubsettingHeight() {
        float subsettingHeight = 0f;

        for (ElementSetting<?> subsetting : subsettings) {
            if (subsetting.getSetting().isVisible()) {
                subsettingHeight += subsetting.getOffset();
            }
        }

        return (float) (subsettingHeight * expandAnimation.getAnimationFactor());
    }

    @Override
    public float getOffset() {
        return getHeight() + getSubsettingHeight();
    }
}
