package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.event.EventRenderVanillaNametag;

import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.disableLighting;
import static net.minecraft.client.renderer.GlStateManager.enableLighting;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 24/08/2022
 */
public class Nametags extends Module {

    private final Setting<Boolean> distanceScale = new Setting<>("DistanceScale", true)
            .describedBy("Scale the nametag based on the distance");

    private final Setting<Float> scaleSetting = new Setting<>("Scale", 0.2f)
            .minimum(0.1f)
            .maximum(1f)
            .incrementation(0.01f)
            .describedBy("The scale of the nametag");

    private final Setting<Boolean> health = new Setting<>("Health", true)
            .describedBy("Render the player's health");

    public Nametags() {
        super("Nametags", "Renders more info on a players nametag", Category.VISUAL);
    }

    @EventLink
    private final Listener<EventRender3D> render3DListener = event -> {
        mc.theWorld.playerEntities.forEach(player -> {
            if ((player == mc.thePlayer && mc.gameSettings.thirdPersonView < 1) || player.isInvisible()) {
                return;
            }

            double[] renderValues = {
                    mc.getRenderManager().renderPosX,
                    mc.getRenderManager().renderPosY,
                    mc.getRenderManager().renderPosZ
            };

            Vec3 vec = EntityUtil.getInterpolatedPosition(player);

            double distance = mc.thePlayer.getDistance(vec.x, vec.y, vec.z);
            float scale = scaleSetting.getValue() * 5 / 50f;

            if (distanceScale.getValue()) {
                scale = (float) (Math.max(scaleSetting.getValue() * 5, scaleSetting.getValue() * distance) / 50);
            }

            glPushMatrix();
            RenderHelper.enableStandardItemLighting();
            disableLighting();
            glTranslated(vec.x - renderValues[0], vec.y + player.height + 0.1 + (player.isSneaking() ? 0.05 : 0.08) - renderValues[1], vec.z - renderValues[2]);

            glRotated(-mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
            glRotated(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 0.0, 0.0);

            glScaled(-scale, -scale, scale);

            glDisable(GL_DEPTH_TEST);

            StringBuilder builder = new StringBuilder();

            builder.append(player.getCommandSenderName());

            if (health.getValue()) {
                builder.append(" ").append(EntityUtil.getTextColourFromEntityHealth(player)).append(Math.round(EntityUtil.getTotalHealth(player)) / 2f).append(EnumChatFormatting.WHITE).append(" ");
            }

            float width = Wrapper.getFont().getStringWidth(builder.toString());

            // Center nametag
            glTranslated((-width / 2), -20.0, 0.0);
            float size = 2;
            RoundedUtils.round(-size, -size, Wrapper.getFont().getStringWidth(builder.toString()) + size * 2f, Wrapper.getFont().getHeight() + size * 2f, 3f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.5f));
            Wrapper.getFont().drawString(builder.toString(), 0, 0, Color.WHITE, false);

            glEnable(GL_ALPHA_TEST);
            enableLighting();
            RenderHelper.disableStandardItemLighting();
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
            glPopMatrix();
        });
    };

    @EventLink
    private final Listener<EventRenderVanillaNametag> eventRenderVanillaNametagListener = event -> {
        if (event.getEntity() instanceof EntityPlayer) {
            event.cancel();
        }
    };

}
