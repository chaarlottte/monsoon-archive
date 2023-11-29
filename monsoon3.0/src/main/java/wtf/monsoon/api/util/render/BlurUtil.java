package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.event.EventBlur;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class BlurUtil {
    private static Shader blur_shader = null;
    public static Framebuffer blur_framebuffer = new Framebuffer(1, 1, false), blur_shader_2 = new Framebuffer(1, 1, false);
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static int lastScale = -1;
    private static int lastScaleWidth = -1;
    private static int lastScaleHeight = -1;
    public static Framebuffer framebuffer = null;
    private static ShaderGroup blurShader = null;

    public static void blur(float radius, float direction) {
        if (mc.displayWidth != blur_framebuffer.framebufferWidth || mc.displayHeight != blur_framebuffer.framebufferHeight) {
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }

        EventBlur eventBlur = new EventBlur();
        ScaledResolution sr = new ScaledResolution(mc);
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        preBlur();
        Wrapper.getEventBus().post(eventBlur);
        postBlur(radius, direction);

        StencilUtil.bindReadStencilBuffer(1);
    }

    static void start_shader(float radius) {
        if (blur_shader == null) {
            blur_shader = new Shader(new ResourceLocation("monsoon/shader/blur.frag"));

            blur_shader.setupUniform("texture");
            blur_shader.setupUniform("texelSize");
            blur_shader.setupUniform("direction");
            blur_shader.setupUniform("radius");
        }

        glUseProgram(blur_shader.getProgram());
        glUniform1i(blur_shader.getUniform("texture"), 0);
        glUniform2f(blur_shader.getUniform("texelSize"), 1.0F / mc.displayWidth, 1.0F / mc.displayHeight);
        glUniform1f(blur_shader.getUniform("radius"), MathHelper.ceiling_float_int((2 * radius)));
    }

    public static void preBlur() {
        if (mc.displayWidth != blur_framebuffer.framebufferWidth || mc.displayHeight != blur_framebuffer.framebufferHeight) {
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }

        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
    }

    public static void postBlur(float radius, float direction) {
        StencilUtil.bindReadStencilBuffer(1);
        ScaledResolution sr = new ScaledResolution(mc);

        start_shader(radius);
        blur_framebuffer.framebufferClear();
        blur_framebuffer.bindFramebuffer(false);
        glUniform2f(blur_shader.getUniform("direction"), direction, 0);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        RoundedUtils.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        blur_framebuffer.unbindFramebuffer();

        start_shader(radius);
        mc.getFramebuffer().bindFramebuffer(false);
        glUniform2f(blur_shader.getUniform("direction"), 0, direction);
        glBindTexture(GL_TEXTURE_2D, blur_framebuffer.framebufferTexture);
        RoundedUtils.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight());


        glUseProgram(0);
        StencilUtil.uninitStencilBuffer();
    }

    public static void preBlurNoStencil() {
        if (mc.displayWidth != blur_framebuffer.framebufferWidth || mc.displayHeight != blur_framebuffer.framebufferHeight) {
            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);

            blur_framebuffer.deleteFramebuffer();
            blur_framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
    }

    public static void postBlurNoStencil(float radius, float direction) {
        ScaledResolution sr = new ScaledResolution(mc);

        start_shader(radius);
        blur_framebuffer.framebufferClear();
        blur_framebuffer.bindFramebuffer(false);
        glUniform2f(blur_shader.getUniform("direction"), direction, 0);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        RoundedUtils.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        blur_framebuffer.unbindFramebuffer();

        start_shader(radius);
        mc.getFramebuffer().bindFramebuffer(false);
        glUniform2f(blur_shader.getUniform("direction"), 0, direction);
        glBindTexture(GL_TEXTURE_2D, blur_framebuffer.framebufferTexture);
        RoundedUtils.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight());

        glUseProgram(0);
    }

    public static void alternateBlur(float x, float y, float width, float height, int intensity) {
        ScaledResolution sr = new ScaledResolution(mc);
        int currentScale = sr.getScaleFactor();
        checkScale(currentScale, sr.getScaledWidth(), sr.getScaledHeight());

        if (OpenGlHelper.isFramebufferEnabled()) {
            RenderUtil.pushScissor(x, y + 1, width, height);

            blurShader.listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
            blurShader.listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
            blurShader.listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(2f);
            blurShader.listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(1f);

            framebuffer.bindFramebuffer(true);
            blurShader.loadShaderGroup(mc.getTimer().renderPartialTicks);

            mc.getFramebuffer().bindFramebuffer(true);

            RenderUtil.popScissor();

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ZERO, GL_ONE);

            framebuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
            GlStateManager.disableBlend();
            glScalef(currentScale, currentScale, 0f);
        }
    }

    private static void checkScale(int scaleFactor, int widthFactor, int heightFactor) {
        if (lastScale != scaleFactor || lastScaleWidth != widthFactor || lastScaleHeight != heightFactor || framebuffer == null || blurShader == null) {
            try {
                blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("shaders/post/blur.json"));
                blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
                framebuffer = blurShader.mainFramebuffer;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        lastScale = scaleFactor;
        lastScaleWidth = widthFactor;
        lastScaleHeight = heightFactor;
    }

}
