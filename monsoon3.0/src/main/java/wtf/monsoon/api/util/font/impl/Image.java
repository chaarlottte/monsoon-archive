package wtf.monsoon.api.util.font.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.RenderingHints.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class Image {

    @Getter
    private final Font font;

    private final CharLocation[] charLocations;
    private int fontHeight = -1;
    private int textureID = 0;
    private int textureWidth = 0;
    private int textureHeight = 0;

    public Image(Font font, int startChar, int endChar) {
        this.font = font;

        charLocations = new CharLocation[endChar];
        renderBitmap(startChar, endChar);
    }

    public void drawString(String text, double x, double y, Color colour) {
        glPushMatrix();

        glScaled(0.25, 0.25, 0);
        glTranslated(x * 2, y * 2, 0);

        glBindTexture(GL_TEXTURE_2D, textureID);

        ColorUtil.glColor(colour.getRGB());

        double currX = 0.0;

        glBegin(GL_QUADS);

        for (char ch : text.toCharArray()) {
            CharLocation fontChar;

            if (Character.getNumericValue(ch) >= charLocations.length) {
                glEnd();
                glScaled(4.0, 4.0, 4.0);
                Wrapper.getMinecraft().fontRendererObj.drawString(String.valueOf(ch), (float) currX * 0.25f, 0.0f, colour.getRGB(), false);

                currX += (double) Wrapper.getMinecraft().fontRendererObj.getStringWidth(String.valueOf(ch)) * 4.0;

                glScaled(0.25, 0.25, 0);
                glBindTexture(GL_TEXTURE_2D, textureID);
                ColorUtil.glColor(colour.getRGB());

                glBegin(GL_QUADS);
                continue;
            }

            if (charLocations.length <= ch || (fontChar = charLocations[ch]) == null) {
                continue;
            }

            drawChar(fontChar, (float) currX, 0.0f);
            currX += (double) fontChar.width - 8.0;
        }

        glEnd();

        glBindTexture(GL_TEXTURE_2D, 0);

        glPopMatrix();
    }

    private void drawChar(CharLocation ch, float x, float y) {
        float width = ch.width;
        float height = ch.height;

        float renderX = ch.x / (float) textureWidth;
        float renderY = ch.y / (float) textureHeight;
        float renderWidth = width / (float) textureWidth;
        float renderHeight = height / (float) textureHeight;

        glTexCoord2f(renderX, renderY);
        glVertex2f(x, y);
        glTexCoord2f(renderX, renderY + renderHeight);
        glVertex2f(x, y + height);
        glTexCoord2f(renderX + renderWidth, renderY + renderHeight);
        glVertex2f(x + width, y + height);
        glTexCoord2f(renderX + renderWidth, renderY);
        glVertex2f(x + width, y);
    }

    private void renderBitmap(int startChar, int endChar) {
        BufferedImage[] images = new BufferedImage[endChar];

        int row = 0;
        int charX = 0;
        int charY = 0;

        for (int targetChar = startChar; targetChar < endChar; targetChar++) {
            BufferedImage fontImage = drawCharToImage((char) targetChar);
            CharLocation fontChar = new CharLocation(charX, charY, fontImage.getWidth(), fontImage.getHeight());

            if (fontChar.height > fontHeight) {
                fontHeight = fontChar.height;
            }

            if (fontChar.height > row) {
                row = fontChar.height;
            }

            if (charLocations.length <= targetChar) {
                continue;
            }

            charLocations[targetChar] = fontChar;
            images[targetChar] = fontImage;

            if ((charX += fontChar.width) <= 2048) {
                continue;
            }

            if (charX > textureWidth) {
                textureWidth = charX;
            }

            charX = 0;
            charY += row;
            row = 0;
        }

        textureHeight = charY + row;

        BufferedImage image = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        graphics.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);

        graphics.setFont(font);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, textureWidth, textureHeight);
        graphics.setColor(Color.WHITE);

        for (int targetChar = startChar; targetChar < endChar; ++targetChar) {
            if (images[targetChar] == null || charLocations[targetChar] == null) {
                continue;
            }

            graphics.drawImage(images[targetChar], charLocations[targetChar].x, charLocations[targetChar].y, null);
        }

        textureID = TextureUtil.uploadTextureImageAllocate(glGenTextures(), image, true, true);
    }

    private BufferedImage drawCharToImage(char ch) {
        Graphics2D graphics = (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();

        graphics.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);

        FontMetrics metrics = graphics.getFontMetrics();

        int width = metrics.charWidth(ch) + 8;
        int height = metrics.getHeight() + 3;

        if (width <= 8) {
            width = 7;
        }

        if (height <= 0) {
            height = font.getSize();
        }

        BufferedImage fontImage = new BufferedImage(width, height, 2);
        graphics = (Graphics2D) fontImage.getGraphics();

        graphics.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(ch), 0, metrics.getAscent());

        return fontImage;
    }

    public int getStringWidth(String text) {
        int width = 0;

        for (int ch : text.toCharArray()) {
            int index = ch < charLocations.length ? ch : 3;

            CharLocation fontChar;

            if (charLocations.length <= index || (fontChar = charLocations[index]) == null) {
                width += Wrapper.getMinecraft().fontRendererObj.getStringWidth(String.valueOf(ch)) / 4.0;
                continue;
            }

            width += fontChar.width - 8;
        }

        return width / 2;
    }

    public float getHeight() {
        return (fontHeight - 8f) / 2f;
    }

    @Data
    @AllArgsConstructor
    private static class CharLocation {
        @Getter
        private final int x, y, width, height;
    }

}