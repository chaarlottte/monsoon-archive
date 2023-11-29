package wtf.monsoon.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.shader.Shader;
import wtf.monsoon.impl.event.EventBloom;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.*;

public class BloomUtil {
    public static Framebuffer framebuffer = new Framebuffer(1,1,false);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void bloom() {
        ScaledResolution sr = new ScaledResolution(mc);

        if (mc.displayWidth != framebuffer.framebufferWidth || mc.displayHeight != framebuffer.framebufferHeight) {
            framebuffer.deleteFramebuffer();
            framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        EventBloom bloom = new EventBloom();

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        Wrapper.getEventBus().post(bloom);
        framebuffer.unbindFramebuffer();

        RoundedUtils.test(framebuffer.framebufferTexture, Color.WHITE);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        RoundedUtils.rect(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
    }
}
