package wtf.monsoon.impl.module.hud;

import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Speedometer extends HUDModule {

    public static Setting<Float> delay = new Setting<>("Delay", 50f)
            .minimum(0f)
            .maximum(100f)
            .incrementation(1f)
            .describedBy("The delay between adding points to the graph");

    public Speedometer() {
        super("Speedometer", "Displays your BPS on a graph.", 4, 88);
    }

    private final ArrayList<Float> speeds = new ArrayList<>();

    private final Timer updateTimer = new Timer();

    @Override
    public void render() {
        if (speeds.size() < getWidth() - 6) for (int i = 0; i < getWidth() - 6; i++) speeds.add((float) 0);

        if (speeds.size() > getWidth() - 6) speeds.remove(0);

        float speed = (float) mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ) * (Minecraft.getMinecraft().getTimer().ticksPerSecond * Minecraft.getMinecraft().getTimer().timerSpeed);
        String bps = new DecimalFormat("#.##").format(speed);

        if (updateTimer.hasTimeElapsed(delay.getValue(), true)) {
            speeds.add(speed);
        }

        /*RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 10f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
        RoundedUtils.outline(getX(), getY(), getWidth(), getHeight(), 10f, 2f, 2f,
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[0] : ColorUtil.fadeBetween(10, 270, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[1] : ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0] : ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1] : ColorUtil.fadeBetween(10, 90, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1])
        );*/
        RenderUtil.getDefaultHudRenderer(this);

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.round(getX() + 3, getY() + 4, getWidth() - 6, getHeight() - 8, 10, Color.WHITE);
        StencilUtil.bindReadStencilBuffer(1);

        glPushMatrix();
        glShadeModel(GL_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glLineWidth(1.5f);
        glBegin(GL_LINE_LOOP);
        float i = 0;
        glVertex2f(getX() - 6, getY() + getHeight() + 6);

        for (float speedPoint : speeds) {
            ColorUtil.glColor(ColorUtil.fadeBetween(10, (int) (i * 2) * 4, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]).getRGB());

            float g = speedPoint > 35 ? 2 : 1;
            glVertex2f(getX() + 4 + i * 2, getY() + getHeight() - 7 - speedPoint / g);
            i += 0.5f;
        }

        glVertex2f(getX() + getWidth() + 6, getY() + getHeight() + 6);

        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_FLAT);
        glLineWidth(2.0f);
        glPopMatrix();

        StencilUtil.uninitStencilBuffer();

        Wrapper.getFont().drawString("Speed:", getX() + 6, getY() + 4, Color.WHITE, false);
        Wrapper.getFont().drawString(bps + " b/s", getX() + getWidth() - 6 - Wrapper.getFont().getStringWidth(bps + " b/s"), getY() + 4, Color.WHITE, false);
    }

    @Override
    public void blur() {
        RoundedUtils.glRound(getX(), getY(), getWidth(), getHeight(), 10, Wrapper.getPallet().getBackground().getRGB());
    }

    @Override
    public float getWidth() {
        return 100;
    }

    @Override
    public float getHeight() {
        return 50;
    }
}
