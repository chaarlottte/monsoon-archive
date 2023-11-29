package wtf.monsoon.impl.ui.windowgui.drawables;

import lombok.Getter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BindDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.BooleanDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.EnumDrawable;
import wtf.monsoon.impl.ui.windowgui.drawables.setting.SliderDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Surge
 * @since 28/08/2022
 */
public class SettingDrawable<T> extends Drawable {

    @Getter
    private final Drawable parent;

    @Getter
    private final Setting<T> setting;

    @Getter
    private final List<SettingDrawable<?>> subsettings = new ArrayList<>();

    @Getter
    private final Animation hover = new Animation(() -> 200f, false, () -> Easing.LINEAR);

    @Getter
    private final Animation descriptionHover = new Animation(() -> 2000f, false, () -> Easing.LINEAR);

    @Getter
    private final Animation expand = new Animation(() -> 400f, false, () -> Easing.CUBIC_IN_OUT);

    public SettingDrawable(Drawable parent, Setting<T> setting, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.parent = parent;
        this.setting = setting;

        this.setting.getChildren().forEach(subsetting -> {
            if (subsetting.getValue() instanceof Boolean) {
                subsettings.add(new BooleanDrawable(this, (Setting<Boolean>) subsetting, getX() + 2, getY(), getWidth() - 4, getHeight()));
            } else if (subsetting.getValue() instanceof Enum<?>) {
                subsettings.add(new EnumDrawable(this, (Setting<Enum<?>>) subsetting, getX() + 2, getY(), getWidth() - 4, getHeight()));
            } else if (subsetting.getValue() instanceof Number) {
                subsettings.add(new SliderDrawable(this, (Setting<Number>) subsetting, getX() + 2, getY(), getWidth() - 4, getHeight() + 10));
            } else if (subsetting.getValue() instanceof Bind) {
                subsettings.add(new BindDrawable(this, (Setting<Bind>) subsetting, getX() + 2, getY(), getWidth() - 4, getHeight()));
            }
        });
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        hover.setState(MathUtils.within(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY));
        descriptionHover.setState(MathUtils.within(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) && Wrapper.getFont().getStringWidth(getSetting().getDescription()) >= getWidth() - 5);

        if (expand.getAnimationFactor() > 0) {
            float subY = getY() + 30;

            for (SettingDrawable<?> subsetting : subsettings) {
                if (subsetting.getSetting().isVisible()) {
                    subsetting.setX(getX() + 2);
                    subsetting.setY(subY);

                    subsetting.draw(mouseX, mouseY, mouseDelta);

                    subY += subsetting.getHeight() + subsetting.getOffset();
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hover.getState() && click.equals(Click.RIGHT)) {
            expand.setState(!expand.getState());
        }

        if (expand.getState()) {
            subsettings.forEach(settingDrawable -> {
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
            subsettings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.mouseReleased(mouseX, mouseY, click);
                }
            });
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (expand.getState()) {
            subsettings.forEach(settingDrawable -> {
                if (settingDrawable.getSetting().isVisible()) {
                    settingDrawable.keyTyped(typedChar, keyCode);
                }
            });
        }
    }

    @Override
    public float getOffset() {
        float subsettingHeight = 0f;

        for (SettingDrawable<?> subsetting : subsettings) {
            if (subsetting.getSetting().isVisible()) {
                subsettingHeight += subsetting.getHeight();
            }
        }

        return (float) (subsettingHeight * expand.getAnimationFactor());
    }

}
