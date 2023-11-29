package wtf.monsoon.impl.ui.panel.elements.setting;

import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;

public class ElementSettingColor extends ElementSetting<Color> {

    private boolean dragging, sliding;

    public ElementSettingColor(Setting<Color> set, float x, float y, float width, float height) {
        super(set, x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        if (!Mouse.isButtonDown(0)) {
            sliding = false;
            dragging = false;
        }

        Color originalSetColor = getSetting().getValue();
        float[] hsb = new float[3];
        Color.RGBtoHSB(originalSetColor.getRed(), originalSetColor.getGreen(), originalSetColor.getBlue(), hsb);

        float pickerX = getX() + 4;
        float pickerY = getY() + 18;
        float pickerWidth = getWidth() - 8;
        float pickerHeight = getHeight() - 30;

        float RAWpickerTargetX = Math.min(Math.max(0, pickerWidth + (pickerX - mouseX)), pickerWidth);
        float RAWpickerTargetY = Math.min(Math.max(0, pickerHeight + (pickerY - mouseY)), pickerHeight);

        float pickerTargetX = RAWpickerTargetX / pickerWidth;
        float pickerTargetY = RAWpickerTargetY / pickerHeight;

        float hueX = pickerX;
        float hueY = getY() + getHeight() - 8;
        float hueWidth = pickerWidth;
        float hueHeight = 4;

        float RAWhueTargetX = RAWpickerTargetX;
        float hueTargetX = RAWhueTargetX / hueWidth;

        Color hueCol = new Color(Color.HSBtoRGB(hsb[0], 1.0f, 1.0f));
        float[] hsb2 = Color.RGBtoHSB(getSetting().getValue().getRed(), getSetting().getValue().getGreen(), getSetting().getValue().getBlue(), null);

        float[] newHSB = {
                sliding ? hueTargetX : 1f - hsb2[0],
                dragging ? pickerTargetX : 1f - hsb2[1],
                dragging ? pickerTargetY : hsb2[2]
        };

        getSetting().setValue(new Color(Color.HSBtoRGB(1f - newHSB[0], 1f - newHSB[1], newHSB[2])));


        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), ColorUtil.fadeBetween(10, 0, ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.getClientAccentTheme()[1]));
        RenderUtil.rect(getX() + 1, getY(), getWidth() - 2, getHeight(), new Color(0x252525));


        RoundedUtils.gradient(pickerX, pickerY, pickerWidth, pickerHeight, 4, 1.0f,
                Color.WHITE, Color.BLACK, hueCol, Color.BLACK
        );


        for (int i = 0; i < hueWidth; i++) {
            RenderUtil.rect(hueX + i, hueY, 1, hueHeight, new Color(Color.HSBtoRGB((i / (getWidth() - 8F)), 1.0F, 1.0F)));
        }

        RenderUtil.rect(hueX + hueWidth * ((hsb[0] - (1 / 360f))), hueY, 1, hueHeight, Color.WHITE);

        float circleWidth = 3;
        RoundedUtils.circle(pickerX + (hsb2[1]) * pickerWidth, pickerY + (1 - hsb2[2]) * pickerHeight, circleWidth, Color.WHITE);

        Wrapper.getFontUtil().productSansSmall.drawString(getSetting().getName(), getX() + 4f, getY() + 4, new Color(0xff8f8f8f), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        float pickerX = getX() + 4;
        float pickerY = getY() + 18;
        float pickerWidth = getWidth() - 8;
        float pickerHeight = getHeight() - 30;

        float hueX = pickerX;
        float hueY = getY() + getHeight() - 8;
        float hueWidth = pickerWidth;
        float hueHeight = 4;

        if (mouseX >= pickerX && mouseY >= pickerY && mouseX <= pickerX + pickerWidth && mouseY <= pickerY + pickerHeight) {
            if (click.equals(Click.LEFT)) {
                dragging = true;
            }
        }

        if (mouseX >= hueX && mouseY >= hueY && mouseX <= hueX + hueWidth && mouseY <= hueY + hueHeight) {
            if (click.equals(Click.LEFT)) {
                sliding = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, click);
    }
}
