package wtf.monsoon.client.ui.test;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.*;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.client.util.ui.Shader;

import java.awt.*;
import java.nio.FloatBuffer;

public class TestScreen extends GuiScreen {
    private Framebuffer fb, blurfb;
    private Shader blurShader;
    private NVGLUFramebuffer nvgBuffer;
    @Override
    public void initGui() {
        nvgBuffer = NanoVGGL2.nvgluCreateFramebuffer(Wrapper.getMonsoon().nvg.vg, Display.getWidth(), Display.getHeight(), OpenGlHelper.GL_RENDERBUFFER);

        fb = new Framebuffer(Display.getWidth(),Display.getHeight(), true);
        blurfb = new Framebuffer(Display.getWidth(),Display.getHeight(), true);
        blurShader = new Shader(new ResourceLocation( "monsoon/shader/gaussian.frag"));
        blurShader.setupUniform("resolution");
        blurShader.setupUniform("texture");
        blurShader.setupUniform("mask");
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        blurfb.framebufferClear();

        NanoVGGL2.nvgluBindFramebuffer(Wrapper.getMonsoon().nvg.vg, nvgBuffer);
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
        Wrapper.getMonsoon().nvg.beginFrame();
        Wrapper.getMonsoon().nvg.round(Display.getWidth()/2f-300,Display.getHeight()/2f-200,600,400,10, Color.WHITE);
        Wrapper.getMonsoon().nvg.text("Hello!", 10, 50, "black", 120, Color.WHITE);
        Wrapper.getMonsoon().nvg.endFrame();
        NanoVGGL2.nvgluBindFramebuffer(Wrapper.getMonsoon().nvg.vg, null);

        blurfb.bindFramebuffer(false);
        blurShader.init();

        uniforms(0);

        glActiveTexture(GL_TEXTURE16);
        glBindTexture(GL11.GL_TEXTURE_2D, nvgBuffer.texture());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL11.GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);

        blurShader.bind(0,0,blurfb.framebufferWidth, blurfb.framebufferHeight);
        blurShader.finish();

        mc.getFramebuffer().bindFramebuffer(false);

        Wrapper.getMonsoon().nvg.beginFrame();
        Wrapper.getMonsoon().nvg.glTexture(0,0,Display.getWidth(), Display.getHeight(), 0, blurfb.framebufferTexture);
//        Wrapper.getMonsoon().nvg.round(Display.getWidth()/2f-300,Display.getHeight()/2f-200,600,400,10,new Color(26, 26, 26, 230));
//        Wrapper.getMonsoon().nvg.round(Display.getWidth()/2f-300,Display.getHeight()/2f-200,600,20,10,new Color(255, 188, 0, 230));
        Wrapper.getMonsoon().nvg.endFrame();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void uniforms(int radius) {
        GL20.glUniform2f(blurShader.getUniform("resolution"), Display.getWidth(), Display.getHeight());
        GL20.glUniform1i(blurShader.getUniform("texture"), 0);
        GL20.glUniform1i(blurShader.getUniform("mask"), 16);
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
