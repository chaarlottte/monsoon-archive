package wtf.monsoon.api.ui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.NVGR;
import wtf.monsoon.impl.ui.ScalableScreen;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

/**
 * this is for nanovg component system etc
 */

public abstract class Screen extends GuiScreen {
    protected NVGR ui = Wrapper.getNVG();
    protected int dw = Display.getWidth(), dh = Display.getHeight();
    boolean vsync;

    @Override
    public void initGui() {
        init();
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Display.setVSyncEnabled(vsync);
    }

    public void init() {
        vsync = mc.gameSettings.enableVsync;
        Display.setVSyncEnabled(true);
        dw = Display.getWidth();
        dh = Display.getHeight();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dw = Display.getWidth();
        dh = Display.getHeight();

        glPushAttrib(0);
        glPushMatrix();
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glAlphaFunc(GL_GREATER, 0);
        ui.initFrame();
        render(Mouse.getX(), dh-Mouse.getY());
        ui.finishFrame();
        glDisable(GL_BLEND);
        glPopMatrix();
        glPopAttrib();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        click(Mouse.getX(), dh-Mouse.getY(), mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public abstract void render(float mx, float my);
    public abstract void click(float mx, float my, int button);
}
