package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.impl.event.EventRender3D;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 27/07/2022
 */
public class Tracers extends Module {

    public Setting<Boolean> players = new Setting<>("Players", true)
            .describedBy("Show tracers for players");

    public Setting<Boolean> mobs = new Setting<>("Mobs", false)
            .describedBy("Show tracers for mobs");

    public Setting<Boolean> passives = new Setting<>("Passives", false)
            .describedBy("Show tracers for passives");

    public Setting<Boolean> neutral = new Setting<>("Neutral", false)
            .describedBy("Show tracers for neutral entities");

    public Setting<Double> lineWidth = new Setting<>("LineWidth", 0.1)
            .minimum(0.1)
            .maximum(3.0)
            .incrementation(0.1)
            .describedBy("The width of the tracer line");

    public Setting<Float> alpha = new Setting<>("Alpha", 255f)
            .minimum(0f)
            .maximum(255f)
            .incrementation(1f)
            .describedBy("The alpha of the tracer line");

    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.VISUAL);
    }

    @EventLink
    private final Listener<EventRender3D> render3DListener = event -> {
        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (isAllowed(entity) && !entity.isInvisible()) {
                Vec3 vec = EntityUtil.getInterpolatedPosition(entity);
                double x = vec.x - mc.getRenderManager().viewerPosX;
                double y = vec.y - mc.getRenderManager().viewerPosY;
                double z = vec.z - mc.getRenderManager().viewerPosZ;

                Vec3 eyes = (new Vec3(0.0D, 0.0D, 1.0D)).rotatePitch(-((float) Math.toRadians(mc.thePlayer.rotationPitch))).rotateYaw(-((float) Math.toRadians(mc.thePlayer.rotationYaw)));

                glDepthMask(false);
                glDisable(GL_DEPTH_TEST);
                glDisable(GL_ALPHA_TEST);
                glEnable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_LINE_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

                Color colour = getColour(entity);

                glColor4f(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, alpha.getValue().floatValue() / 255f);
                glLineWidth(lineWidth.getValue().floatValue());

                glBegin(GL_CURRENT_BIT);

                glVertex3d(eyes.x, eyes.y + mc.thePlayer.getEyeHeight(), eyes.z);
                glVertex3d(x, y, z);

                glEnd();

                glDepthMask(true);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glDisable(GL_BLEND);
                glEnable(GL_ALPHA_TEST);
                glDisable(GL_LINE_SMOOTH);
                glColor4f(1f, 1f, 1f, 1f);
            }
        });
    };

    public Color getColour(Entity entity) {
        if (entity instanceof EntityOtherPlayerMP) {
            return new Color(0, 140, 255);
        }

        if (EntityUtil.isPassive(entity)) {
            return new Color(0, 255, 0);
        }

        if (EntityUtil.isHostile(entity)) {
            return new Color(255, 0, 0);
        }

        if (EntityUtil.isNeutral(entity)) {
            return new Color(255, 255, 255);
        }

        return new Color(0, 0, 0);
    }

    public boolean isAllowed(Entity entity) {
        if (entity instanceof EntityOtherPlayerMP) {
            return players.getValue();
        }

        if (EntityUtil.isPassive(entity)) {
            return passives.getValue();
        }

        if (EntityUtil.isHostile(entity)) {
            return mobs.getValue();
        }

        if (EntityUtil.isNeutral(entity)) {
            return neutral.getValue();
        }

        return false;
    }

}
