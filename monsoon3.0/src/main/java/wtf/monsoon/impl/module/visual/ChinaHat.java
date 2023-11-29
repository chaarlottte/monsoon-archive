package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Vec3;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.event.EventRender3D;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 30/07/2022
 */
public class ChinaHat extends Module {

    private final Setting<Color> topColour = new Setting<>("TopColour", new Color(0, 140, 255))
            .describedBy("The color of the top of the hat");

    private final Setting<Color> bottomColour = new Setting<>("BottomColour", new Color(0, 140, 255).darker())
            .describedBy("The color of the bottom of the hat");

    private final Setting<Boolean> others = new Setting<>("Others", true)
            .describedBy("Whether to render hats other players");

    public ChinaHat() {
        super("ChinaHat", "ching chong -69420 social credit!", Category.VISUAL);
    }

    @EventLink
    public final Listener<EventRender3D> eventRender3DListener = event -> {
        mc.theWorld.playerEntities.forEach(player -> {
            if (player == mc.thePlayer && mc.gameSettings.thirdPersonView == 0 || !others.getValue() && player != mc.thePlayer) {
                return;
            }

            glPushMatrix();
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL_POINT_SMOOTH);
            glEnable(GL_BLEND);
            glShadeModel(GL_SMOOTH);
            GlStateManager.disableCull();
            glBegin(GL_TRIANGLE_STRIP);

            // Get the vector to start drawing the hat
            Vec3 vec = EntityUtil.getInterpolatedPosition(player).add(new Vec3(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY + player.getEyeHeight() + 0.41 + (player.isSneaking() ? -0.2 : 0.0), -mc.getRenderManager().viewerPosZ));

            for (double i = 0; i < Math.PI * 2; i += Math.PI * 4 / 128) {
                // Set bottom colour
                ColorUtil.glColor(bottomColour.getValue().getRGB());

                // Add bottom point
                glVertex3d(vec.x + 0.65 * Math.cos(i), vec.y - 0.25, vec.z + 0.65 * Math.sin(i));

                // Set top colour
                ColorUtil.glColor(topColour.getValue().getRGB());

                // Add top point
                glVertex3d(vec.x, vec.y, vec.z);
            }

            glEnd();
            glShadeModel(GL_FLAT);
            glDepthMask(true);
            glEnable(GL_LINE_SMOOTH);
            GlStateManager.enableCull();
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_POINT_SMOOTH);
            glEnable(GL_TEXTURE_2D);
            glPopMatrix();
        });
    };

}
