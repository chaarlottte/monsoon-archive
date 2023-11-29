package wtf.monsoon.impl.ui.panel.elements.setting;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ElementSettingEnum extends ElementSetting<Enum<?>> {

    private final ArrayList<Button> buttons = new ArrayList<>();

    private final Animation expandAnimation = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT);
    private final Animation rotate = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT);

    public ElementSettingEnum(Setting<Enum<?>> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);

        Enum<?> enumeration = set.getValue();
        String[] values = Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);

        for (int i = 0; i < values.length; i++) {
            buttons.add(new Button(i, getX(), getY(), getWidth(), getHeight()));
        }
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        super.draw(mouseX, mouseY, mouseDelta);

        rotate.setState(expandAnimation.getState());

        RenderUtil.rotate(getX() + getWidth() - 9, getY() + getHeight() / 2f - 0.5f, 360 - (float) (rotate.getAnimationFactor() * 180F), () -> {
            Wrapper.getFontUtil().entypo14.drawCenteredString(FontUtil.UNICODES_UI.UP, getX() + getWidth() - 9, getY() + getHeight() / 2f - Wrapper.getFontUtil().entypo14.getHeight() / 2f - 1.5f, new Color(0xff8f8f8f), false);
        });

        if (expandAnimation.getAnimationFactor() > 0) {
            int i = 0;

            for (Button button : buttons) {
                button.setX(getX());
                button.setY(getY() + getHeight() + getHeight() * i);
                button.draw(mouseX, mouseY, mouseDelta);
                i++;
            }
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hovered(mouseX, mouseY) && click.equals(Click.LEFT)) {
            expandAnimation.setState(!expandAnimation.getState());
        }

        if (expandAnimation.getState()) {
            for (Button button : buttons) {
                button.mouseClicked(mouseX, mouseY, click);
            }
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public float getOffset() {
        return super.getOffset() + (buttons.size() * getHeight()) * (float) expandAnimation.getAnimationFactor();
    }

    private class Button extends Drawable {
        private final int ordinal;
        private final Animation hover = new Animation(() -> 200F, false, () -> Easing.LINEAR);

        public Button(int ordinal, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.ordinal = ordinal;
        }

        @Override
        public void draw(float mouseX, float mouseY, int mouseDelta) {
            hover.setState(ordinal == getSetting().getValue().ordinal());

            Enum<?> enumeration = getSetting().getValue();

            RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]));
            RenderUtil.rect(getX() + 1.5f, getY(), getWidth() - 3, getHeight(), ColorUtil.interpolate(new Color(0x2A2A2A), new Color(0x333333), hover.getAnimationFactor()));

            Wrapper.getFontUtil().productSansSmall.drawString(StringUtil.formatEnum(Arrays.stream(enumeration.getClass().getEnumConstants()).filter(e -> e.ordinal() == ordinal).collect(Collectors.toList()).get(0)), getX() + 4f, getY() + getHeight() / 2f - Wrapper.getFont().getHeight() / 2f, new Color(0xff8f8f8f), false);
        }

        @Override
        public boolean mouseClicked(float mouseX, float mouseY, Click click) {
            if (hovered(mouseX, mouseY)) {
                Enum<?> enumeration = getSetting().getValue();
                getSetting().setValue(Enum.valueOf(enumeration.getClass(), Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new)[ordinal]));
            }

            return false;
        }

        @Override
        public void mouseReleased(float mouseX, float mouseY, Click click) {

        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {

        }
    }
}
