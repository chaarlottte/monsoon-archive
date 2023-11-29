package wtf.monsoon.impl.ui.particle;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import wtf.monsoon.api.util.render.RenderUtil;

import java.awt.*;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class Particle {

    @Getter
    private float x;

    @Getter
    private float y;

    @Getter
    private float yaw;

    private final ParticleSystem system;

    public Particle(ParticleSystem system) {
        this.system = system;

        Random random = new Random();

        x = random.nextFloat() * Minecraft.getMinecraft().displayWidth / 2f;
        y = random.nextFloat() * Minecraft.getMinecraft().displayHeight / 2f;
        yaw = random.nextFloat() * 360;
    }

    public void render() {
        // i need to fix this....

        //        mc.player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * speed;
        //        mc.player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * speed;
        //        mc.player.motionY += -(Math.sin(Math.toRadians(pitch))) * speed;

        /* if (x <= 0 || x >= Minecraft.getMinecraft().displayWidth / 2f || y <= 0 || y >= Minecraft.getMinecraft().displayHeight / 2f) {
            yaw = 270;
        }

        x -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(yaw)) * 2;
        y += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(yaw)) * 2;

        x = MathHelper.clamp_float(x, 0, Minecraft.getMinecraft().displayWidth / 2f);
        y = MathHelper.clamp_float(y, 0, Minecraft.getMinecraft().displayHeight / 2f); */

        float nextX = (float) (x + Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(yaw)) * 1);
        float nextY = (float) (y + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(yaw)) * 1);

        RenderUtil.drawRect(nextX - 2, nextY - 2, 4, 4, Color.RED.getRGB());

        if (nextX <= 0 || nextX >= Minecraft.getMinecraft().displayWidth / 2f || nextY <= 0 || nextY >= 200) {
            yaw += 45;

            if (nextY >= 200) {
                yaw = 0;
            }

            nextX = (float) (x + Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(yaw)) * 1);
            nextY = (float) (y + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(yaw)) * 1);
        }

        if (yaw > 360) {
            yaw -= 360;
        }

        x = nextX;
        y = nextY;

        Particle nearest = system.getNearest(this);

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glDisable(GL_ALPHA_TEST);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glLineWidth(0.5f);
        glColor4f(1, 1, 1, 1f);

        glBegin(GL_LINE_STRIP);

        glVertex2f(x, y);
        glVertex2f(nearest.x, nearest.y);

        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        //RenderUtil.drawRect(x - 0.5f, y - 0.5f, 1, 1, 0xffffffff);
    }

}
