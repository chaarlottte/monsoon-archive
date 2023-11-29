package wtf.monsoon.api.util.render;

import me.surge.animation.Animation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.module.hud.HUD;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    static Shader rectShader = new Shader(new ResourceLocation("monsoon/shader/rect.frag"));
    static Shader gradientShader = new Shader(new ResourceLocation("monsoon/shader/gradientRect.frag"));

    static Minecraft mc = Minecraft.getMinecraft();

    public static void renderItem(ItemStack stack, float x, float y, float size) {
        RenderItem ri = mc.getRenderItem();
        ri.renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
    }

    public static void arrow(float centerX, float centerY, float sharpness, float size, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos(centerX, centerY - size - sharpness, 0.0D).end();
        worldrenderer.pos(centerX + size, centerY + size, 0.0D).end();
        worldrenderer.pos(centerX, centerY + size, 0.0D).end();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void rect(float x, float y, float width, float height, Color color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_TEXTURE_2D);
        ColorUtil.glColor(color.getRGB());

        glBegin(GL_QUADS);

        glVertex2f(x, y);
        glVertex2f(x, y + height);
        glVertex2f(x + width, y + height);
        glVertex2f(x + width, y);

        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    // by surge (insane)
    public static void renderTexture(float x, float y, float width, float height) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1f, 1f, 1f, 1f);

        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        glDisable(GL_BLEND);
    }

    public static void hollowRect(float x, float y, float w, float h, float width, Color color) {
        rect(x, w, y, width, color);
        rect(x, w, h, width, color);

        rect(x, h, y, width, color);
        rect(w, h, y, width, color);
    }

    public static void gradient(float x, float y, float width, float height, Color color1, Color color2, Color color3, Color color4, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        rectShader.init();
        GL20.glUniform4f(GL20.glGetUniformLocation(rectShader.getProgram(), "color1"), color1.getRed() / 255f, color1.getGreen() / 255f, color1.getBlue() / 255f, alpha);
        GL20.glUniform4f(GL20.glGetUniformLocation(rectShader.getProgram(), "color2"), color2.getRed() / 255f, color2.getGreen() / 255f, color2.getBlue() / 255f, alpha);
        GL20.glUniform4f(GL20.glGetUniformLocation(rectShader.getProgram(), "color3"), color3.getRed() / 255f, color3.getGreen() / 255f, color3.getBlue() / 255f, alpha);
        GL20.glUniform4f(GL20.glGetUniformLocation(rectShader.getProgram(), "color4"), color4.getRed() / 255f, color4.getGreen() / 255f, color4.getBlue() / 255f, alpha);
        rectShader.bind(x, y, height, width);
        rectShader.finish();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }

    public static void verticalGradient(float x, float y, float width, float height, Color top, Color bottom) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);

        glBegin(GL_QUADS);

        ColorUtil.glColor(top.getRGB());

        glVertex2f(x, y);

        ColorUtil.glColor(bottom.getRGB());

        glVertex2f(x, y + height);
        glVertex2f(x + width, y + height);

        ColorUtil.glColor(top.getRGB());

        glVertex2f(x + width, y);

        glEnd();

        glShadeModel(GL_FLAT);
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void drawCircle(Entity entity, float partialTicks, double rad, Color color, float linewidth, double point) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        startSmooth();
        glLineWidth(linewidth);
        glBegin(GL_LINE_STRIP);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX,
                y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY,
                z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
        int r = (color.getRGB() >> 16) & 0xFF,
                g = (color.getRGB() >> 8) & 0xFF,
                b = color.getRGB() & 0xFF,
                a = (color.getRGB() >> 24) & 0xFF;
        double pix2 = Math.PI * point;
        for (int i = 0; i <= 90; ++i) {
            glColor4f((float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F, (float) a / 255.0F);
            glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
        }
        glEnd();
        endSmooth();
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void drawCircleWithY(Entity entity, float partialTicks, double rad, Color color, float linewidth, double point, double yPos) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        startSmooth();
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(linewidth);
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY + yPos;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        final float r = ((float) 1 / 255) * color.getRed();
        final float g = ((float) 1 / 255) * color.getGreen();
        final float b = ((float) 1 / 255) * color.getBlue();

        final double pix2 = Math.PI * point;

        for (int i = 0; i <= 90; ++i) {
            glColor3f(r, g, b);
            glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        endSmooth();
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public static void startSmooth() {
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
    }

    public static void endSmooth() {
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
    }

    // keep this in here please (for my sanity :>)
    public static void drawRect(double x, double y, double width, double height, int color) {
        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);

        worldrenderer.begin(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.pos(x, y + height, 0.0D).end();
        worldrenderer.pos(x + width, y + height, 0.0D).end();
        worldrenderer.pos(x + width, y, 0.0D).end();
        worldrenderer.pos(x, y, 0.0D).end();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(float x, float y, float width, float height, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x + width, y, 0).func_181666_a(f1, f2, f3, f).end();
        worldrenderer.pos(x, y, 0).func_181666_a(f1, f2, f3, f).end();
        worldrenderer.pos(x, y + height, 0).func_181666_a(f5, f6, f7, f4).end();
        worldrenderer.pos(x + width, y + height, 0).func_181666_a(f5, f6, f7, f4).end();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Starts scissoring a rect
     *
     * @param x      X coord
     * @param y      Y coord
     * @param width  Width of scissor
     * @param height Height of scissor
     */
    public static void pushScissor(double x, double y, double width, double height) {
        width = MathHelper.clamp_double(width, 0, width);
        height = MathHelper.clamp_double(height, 0, height);

        glPushAttrib(GL_SCISSOR_BIT);
        {
            scissorRect(x, y, width, height);
            glEnable(GL_SCISSOR_TEST);
        }
    }

    /**
     * Scissors a rect
     *
     * @param x      X coord
     * @param y      Y coord
     * @param width  Width of scissor
     * @param height Height of scissor
     */
    public static void scissorRect(double x, double y, double width, double height) {
        ScaledResolution sr = new ScaledResolution(Wrapper.getMinecraft());
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    /**
     * Disables scissor
     */
    public static void popScissor() {
        glDisable(GL_SCISSOR_TEST);
        glPopAttrib();
    }

    public static void scale(float x, float y, float[] scale, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale[0], scale[1], 1);
        GlStateManager.translate(-x, -y, 0);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scaleXY(float x, float y, Animation anim, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(anim.getAnimationFactor(), anim.getAnimationFactor(), 1);
        GlStateManager.translate(-x, -y, 0);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scaleX(float x, float y, Animation anim, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(anim.getAnimationFactor(), 1, 1);
        GlStateManager.translate(-x, -y, 0);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scaleY(float x, float y, Animation anim, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(1, anim.getAnimationFactor(), 1);
        GlStateManager.translate(-x, -y, 0);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void scale(float x, float y, float[] scale) {
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale[0], scale[1], 1);
        GlStateManager.translate(-x, -y, 0);
    }

    public static void rotate(float x, float y, double rotate, Runnable block) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GL11.glRotated(rotate, 0, 0, -1);
        GlStateManager.translate(-x, -y, 0);
        block.run();
        GlStateManager.popMatrix();
    }

    public static void getDefaultHudRenderer(HUDModule module) {
        getDefaultHudRenderer(module.getX(),module.getY(),module.getWidth(),module.getHeight());
    }

    public static void getDefaultHudRenderer(float x, float y, float w, float h) {
        HUD hudInstance = Wrapper.getModule(HUD.class);

        boolean outline = hudInstance.hudModuleOutline.getValue();
        boolean shadow = hudInstance.hudModuleShadow.getValue();
        boolean background = hudInstance.hudModuleBackground.getValue();

        float radius = 10;

        if(shadow) {
            RoundedUtils.shadowGradient(x,y,w,h, radius, 10, 2f, ColorUtil.getAccent()[0], ColorUtil.getAccent()[1], ColorUtil.getAccent()[2], ColorUtil.getAccent()[3], false);
        }
        if(background) {
            RoundedUtils.round(x,y,w,h, radius, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
        }
        if(outline) {
            RoundedUtils.outline(x,y,w,h, radius, 2, 2f, ColorUtil.getAccent()[0], ColorUtil.getAccent()[1], ColorUtil.getAccent()[2], ColorUtil.getAccent()[3]);
        }
    }
}
