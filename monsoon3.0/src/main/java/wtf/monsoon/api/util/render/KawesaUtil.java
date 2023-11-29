package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.event.EventBlur;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class KawesaUtil {
    public static Framebuffer framebuffer = new Framebuffer(1,1,false);
    private static final Minecraft mc = Minecraft.getMinecraft();
    static Shader blurShader = null;

    public static void blur(int passes) {
        if(blurShader == null) {
            blurShader = new Shader(new ResourceLocation("monsoon/shader/kawesa.frag"));
            blurShader.setupUniform("iResolution");
            blurShader.setupUniform("iChannelResolution");
            blurShader.setupUniform("tex");
            blurShader.setupUniform("blurStrength");
        }

        EventBlur eventBlur = new EventBlur();

        preBlur();
        Wrapper.getEventBus().post(eventBlur);
        postBlur(passes);
    }

    static void uniforms(float strength) {
        GL20.glUniform1i(blurShader.getUniform("tex"), 0);
        glActiveTexture(GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        GL20.glUniform2f(blurShader.getUniform("iResolution"), mc.displayWidth, mc.displayHeight);
        GL20.glUniform2f(blurShader.getUniform("iChannelResolution"), mc.displayWidth, mc.displayHeight);
        GL20.glUniform1f(blurShader.getUniform("blurStrength"), strength);
    }

    public static void preBlur() {
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
    }

    public static void postBlur(int passes) {
        ScaledResolution sr = new ScaledResolution(mc);
        StencilUtil.bindReadStencilBuffer(1);

        blurShader.init();
        for (float i = 2.5f; i < passes; i+=0.5f) {
            uniforms(i);
            blurShader.bind(0f, 0f, (float) sr.getScaledWidth_double() * sr.getScaleFactor(), (float) sr.getScaledHeight_double() * sr.getScaleFactor());
        }
        blurShader.finish();

        StencilUtil.uninitStencilBuffer();
    }
}
