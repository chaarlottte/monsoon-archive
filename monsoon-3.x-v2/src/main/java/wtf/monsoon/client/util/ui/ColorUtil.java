package wtf.monsoon.client.util.ui;

import java.awt.*;


public class ColorUtil {
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

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
}
