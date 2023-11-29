package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.shader.Shader;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RoundedUtils extends Util {
    private static final Shader rectShader = new Shader(new ResourceLocation("monsoon/shader/roundedRect.frag"));
    private static final Shader outlineShader = new Shader(new ResourceLocation("monsoon/shader/roundedOutline.frag"));
    private static final Shader outlineGradientShader = new Shader(new ResourceLocation("monsoon/shader/roundedGradientOutline.frag"));
    private static final Shader textureShader = new Shader(new ResourceLocation("monsoon/shader/texture.frag"));
    private static final Shader gradientShader = new Shader(new ResourceLocation("monsoon/shader/roundedGradientRect.frag"));
    private static final Shader shadowShader = new Shader(new ResourceLocation("monsoon/shader/shadowRect.frag"));
    private static final Shader shadowGradientShader = new Shader(new ResourceLocation("monsoon/shader/shadowGradient.frag"));
    private static final Shader testShader = new Shader(new ResourceLocation("monsoon/shader/test.frag"));

    public static void glRound(float x, float y, float width, float height, float radius, int color1, int color2, int color3, int color4) {
        glShadeModel(GL_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        glBegin(GL_POLYGON);
        int rad;

        ColorUtil.glColor(color1);
        for (rad = 0; rad <= 90; ++rad)
            glVertex2d(x + radius + Math.sin(rad * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(rad * Math.PI / 180.0D) * radius * -1.0D);

        ColorUtil.glColor(color2);
        for (rad = 90; rad <= 180; ++rad)
            glVertex2d(x + radius + Math.sin(rad * Math.PI / 180.0D) * radius * -1.0D, y + height - radius + Math.cos(rad * Math.PI / 180.0D) * radius * -1.0D);

        ColorUtil.glColor(color3);
        for (rad = 0; rad <= 90; ++rad)
            glVertex2d(x + width - radius + Math.sin(rad * Math.PI / 180.0D) * radius, y + height - radius + Math.cos(rad * Math.PI / 180.0D) * radius);

        ColorUtil.glColor(color4);
        for (rad = 90; rad <= 180; ++rad)
            glVertex2d(x + width - radius + Math.sin(rad * Math.PI / 180.0D) * radius, y + radius + Math.cos(rad * Math.PI / 180.0D) * radius);

        glEnd();
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_FLAT);

        GlStateManager.resetColor();
    }

    public static void glRound(HUDModule parent, float radius, Color color) {
        glRound(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, color.getRGB());
    }

    public static void glRound(float x, float y, float width, float height, float radius, int color) {
        glRound(x, y, width, height, radius, color, color, color, color);
    }

    public static void circle(float x, float y, float radius, Color color) {
        round(x - radius, y - radius, radius * 2, radius * 2, radius - 1, color);
    }

    public static void gradient(float x, float y, float width, float height, float radius, float opacity, Color[] colours) {
        gradient(x, y, width, height, radius, opacity, colours[0], colours[1], colours[2], colours[3]);
    }

    public static void gradient(float x, float y, float width, float height, float radius, float opacity, Color c1, Color c2, Color c3, Color c4) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram(gradientShader.getProgram());

        GL20.glUniform2f(GL20.glGetUniformLocation(gradientShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(gradientShader.getProgram(), "radius"), radius);

        GL20.glUniform1f(GL20.glGetUniformLocation(gradientShader.getProgram(), "alpha"), opacity);
        GL20.glUniform4f(GL20.glGetUniformLocation(gradientShader.getProgram(), "color1"), c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(gradientShader.getProgram(), "color2"), c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(gradientShader.getProgram(), "color3"), c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(gradientShader.getProgram(), "color4"), c4.getRed() / 255f, c4.getGreen() / 255f, c4.getBlue() / 255f, 1.0f);

        rect(x, y, width, height);

        GL20.glUseProgram(0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void texture(float x, float y, float width, float height, float radius, float opacity) {
        GlStateManager.resetColor();
        GL20.glUseProgram(textureShader.getProgram());

        GL20.glUniform2f(GL20.glGetUniformLocation(textureShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(textureShader.getProgram(), "radius"), radius);

        GL20.glUniform1f(GL20.glGetUniformLocation(textureShader.getProgram(), "alpha"), opacity);
        GL20.glUniform1i(GL20.glGetUniformLocation(textureShader.getProgram(), "texture"), 0);

        rect(x, y, width, height);

        GL20.glUseProgram(0);
        GlStateManager.disableBlend();
    }

    public static void round(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram(rectShader.getProgram());

        GL20.glUniform2f(GL20.glGetUniformLocation(rectShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(rectShader.getProgram(), "radius"), radius);

        GL20.glUniform4f(GL20.glGetUniformLocation(rectShader.getProgram(), "color"), color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        rect(x, y, width, height);

        GL20.glUseProgram(0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void outline(float x, float y, float width, float height, float radius, float thickness, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram(outlineShader.getProgram());

        GL20.glUniform2f(GL20.glGetUniformLocation(outlineShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(outlineShader.getProgram(), "radius"), radius);
        GL20.glUniform1f(GL20.glGetUniformLocation(outlineShader.getProgram(), "thickness"), thickness);

        GL20.glUniform4f(GL20.glGetUniformLocation(outlineShader.getProgram(), "color"), color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        rect(x, y, width, height);

        GL20.glUseProgram(0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void outline(float x, float y, float width, float height, float radius, float thickness, float opacity, Color[] colours) {
        outline(x, y, width, height, radius, thickness, opacity, colours[0], colours[1], colours[2], colours[3]);
    }

    public static void outline(float x, float y, float width, float height, float radius, float thickness, float opacity, Color c1, Color c2, Color c3, Color c4) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram(outlineGradientShader.getProgram());

        GL20.glUniform2f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "radius"), radius - 1.5f);
        GL20.glUniform1f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "thickness"), thickness);

        GL20.glUniform1f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "alpha"), opacity);
        GL20.glUniform4f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "color1"), c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "color2"), c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "color3"), c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(outlineGradientShader.getProgram(), "color4"), c4.getRed() / 255f, c4.getGreen() / 255f, c4.getBlue() / 255f, 1.0f);

        rect(x, y, width, height);

        GL20.glUseProgram(0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void round(HUDModule parent, float radius, Color color) {
        round(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, color);
    }

    public static void texture(HUDModule parent, float radius, float opacity) {
        texture(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, opacity);
    }

    public static void gradient(HUDModule parent, float radius, float opacity, Color c1, Color c2, Color c3, Color c4) {
        gradient(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight(), radius, opacity, c1, c2, c3, c4);
    }

    public static void shadow(float x, float y, float width, float height, float radius, float softness, Color color) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int sF = sr.getScaleFactor();

        x *= 2;
        y *= 2;
        width *= 2;
        height *= 2;
        radius *= 2;

        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        glDisable(GL_ALPHA_TEST);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GL20.glUseProgram(shadowShader.getProgram());

        GL20.glUniform2f(GL20.glGetUniformLocation(shadowShader.getProgram(), "location"), x, (float) sr.getScaledHeight_double() * sF - height - y);
        GL20.glUniform2f(GL20.glGetUniformLocation(shadowShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowShader.getProgram(), "radius"), radius);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowShader.getProgram(), "shadowSoftness"), softness);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowShader.getProgram(), "edgeSoftness"), 1f);
        GL20.glUniform4f(GL20.glGetUniformLocation(shadowShader.getProgram(), "color"), color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        float g = width / 5.05f;
        float h = height / 5.05f;

        shadowShader.bind(0f, 0f, (float) sr.getScaledWidth_double() * sF, (float) sr.getScaledHeight_double() * sF);

        GL20.glUseProgram(0);
        glEnable(GL_ALPHA_TEST);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void shadowGradient(float x, float y, float width, float height, float radius, float softness, float opacity, Color c1, Color c2, Color c3, Color c4, boolean inner) {
        GlStateManager.resetColor();
        glDisable(GL_ALPHA_TEST);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GL20.glUseProgram(shadowGradientShader.getProgram());

        float g = width / 5.05f;
        float h = height / 5.05f;

        GL20.glUniform2f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "size"), width, height);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "radius"), radius);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "shadowSoftness"), softness);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "edgeSoftness"), 1f);
        GL20.glUniform1f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "alpha"), opacity);
        GL20.glUniform4f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "color1"), c1.getRed() / 255f, c1.getGreen() / 255f, c1.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "color2"), c2.getRed() / 255f, c2.getGreen() / 255f, c2.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "color3"), c3.getRed() / 255f, c3.getGreen() / 255f, c3.getBlue() / 255f, 1.0f);
        GL20.glUniform4f(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "color4"), c4.getRed() / 255f, c4.getGreen() / 255f, c4.getBlue() / 255f, 1.0f);
        GL20.glUniform1i(GL20.glGetUniformLocation(shadowGradientShader.getProgram(), "inner"), inner ? 1 : 0);

        if (inner) {
            rect(x, y, width, height);
        } else {
            glBegin(GL_QUADS);
            glTexCoord2f(-0.2f, -0.2f);
            glVertex2f(x - g, y - h);
            glTexCoord2f(-0.2f, 1.2f);
            glVertex2f(x - g, y + height + h);
            glTexCoord2f(1.2f, 1.2f);
            glVertex2f(x + width + g, y + height + h);
            glTexCoord2f(1.2f, -0.2f);
            glVertex2f(x + width + g, y - h);
            glEnd();
        }

        GL20.glUseProgram(0);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA_TEST);
        glShadeModel(GL_FLAT);
        GlStateManager.resetColor();
    }

    public static void rect(float x, float y, float width, float height) {
        glBegin(GL_QUADS);

        glTexCoord2f(0f, 0f);
        glVertex2f(x, y);
        glTexCoord2f(0f, 1f);
        glVertex2f(x, y + height);
        glTexCoord2f(1f, 1f);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1f, 0f);
        glVertex2f(x + width, y);

        glEnd();
    }




    public static void test(int texture, Color color) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int sF = sr.getScaleFactor();

        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL20.glUseProgram(testShader.getProgram());

        GL20.glUniform1i(GL20.glGetUniformLocation(testShader.getProgram(), "originalTexture"), texture);
        GL20.glUniform1f(GL20.glGetUniformLocation(testShader.getProgram(), "width"), (float) (sr.getScaledWidth_double()*sF));
        GL20.glUniform3f(GL20.glGetUniformLocation(testShader.getProgram(), "glowColor"), color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);

        shadowShader.bind(0f, 0f, (float) sr.getScaledWidth_double() * sF, (float) sr.getScaledHeight_double() * sF);

        GL20.glUseProgram(0);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }
}
