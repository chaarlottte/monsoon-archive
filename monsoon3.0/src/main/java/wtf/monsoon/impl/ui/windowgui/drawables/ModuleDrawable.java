package wtf.monsoon.impl.ui.windowgui.drawables;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BindDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BooleanDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.EnumDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.SliderDrawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Surge
 * @since 28/08/2022
 */
public class ModuleDrawable extends Drawable {

    @Getter
    private final CategoryDrawable parent;

    @Getter
    private final Module module;

    @Getter
    @Setter
    private CategoryDrawable.Column column;

    @Getter
    private final List<SettingDrawable<?>> settings = new ArrayList<>();

    private final Animation hover = new Animation(() -> 200f, false, () -> Easing.LINEAR);
    private final Animation descriptionHover = new Animation(() -> 2000f, false, () -> Easing.LINEAR);
    private final Animation enabled = new Animation(() -> 200f, false, () -> Easing.LINEAR);
    private final Animation expand = new Animation(() -> 300f, false, () -> Easing.CUBIC_IN_OUT);

    public ModuleDrawable(CategoryDrawable parent, Module module, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.parent = parent;
        this.module = module;

        this.module.getSettings().forEach(setting -> {
            if (setting.getValue() instanceof Boolean) {
                settings.add(new BooleanDrawable(this, (Setting<Boolean>) setting, getX() + 2, getY(), getWidth() - 4, getHeight()));
            } else if (setting.getValue() instanceof Enum<?>) {
                settings.add(new EnumDrawable(this, (Setting<Enum<?>>) setting, getX() + 2, getY(), getWidth() - 4, getHeight()));
            } else if (setting.getValue() instanceof Number) {
                settings.add(new SliderDrawable(this, (Setting<Number>) setting, getX() + 2, getY(), getWidth() - 4, getHeight() + 10));
            } else if (setting.getValue() instanceof Bind) {
                settings.add(new BindDrawable(this, (Setting<Bind>) setting, getX() + 2, getY(), getWidth() - 4, getHeight()));
            }
        });
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        hover.setState(MathUtils.within(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY));
        descriptionHover.setState(MathUtils.within(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) && Wrapper.getFont().getStringWidth(getModule().getDescription()) >= getWidth() - 5);
        enabled.setState(module.isEnabled());

        RoundedUtils.round(getX(), getY(), getWidth(), getHeight() + getOffset(), 6,
                // wtfrick
                ColorUtil.interpolate(WindowGUI.INTERACTABLE, WindowGUI.INTERACTABLE.brighter(), 0.6)
        );

        RoundedUtils.gradient(getX(), getY(), getWidth(), getHeight() + getOffset(), 6, (float) enabled.getAnimationFactor(),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 45, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 135, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255))
        );

        RoundedUtils.round(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 6, ColorUtil.interpolate(WindowGUI.INTERACTABLE, WindowGUI.HOVER, hover.getAnimationFactor()));

        Wrapper.getFont().drawString(getModule().getName(), getX() + 5, getY() + 2, Color.WHITE, false);

        RenderUtil.pushScissor(getX() + 2, MathHelper.clamp_float(getY() + 15, getParent().getParent().getY() + 25, 100000), getWidth() - 4, ((getParent().getParent().getY() + 25) + (getParent().getParent().getHeight() - 82)) - (getY() + 13));

        Wrapper.getFont().drawString(getModule().getDescription(), (float) ((getX() + 5) - ((Wrapper.getFont().getStringWidth(getModule().getDescription()) - (getWidth() - 10)) * descriptionHover.getAnimationFactor())), getY() + 15, Color.GRAY.brighter(), false);

        RenderUtil.popScissor();

        if (expand.getAnimationFactor() > 0) {
            float subY = getY() + 30;

            RenderUtil.pushScissor(getX() + 2, MathHelper.clamp_float(getY() + 30, getParent().getParent().getY() + 26, 100000), getWidth() - 4, MathHelper.clamp_double(getOffset(), 0, (((getParent().getParent().getY() + 25) + (getParent().getParent().getHeight() - 80)) - (getY() + 30)) * expand.getAnimationFactor()));

            for (SettingDrawable<?> settingDrawable : settings) {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.setX(getX() + 2);
                    settingDrawable.setY(subY);

                    settingDrawable.draw(mouseX, mouseY, mouseDelta);

                    subY += settingDrawable.getHeight() + settingDrawable.getOffset();
                }
            }

            RenderUtil.popScissor();
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hover.getState()) {
            if (click.equals(Click.LEFT)) {
                module.toggle();
            } else if (click.equals(Click.RIGHT)) {
                expand.setState(!expand.getState());
            }
        }

        if (expand.getState()) {
            settings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.mouseClicked(mouseX, mouseY, click);
                }
            });
        }

        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        if (expand.getState()) {
            settings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.mouseReleased(mouseX, mouseY, click);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (expand.getState()) {
            settings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    @Override
    public float getOffset() {
        float settingHeight = 2f;

        for (SettingDrawable<?> setting : settings) {
            if (setting.getSetting().isVisible()) {
                settingHeight += setting.getHeight() + setting.getOffset();
            }
        }

        return (float) (settingHeight * expand.getAnimationFactor());
    }

}
