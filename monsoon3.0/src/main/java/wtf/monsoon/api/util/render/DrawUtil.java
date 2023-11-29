package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class DrawUtil extends GuiScreen {

    public static void drawHollowRect(int x, int y, int w, int h, int color) {
        Gui.drawHorizontalLine(x, x + w, y, color);
        Gui.drawHorizontalLine(x, x + w, y + h, color);

        Gui.drawVerticalLine(x, y + h, y, color);
        Gui.drawVerticalLine(x + w, y + h, y, color);
    }

    public static void drawHollowRect(float x, float y, float w, float h, int color) {
        Gui.drawHorizontalLine(x, w, y, color);
        Gui.drawHorizontalLine(x, w, h, color);

        Gui.drawVerticalLine(x, h, y, color);
        Gui.drawVerticalLine(w, h, y, color);
    }

    public static void drawHollowRectDefineWidth(float x, float y, float w, float h, float width, int color) {
        Gui.drawHorizontalLineDefineWidth(x, w, y, width, color);
        Gui.drawHorizontalLineDefineWidth(x, w, h, width, color);

        Gui.drawVerticalLineDefineWidth(x, h, y, width, color);
        Gui.drawVerticalLineDefineWidth(w, h, y, width, color);
    }

    public static void drawHollowRect(float x, float y, float w, float h, double size, int color) {
        Gui.drawHorizontalLine(x, w, y, color, size);
        Gui.drawHorizontalLine(x, w, h, color, size);

        Gui.drawVerticalLine(x, h, y, color, size);
        Gui.drawVerticalLine(w, h, y, color, size);
    }

    public static void drawHollowRect(double x, double y, double w, double h, int color) {
        Gui.drawHorizontalLine(x, w, y, color);
        Gui.drawHorizontalLine(x, w, h, color);

        Gui.drawVerticalLine(x, h, y, color);
        Gui.drawVerticalLine(w, h, y, color);
    }

    public static void draw2DImage(ResourceLocation image, int x, int y, int width, int height, Color c) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha());
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void draw2DImage(ResourceLocation image, float x, float y, float width, float height, Color c) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0.0F, 0.0F, (int) width, (int) height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void draw2DImageNoBind(float x, float y, float width, float height, Color c) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glColor4f(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha());
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawFace(double x, double y, int width, int height, AbstractClientPlayer target) {
        try {
            ResourceLocation skin = target.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 0, 0, width, height, width, height, width, height);
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawFace(EntityLivingBase target, int x, int y, int width, int height, boolean hurtTime) {
        try {
            if (!(target instanceof EntityPlayer)) {
                ResourceLocation skin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
                if (target instanceof EntityZombie) {
                    skin = new ResourceLocation("textures/entity/zombie/zombie.png");
                }
                if (target instanceof EntityCreeper) {
                    skin = new ResourceLocation("textures/entity/creeper/creeper.png");
                }
                if (target instanceof EntitySpider) {
                    skin = new ResourceLocation("textures/entity/spider/spider.png");
                }
                if (target instanceof EntitySkeleton) {
                    skin = new ResourceLocation("textures/entity/skeleton/skeleton.png");
                }
                Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            } else {
                EntityPlayer penis = (EntityPlayer) target;
                AbstractClientPlayer player = (AbstractClientPlayer) penis;
                ResourceLocation skin = player.getLocationSkin();
                Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            }
            glEnable(GL_BLEND);
            if (hurtTime) {
                if (target.hurtTime > 0) glColor4f(1, 1 - target.hurtTime * 0.1f, 1 - target.hurtTime * 0.1f, 1);
                else glColor4f(1, 1, 1, 1);
            } else glColor4f(1, 1, 1, 1);
            Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, height, 64, 64);
            glDisable(GL_BLEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
