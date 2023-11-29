package wtf.monsoon.api.util.font;

import java.awt.*;

public interface IFontRenderer {

    public int drawStringWithShadow(String text, float x, float y, int color);
    public int drawString(String text, float x, float y, int color);
    public int drawString(String text, float x, float y, int color, boolean dropShadow);

    public int drawStringWithShadow(String text, float x, float y, Color color);
    public int drawString(String text, float x, float y, Color color);
    public int drawString(String text, float x, float y, Color color, boolean dropShadow);
    public float getStringWidthF(String text);
    public int getStringWidth(String text);
    public int getHeight();
    public float getHeightF();

}
