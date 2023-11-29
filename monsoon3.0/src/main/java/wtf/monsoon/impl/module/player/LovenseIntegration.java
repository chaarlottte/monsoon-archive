package wtf.monsoon.impl.module.player;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class LovenseIntegration extends HUDModule {

    public LovenseIntegration() {
        super("Lovense Integration", "trpedyjbjrcdaopntkmnqfjrcwizs \uD83D\uDC49 \uD83D\uDC48", 4, 208);
    }

    ArrayList<Float> speeds = new ArrayList<>();

    Timer updateTimer = new Timer();

    @Override
    public void render() {
        if (speeds.size() < getWidth() - 6) for (int i = 0; i < getWidth() - 6; i++) speeds.add((float) 0);

        if (speeds.size() > getWidth() - 6) speeds.remove(0);

        float vibration = Wrapper.getSexToyManager().getVibrationIncrement();
        if (updateTimer.hasTimeElapsed(50, true))
            speeds.add(vibration);

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.round(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, 7, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.85f));
        StencilUtil.bindReadStencilBuffer(0);
        RoundedUtils.gradient(this, 10, 1f,
                ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255))
        );
        StencilUtil.uninitStencilBuffer();
        RoundedUtils.round(getX() + 1.5f, getY() + 1.5f, getWidth() - 3, getHeight() - 3, 7.5f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.glRound(getX() + 4, getY() + 4, getWidth() - 8, getHeight() - 8, 6, -1);
        StencilUtil.bindReadStencilBuffer(1);

        glPushMatrix();
        {
            glShadeModel(GL_SMOOTH);
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glLineWidth(1.5f);
            glBegin(2);
            float i = 0;
            glVertex2f(getX() - 6, getY() + getHeight() + 6);
            for (float s : speeds) {
                Color c = ColorUtil.fadeBetween(10, (int) (i * 2) * 4, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255));
                float red = c.getRed() / 255f;
                float green = c.getGreen() / 255f;
                float blue = c.getBlue() / 255f;
                float alpha = c.getAlpha() / 255f;
                glColor4f(red, green, blue, alpha);
                float g = s > 35 ? 2 : 1;
                glVertex2f(getX() + 4 + i * 2, getY() + getHeight() - 7 - s / g);
                i += 0.5f;
            }
            glVertex2f(getX() + getWidth() + 6, getY() + getHeight() + 6);
            glEnd();

            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_TEXTURE_2D);
            glShadeModel(GL_FLAT);
        }
        glLineWidth(2.0f);
        glPopMatrix();

        StencilUtil.uninitStencilBuffer();

        //Wrapper.getFont().drawString(Wrapper.getSexToyManager().getMainToy().getNickname() + " (" + Wrapper.getSexToyManager().getMainToy().getName() + ")", getX() + 6, getY() + 4, Color.WHITE, false);
        Wrapper.getFont().drawString("Vibration Strength: " + vibration, getX() + getWidth() - 6 - Wrapper.getFont().getStringWidth("Vibration Strength: " + vibration), getY() + 4, Color.WHITE, false);
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
