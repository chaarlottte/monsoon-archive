package wtf.monsoon.api.util.render;

import com.viaversion.viaversion.util.MathUtil;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.module.visual.Accent;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glColor4f;

public class ColorUtil extends Util {
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    /**
     * Gets the mix of 2 colors by %
     *
     * @param from  Start color
     * @param to    end color
     * @param value % value of the color interpolation (0-1)
     * @return The interpolated colour
     */
    public static Color interpolate(Color from, Color to, double value) {
        double progress = value > 1 ? 1 : (value < 0 ? 0 : value);
        int redDiff = to.getRed() - from.getRed();
        int greenDiff = to.getGreen() - from.getGreen();
        int blueDiff = to.getBlue() - from.getBlue();
        int alphaDiff = to.getAlpha() - from.getAlpha();
        int newRed = (int) (from.getRed() + (redDiff * progress));
        int newGreen = (int) (from.getGreen() + (greenDiff * progress));
        int newBlue = (int) (from.getBlue() + (blueDiff * progress));
        int newAlpha = (int) (from.getAlpha() + (alphaDiff * progress));
        return new Color(newRed, newGreen, newBlue, newAlpha);
    }

    public static Color withAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) MathUtils.clamp(0, 255, alpha));
    }

    public static void glColor(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;
        glColor4f((float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F, (float) a / 255.0F);
    }

    public static void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    //Credits to Plexter C#1339 for this :)
    public static int astolfoColors(int yOffset, int yTotal) {
        float speed = 2900F;
        float hue = (float) (System.currentTimeMillis() % (int) speed) + ((yTotal - yOffset) * 9);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return Color.HSBtoRGB(hue, 0.5f, 1F);
    }

    public static Color astolfoColorsC(int yOffset, int yTotal) {
        float speed = 2900F;
        float hue = (float) (System.currentTimeMillis() % (int) speed) + ((yTotal - yOffset) * 9);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 0.5) {
            hue = 0.5F - (hue - 0.5f);
        }
        hue += 0.5F;
        return new Color(Color.HSBtoRGB(hue, 0.5f, 1F));
    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f).getRGB();
    }

    public static Color rainbow(long delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f);
    }

    public static Color exhibition(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.6f, 1.0f);
    }

    public static Color exhibition(long delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.6f, 1.0f);
    }

    public static int darker(final int color, final float factor) {
        final int r = (int) ((color >> 16 & 0xFF) * factor);
        final int g = (int) ((color >> 8 & 0xFF) * factor);
        final int b = (int) ((color & 0xFF) * factor);
        final int a = color >> 24 & 0xFF;
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) | (a & 0xFF) << 24;
    }

    public static int fadeBetween(final int startColor, final int endColor, float progress) {
        if (progress > 1.0f) {
            progress = 1.0f - progress % 1.0f;
        }

        return fadeTo(startColor, endColor, progress);
    }

    public static Color fadeBetween(int speed, int index, Color start, Color end) {
        int tick = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        tick = (tick >= 180 ? 360 - tick : tick) * 2;
        return ColorUtil.interpolate(start, end, tick / 360f);
    }

    public static int fadeTo(final int startColor, final int endColor, final float progress) {
        final float invert = 1.0f - progress;
        final int r = (int) ((startColor >> 16 & 0xFF) * invert + (endColor >> 16 & 0xFF) * progress);
        final int g = (int) ((startColor >> 8 & 0xFF) * invert + (endColor >> 8 & 0xFF) * progress);
        final int b = (int) ((startColor & 0xFF) * invert + (endColor & 0xFF) * progress);
        final int a = (int) ((startColor >> 24 & 0xFF) * invert + (endColor >> 24 & 0xFF) * progress);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static Color fadeTo(final Color startColor, final Color endColor, final float progress) {
        final float invert = 1.0f - progress;
        final int r = (int) ((startColor.getRGB() >> 16 & 0xFF) * invert + (endColor.getRGB() >> 16 & 0xFF) * progress);
        final int g = (int) ((startColor.getRGB() >> 8 & 0xFF) * invert + (endColor.getRGB() >> 8 & 0xFF) * progress);
        final int b = (int) ((startColor.getRGB() & 0xFF) * invert + (endColor.getRGB() & 0xFF) * progress);
        final int a = (int) ((startColor.getRGB() >> 24 & 0xFF) * invert + (endColor.getRGB() >> 24 & 0xFF) * progress);
        return new Color(r, g, b, a);
    }

    public static Color[] getClientAccentTheme() {
        Accent.EnumAccents enumeration;
        Color customColor1;
        Color customColor2;
        Color[] clrs;

        try {
            enumeration = Wrapper.getModule(Accent.class).accents.getValue();
            customColor1 = Wrapper.getModule(Accent.class).customColor1.getValue();
            customColor2 = Wrapper.getModule(Accent.class).customColor2.getValue();
            clrs = enumeration.getClrs();
        } catch (Exception exception) {
            enumeration = Accent.EnumAccents.MONSOON_OLD;
            customColor1 = new Color(0, 238, 255, 255);
            customColor2 = new Color(135, 56, 232, 255);
            clrs = enumeration.getClrs();
        }

        switch (enumeration) {
            case FADE:
                return new Color[]{ customColor1, customColor2 };
            case FADE_STATIC:
                return new Color[]{ customColor1, customColor1.darker().darker().darker().darker() };
            case RAINBOW:
                return new Color[]{ ColorUtil.rainbow(0L), ColorUtil.rainbow(500L), ColorUtil.rainbow(1000L), ColorUtil.rainbow(1500L) };
            case EXHIBITION:
                return new Color[]{ ColorUtil.exhibition(0L), ColorUtil.exhibition(500L), ColorUtil.exhibition(1000L), ColorUtil.exhibition(1500L) };
            case STATIC:
                return new Color[]{ customColor1, customColor1 };
            case COTTON_CANDY:
                return new Color[]{ new Color(91, 206, 250), new Color(245, 169, 184) };
            case ASTOLFO:
                return new Color[]{ ColorUtil.astolfoColorsC(0, 100), ColorUtil.astolfoColorsC(0, 100) };
            default:
                return clrs;
        }

    }

    public static Color[] getClientAccentTheme(int yOffset, int yTotal) {
        Accent.EnumAccents enumeration;
        Color customColor1;
        Color customColor2;
        Color[] clrs;

        try {
            enumeration = Wrapper.getModule(Accent.class).accents.getValue();
            customColor1 = Wrapper.getModule(Accent.class).customColor1.getValue();
            customColor2 = Wrapper.getModule(Accent.class).customColor2.getValue();
            clrs = enumeration.getClrs();
        } catch (Exception exception) {
            enumeration = Accent.EnumAccents.MONSOON_OLD;
            customColor1 = new Color(0, 238, 255, 255);
            customColor2 = new Color(135, 56, 232, 255);
            clrs = enumeration.getClrs();
        }

        switch (enumeration) {
            case FADE:
                return new Color[]{ ColorUtil.fadeBetween(5, (int) ((System.currentTimeMillis() + yTotal * 3) % 1500 / (1500 / 2)), customColor1, customColor2) };
            case FADE_STATIC:
                return new Color[]{ ColorUtil.fadeBetween(5, (int) ((System.currentTimeMillis() + yTotal * 3) % 1500 / (1500 / 2)), customColor1, customColor1.darker().darker().darker().darker()) };
            case RAINBOW:
                return new Color[]{ ColorUtil.rainbow(yTotal * 5L) };
            case EXHIBITION:
                return new Color[]{ ColorUtil.exhibition(yTotal * 5L) };
            case STATIC:
                return new Color[]{ customColor1, customColor1 };
            case COTTON_CANDY:
                return new Color[]{ ColorUtil.fadeBetween(5, (int) ((System.currentTimeMillis() + yTotal * 3) % 1500 / (1500 / 2)), new Color(91, 206, 250), new Color(245, 169, 184)) };
            case ASTOLFO:
                return new Color[]{ ColorUtil.astolfoColorsC(yOffset, yTotal) };
            case MONSOON_NEW:
            case MONSOON_OLD:
                return new Color[]{ ColorUtil.fadeBetween(5, (int) ((System.currentTimeMillis() + yTotal * 3) % 1500 / (1500 / 2)), clrs[0], clrs[1]) };
            default:
                return clrs;
        }
    }

    public static Color[] getAccent() {
        return new Color[] {
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[0] : ColorUtil.fadeBetween(10, 270, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[1] : ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0] : ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1] : ColorUtil.fadeBetween(10, 90, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1])
        };
    }

    public static Color[] getAccent(float darken) {
        Color[] colours = getAccent();

        for (int i = 0; i < 3; i++) {
            colours[i] = new Color(darker(colours[i].getRGB(), darken));
        }

        return colours;
    }

    public static Color integrateAlpha(Color colour, float alpha) {
        return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), MathHelper.clamp_int((int) alpha, 0, 255));
    }
}
