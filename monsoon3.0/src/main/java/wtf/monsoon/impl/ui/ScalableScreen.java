package wtf.monsoon.impl.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;

import java.io.IOException;

public abstract class ScalableScreen extends GuiScreen {


    protected ScaledResolution resolution;
    protected float scaledWidth;
    protected float scaledHeight;

    public abstract void init();

    public abstract void render(float mouseX, float mouseY);

    public abstract void click(float mouseX, float mouseY, int mouseButton);


    @Override
    public final void initGui() {
        init();
        super.initGui();
    }


    @Override
    public final void setWorldAndResolution(final Minecraft mc, final int displayWidth, final int displayHeight) {
        this.mc = mc;
        this.fontRendererObj = mc.fontRendererObj;
        this.width = displayWidth;
        this.height = displayHeight;
        this.buttonList.clear();
        this.resolution = new ScaledResolution(this.mc);
        final float scaleFactor = getScaleFactor();
        this.scaledWidth = width / scaleFactor;
        this.scaledHeight = height / scaleFactor;
        this.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final float scaleFactor = getScaleFactor();
        GL11.glPushMatrix();
        GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
        render(mouseX / scaleFactor, mouseY / scaleFactor);
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int state) throws IOException {
        final float scaleFactor = getScaleFactor();
        click(mouseX / scaleFactor, mouseY / scaleFactor, state);
        super.mouseClicked(mouseX, mouseY, state);
    }

    public float getScaleFactor() {
        return 1.0f / (resolution.getScaleFactor() * .5f);
    }

    public float getScaledWidth() {
        return scaledWidth;
    }

    public float getScaledHeight() {
        return scaledHeight;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}