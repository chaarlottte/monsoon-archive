package wtf.monsoon.api.util.font.impl;

import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.IFontRenderer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.module.visual.Blur;
import wtf.monsoon.impl.module.visual.ClickGUI;

import java.awt.*;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class FontRenderer implements IFontRenderer {

    private final float height;

    @Getter
    private final Image defaultFont;

    private final String magicAllowedCharacters = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■";

    private final int[] hexcolors = {
            0,
            170,
            43520,
            43690,
            0xAA0000,
            0xAA00AA,
            0xFFAA00,
            0xAAAAAA,
            0x555555,
            0x5555FF,
            0x55FF55,
            0x55FFFF,
            0xFF5555,
            0xFF55FF,
            0xFFFF55,
            0xFFFFFF
    };

    private final Random random = new Random();

    public FontRenderer(Font font) {
        defaultFont = new Image(font, 0, 255);
        height = defaultFont.getHeight() / 2;
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        return (int) drawString(text, x, y, new Color(color), true);
    }

    public int drawString(String text, float x, float y, int color) {
        return (int) drawString(text, x, y, new Color(color), false);
    }

    public int drawStringWithShadow(String text, float x, float y, Color color) {
        return (int) drawString(text, x, y, color, true);
    }

    @Override
    public int drawString(String text, float x, float y, Color color) {
        return drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, float x, float y, Color color, boolean shadow) {
        return drawString(text, x - (getStringWidth(text) / 2f), y, color, shadow);
    }

    public void drawStringWithGradient(String text, float x, float y, Color a, Color b, boolean shadow) {
        int length = text.length();

        double factorIncrease = 1f / length;

        double factor = 0;
        for (char ch : text.toCharArray()) {
            drawString(String.valueOf(ch), x, y, ColorUtil.interpolate(a, b, factor), shadow);

            factor += factorIncrease;

            x += getStringWidth(String.valueOf(ch));
        }
    }

    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        return (int) drawString(text, x, y, new Color(color), dropShadow);
    }

    public int drawString(String text, float x, float y, Color color, boolean dropShadow) {
        if (text.contains("\n")) {
            String[] parts = text.split("\n");
            float newY = 0.0f;

            for (String s : parts) {
                if (dropShadow) {
                    drawText(s, x + 0.6f, y + newY + 0.6f, new Color(0, 0, 0, 150), true);
                }

                drawText(s, x, y + newY, color, dropShadow);
                newY += getHeight();
            }

            return 0;
        }

        if (dropShadow) {
            drawText(text, x + 0.5f, y + 0.5f, new Color(0, 0, 0, 150), true);
        }

        return (int) drawText(text, x, y, color, false);
    }

    private float drawText(String text, float x, float y, Color color, boolean ignore) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        if (Wrapper.getModule(ClickGUI.class) != null && Wrapper.getModule(ClickGUI.class).british.getValue()) {
            text = text.replaceAll("(?i)color", "Colour");
        }

        int texture = GlStateManager.getBoundTexture();

        glPushAttrib(8256);
        glTranslatef(x, y, 0);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        int currentcolor = color.getRGB();

        if ((currentcolor & 0xFC000000) == 0) {
            currentcolor |= 0xFF000000;
        }

        int alpha = currentcolor >> 24 & 0xFF;

        if (text.contains("§")) {
            String[] parts = text.split("§");
            float width = 0f;
            boolean randomCase = false;

            for (String part : parts) {
                if (part.isEmpty()) {
                    continue;
                }

                if (part.equals(parts[0])) {
                    getDefaultFont().drawString(part, width, 0.0, ColorUtil.integrateAlpha(new Color(currentcolor), color.getAlpha()));
                    width += getDefaultFont().getStringWidth(part);
                    continue;
                }

                String words = part.substring(1);
                char type = part.charAt(0);
                int index = "0123456789abcdefklmnor".indexOf(type);

                switch (index) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15: {
                        if (!ignore) {
                            currentcolor = hexcolors[index] | alpha << 24;
                        }

                        randomCase = false;
                        break;
                    }

                    case 16: {
                        randomCase = true;
                        break;
                    }

                    case 18:
                        break;

                    case 21: {
                        currentcolor = color.getRGB();

                        if ((currentcolor & 0xFC000000) == 0) {
                            currentcolor |= 0xFF000000;
                        }

                        randomCase = false;
                    }
                }

                getDefaultFont().drawString(randomCase ? randomMagicText(words) : words, width, 0.0, ColorUtil.integrateAlpha(new Color(currentcolor), color.getAlpha()));

                width += getDefaultFont().getStringWidth(words);
            }
        } else {
            getDefaultFont().drawString(text, 0.0, 0.0, ColorUtil.integrateAlpha(new Color(currentcolor), color.getAlpha()));
        }

        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glTranslatef(-x, -y, 0);

        if (texture >= 0) {
            glBindTexture(GL_TEXTURE_2D, texture);
        }

        glColor4f(1, 1, 1, 1);

        glPopAttrib();

        return x + getStringWidth(text);
    }

    public float getStringWidthF(String text) {
        if (text.contains("§")) {
            String[] parts = text.split("§");
            float width = 0f;

            for (String part : parts) {
                if (part.isEmpty()) {
                    continue;
                }

                if (part.equals(parts[0])) {
                    width += getDefaultFont().getStringWidth(part);
                    continue;
                }

                width += getDefaultFont().getStringWidth(part.substring(1));
            }

            return width / 2f;
        }

        return defaultFont.getStringWidth(text) / 2f;
    }

    public int getStringWidth(String text) {
        return (int) (getStringWidthF(text));
    }

    @Override
    public int getHeight() {
        return (int) height;
    }

    @Override
    public float getHeightF() {
        return height;
    }

    private String randomMagicText(String text) {
        StringBuilder builder = new StringBuilder();

        text.chars().mapToObj(element -> (char) element).forEach(ch -> {
            if (!ChatAllowedCharacters.isAllowedCharacter(ch)) {
                return;
            }

            builder.append(magicAllowedCharacters.charAt(random.nextInt(magicAllowedCharacters.length())));
        });

        return builder.toString();
    }

}