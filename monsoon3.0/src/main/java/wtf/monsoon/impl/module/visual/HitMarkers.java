package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.SoundUtil;
import wtf.monsoon.impl.event.EventPlayerHurtSound;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.module.combat.Aura;
import static org.lwjgl.opengl.GL11.*;

public class HitMarkers extends Module {

    private double currentAlpha = 255;

    public HitMarkers() {
        super("Hit Markers", "Show hit markers when you attack an entity.", Category.VISUAL);
    }

    @EventLink
    private final Listener<EventRender2D> onRender2D = e -> {
        double dif = Math.abs(currentAlpha);
        int fps = mc.getDebugFPS();
        if (dif > 0.0D) {
            double animationSpeed = MathUtils.roundToPlace(Math.min(10.0D, Math.max(0.005D, 144.0D / fps * 4)), 3);
            if (dif < animationSpeed)
                animationSpeed = dif;
            if (currentAlpha < 0)
                currentAlpha = currentAlpha + animationSpeed;
            if (currentAlpha > 0)
                currentAlpha = currentAlpha - animationSpeed;
        }
        for (int i = 0; i < 4; i++) {
            drawHitMarker(e.getSr());
        }
    };

    @EventLink
    private final Listener<EventPlayerHurtSound> eventPlayerHurtSoundListener = e -> {
        //if (Wrapper.getModule(Aura.class).getTarget() != null && e.getEntity().equals(Wrapper.getModule(Aura.class).getTarget())) {
        if (mc.thePlayer.getDistanceToEntity(e.getEntity()) <= 6 && mc.thePlayer.isSwingInProgress && e.getEntity() != mc.thePlayer) {
            // SoundUtil.playSound("hitmarker.wav");
            currentAlpha = 255;
            mc.thePlayer.playSound(mc.thePlayer.getHurtSound(), e.getEntity().getSoundVolume() / 1.5f, 1f);
            mc.thePlayer.playSound(mc.thePlayer.getHurtSound(), e.getEntity().getSoundVolume(), 2f);
            e.setCancelled(true);
        }
    };

    private void drawHitMarker(ScaledResolution scaledResolution) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2.0F);
        glColor4f(1f, 1f, 1f, (float) currentAlpha / 255.0F);
        glBegin(GL11.GL_LINES);

        glVertex2d(scaledResolution.getScaledWidth() / 2.0 - 6, scaledResolution.getScaledHeight() / 2.0 - 7);
        glVertex2d(scaledResolution.getScaledWidth() / 2.0 - 3, scaledResolution.getScaledHeight() / 2.0 - 4);

        glVertex2d(scaledResolution.getScaledWidth() / 2.0 + 7, scaledResolution.getScaledHeight() / 2.0 + 7);
        glVertex2d(scaledResolution.getScaledWidth() / 2.0 + 4, scaledResolution.getScaledHeight() / 2.0 + 4);

        glVertex2d(scaledResolution.getScaledWidth() / 2.0 - 6, scaledResolution.getScaledHeight() / 2.0 + 7);
        glVertex2d(scaledResolution.getScaledWidth() / 2.0 - 3, scaledResolution.getScaledHeight() / 2.0 + 4);

        glVertex2d(scaledResolution.getScaledWidth() / 2.0 + 7, scaledResolution.getScaledHeight() / 2.0 - 7);
        glVertex2d(scaledResolution.getScaledWidth() / 2.0 + 4, scaledResolution.getScaledHeight() / 2.0 - 4);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }


}
