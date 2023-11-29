package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.*;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.builder.BoxRenderMode;
import wtf.monsoon.api.util.render.builder.RenderBuilder;
import wtf.monsoon.impl.event.EventRender3D;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 30/12/2022
 */
public class Trajectories extends Module {

    public static Setting<Boolean> box = new Setting<>("Box", true)
            .describedBy("Draw the box");

    public static Setting<Boolean> line = new Setting<>("Line", true)
            .describedBy("Draw the line");

    public static Setting<Float> lineWidth = new Setting<>("Width", 1.0f)
            .minimum(1.0f)
            .maximum(0.1f)
            .incrementation(2.0f)
            .describedBy("How thick the line is")
            .childOf(line);

    public static Setting<Float> maxVertexCount = new Setting<>("MaxVertexCount", 1000.0f)
            .minimum(100f)
            .maximum(3000f)
            .incrementation(1f)
            .describedBy("The maximum amount of vertices to draw")
            .childOf(line);

    public Trajectories() {
        super("Trajectories", "uhhhh yeah uhhh draws lines when ur holding a bow", Category.VISUAL);
    }

    @EventLink
    private final Listener<EventRender3D> render3DListener = event -> {
        ItemStack stack = mc.thePlayer.getHeldItem();

        if (stack == null) {
            return;
        }

        if (stack.getItem() instanceof ItemBow && mc.thePlayer.getItemInUse() != null || stack.getItem() instanceof ItemSnowball || stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemEnderPearl) {
            Vec3 position = new Vec3(
                    mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.getTimer().renderPartialTicks - Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * 0.16f,
                    mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.getTimer().renderPartialTicks + mc.thePlayer.getEyeHeight() - 0.15,
                    mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.getTimer().renderPartialTicks - Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * 0.16f
            );

            Vec3 velocity = new Vec3(
                -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * Math.cos(Math.toRadians(mc.thePlayer.rotationPitch)) * (stack.getItem() instanceof ItemBow ? 1.0f : 0.4f),
                -Math.sin(Math.toRadians(mc.thePlayer.rotationPitch)) * (stack.getItem() instanceof ItemBow ? 1.0f : 0.4f),
                Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * Math.cos(Math.toRadians(mc.thePlayer.rotationPitch)) * (stack.getItem() instanceof ItemBow ? 1.0f : 0.4f)
            );

            // Motion factor
            double motion = Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);

            // New velocity
            velocity = new Vec3(velocity.x / motion, velocity.y / motion, velocity.z / motion);

            double power = stack.getItem() instanceof ItemBow ? MathHelper.clamp_double(((72000 - mc.thePlayer.getItemInUseCount()) / 20f * ((72000 - mc.thePlayer.getItemInUseCount()) / 20f) + (72000 - mc.thePlayer.getItemInUseCount()) / 20f * 2.0f), 0f, 1f) * 3 : 1.5;

            velocity = new Vec3(velocity.x * power, velocity.y * power, velocity.z * power);

            int vertexCount = 0;

            glPushMatrix();
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_LINE_SMOOTH);

            glLineWidth(lineWidth.getValue());

            glBegin(GL_LINE_STRIP);

            for (int i = 0; i < maxVertexCount.getValue(); i++) {
                Color combined = ColorUtil.fadeBetween(3, i * 15, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);

                Accent.EnumAccents enumeration = Wrapper.getModule(Accent.class).accents.getValue();

                if (enumeration.equals(Accent.EnumAccents.ASTOLFO)) {
                    combined = ColorUtil.astolfoColorsC(i * 5, i * 20);
                } else if (enumeration.equals(Accent.EnumAccents.RAINBOW)) {
                    combined = ColorUtil.rainbow(i * 300L);
                }

                if (line.getValue()) {
                    ColorUtil.glColor(combined.getRGB());

                    glVertex3d(position.x - mc.getRenderManager().viewerPosX, position.y - mc.getRenderManager().viewerPosY, position.z - mc.getRenderManager().viewerPosZ);
                }

                position = new Vec3(position.x + velocity.x * 0.1, position.y + velocity.y * 0.1, position.z + velocity.z * 0.1);

                velocity = new Vec3(velocity.x, velocity.y - (stack.getItem() instanceof ItemBow ? 0.05 : stack.getItem() instanceof ItemPotion ? 0.4 : stack.getItem() instanceof ItemExpBottle ? 0.1 : 0.03) * 0.1, velocity.z);

                vertexCount = i;

                MovingObjectPosition result = mc.theWorld.rayTraceBlocks(
                        EntityUtil.getInterpolatedPosition(mc.thePlayer)
                                .add(new Vec3(0.0, mc.thePlayer.getEyeHeight(), 0.0)),
                        new Vec3(position.x, position.y, position.z));

                if (result != null) {
                    break;
                }
            }

            glEnd();

            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            glDisable(GL_LINE_SMOOTH);
            glPopMatrix();

            if (box.getValue()) {
                AxisAlignedBB bb = new AxisAlignedBB(
                        position.x - mc.getRenderManager().viewerPosX - 0.25,
                        position.y - mc.getRenderManager().viewerPosY - 0.25,
                        position.z - mc.getRenderManager().viewerPosZ - 0.25,
                        position.x - mc.getRenderManager().viewerPosX + 0.25,
                        position.y - mc.getRenderManager().viewerPosY + 0.25,
                        position.z - mc.getRenderManager().viewerPosZ + 0.25
                );

                Color colour = ColorUtil.fadeBetween(3, vertexCount * 15, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);

                Accent.EnumAccents enumeration = Wrapper.getModule(Accent.class).accents.getValue();

                if (enumeration.equals(Accent.EnumAccents.ASTOLFO)) {
                    colour = ColorUtil.astolfoColorsC(vertexCount * 5, vertexCount * 20);
                } else if (enumeration.equals(Accent.EnumAccents.RAINBOW)) {
                    colour = ColorUtil.rainbow(vertexCount * 300L);
                }

                new RenderBuilder()
                        .boundingBox(bb)
                        .innerColour(ColorUtil.integrateAlpha(colour, 100))
                        .outerColour(ColorUtil.integrateAlpha(colour, 255))
                        .type(BoxRenderMode.FILL)
                        .start()
                        .blend()
                        .depth()
                        .texture()
                        .build(false);
            }
        }
    };

}
