package wtf.monsoon.impl.ui.panel.elements;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.luaj.vm2.ast.Str;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.panel.elements.setting.*;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ElementModule extends Drawable {

    private final Module module;

    private final Animation expandAnimation = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT);
    private final ArrayList<ElementSetting<?>> settings = new ArrayList<>();

    public ElementModule(Module module, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.module = module;

        for (Setting<?> setting : module.getSettings()) {
            if (setting.getValue() instanceof Enum) {
                settings.add(new ElementSettingEnum((Setting<Enum<?>>) setting, getX(), getY(), getWidth(), getHeight()));
            } else if (setting.getValue() instanceof Boolean) {
                settings.add(new ElementSettingBoolean((Setting<Boolean>) setting, getX(), getY(), getWidth(), getHeight()));
            } else if (setting.getValue() instanceof Number) {
                settings.add(new ElementSettingSlider((Setting<Number>) setting, getX(), getY(), getWidth(), getHeight()));
            } else if (setting.getValue() instanceof Bind) {
                settings.add(new ElementSettingKey((Setting<Bind>) setting, getX(), getY(), getWidth(), getHeight()));
            } else if (setting.getValue() instanceof Color) {
                settings.add(new ElementSettingColor((Setting<Color>) setting, getX(), getY(), getWidth(), getHeight() * 4));
            } else if (setting.getValue() != null) {
                settings.add(new ElementSetting<>(setting, getX(), getY(), getWidth(), getHeight()));
            }
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        if (expandAnimation.getAnimationFactor() > 0) {
            float settingOffset = getY() + getHeight();

            for (ElementSetting<?> settingElement : settings) {
                if (settingElement.getSetting().isVisible()) {
                    settingElement.setX(getX());
                    settingElement.setY(settingOffset);

                    settingElement.draw(mouseX, mouseY, mouseDelta);

                    settingOffset += settingElement.getOffset();
                }
            }
        }

        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), new Color(0x1F1F1F));
        Wrapper.getFontUtil().productSansSmall.drawString(module.getName(), getX() + 4, (getY() + getHeight() / 2f) - (Wrapper.getFont().getHeight() / 2f), module.isEnabled() ? new Color(0xff8f8f8f) : new Color(0xff5b5b5b), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hovered(mouseX, mouseY)) {
            switch (click) {
                case LEFT: {
                    module.toggle();
                    break;
                }

                case RIGHT: {
                    expandAnimation.setState(!expandAnimation.getState());
                    break;
                }
            }
        }

        if (expandAnimation.getState()) {
            for (ElementSetting<?> setting : settings) {
                if (setting.getSetting().isVisible()) {
                    setting.mouseClicked(mouseX, mouseY, click);
                }
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (ElementSetting<?> setting : settings) {
            setting.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public float getOffset() {
        float settingHeight = 0f;

        for (ElementSetting<?> settingElement : settings) {
            if (settingElement.getSetting().isVisible()) {
                settingHeight += settingElement.getOffset();
            }
        }

        return (float) (getHeight() + (settingHeight * expandAnimation.getAnimationFactor()));
    }
}
