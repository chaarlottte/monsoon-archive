package wtf.monsoon.impl.module.visual;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventBlur;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventRender3D;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class ESP extends Module {

    public Setting<Mode> mode = new Setting<>("Mode", Mode.HOLLOW)
            .describedBy("The mode of ESP");

    public Setting<Boolean> blur = new Setting<>("Blur", false)
            .describedBy("Whether to draw blurred rect");

    public Setting<Boolean> glow = new Setting<>("Glow", false)
            .describedBy("Whether to render the glow.");

    public Setting<Boolean> healthbar = new Setting<>("Health", true)
            .describedBy("Whether to draw the health bar");

    public Setting<Boolean> outline = new Setting<>("Outlines", true)
            .describedBy("Whether to draw the outlines");

    public Setting<Boolean> barry = new Setting<>("Barry", false)
            .describedBy("Barry.");

    @Getter
    private final Setting<String> targets = new Setting<>("Entities", "Entities")
            .describedBy("Set valid targets for Aura.");

    @Getter
    private final Setting<Boolean> targetPlayers = new Setting<>("Players", true)
            .describedBy("Target players.")
            .childOf(targets);

    @Getter
    private final Setting<Boolean> targetAnimals = new Setting<>("Animals", false)
            .describedBy("Target animals.")
            .childOf(targets);

    @Getter
    private final Setting<Boolean> targetMonsters = new Setting<>("Monsters", false)
            .describedBy("Target monsters.")
            .childOf(targets);

    @Getter
    private final Setting<Boolean> targetInvisibles = new Setting<>("Invisibles", false)
            .describedBy("Target invisibles.")
            .childOf(targets);

    public ESP() {
        super("ESP", "Helps you see entities in the world.", Category.VISUAL);
    }

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final Map<EntityLivingBase, float[]> entityPosMap = new HashMap<>();
    private static final Map<EntityLivingBase, float[][]> entities = new HashMap<>();

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    public final Listener<EventRender3D> eventRender3D = e -> {
        if (mode.getValue() == Mode.FILLED || mode.getValue() == Mode.HOLLOW || mode.getValue() == Mode.BOTH || mode.getValue() == Mode.HEALTH_ONLY) {
            ScaledResolution sr = new ScaledResolution(mc);
            entities.keySet().removeIf(player -> !mc.theWorld.loadedEntityList.contains(player));
            if (!entityPosMap.isEmpty())
                entityPosMap.clear();
            int scaleFactor = sr.getScaleFactor();

            List<EntityLivingBase> targets = mc.theWorld.getLoadedEntityLivingBases().stream()
                    .filter(entity -> entity != Minecraft.getMinecraft().thePlayer || entity == Minecraft.getMinecraft().thePlayer)
                    .filter(entity -> entity.ticksExisted > 15)
                    .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= 250)
                    .filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity))
                    .filter(this::validTarget)
                    .sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity)))
                    .collect(Collectors.toList());

            for (EntityLivingBase player : targets) {
                if (player.getDistanceToEntity(mc.thePlayer) < 1.0F && mc.gameSettings.thirdPersonView < 1)
                    continue;
                GlStateManager.pushMatrix();
                Vec3 vec3 = getVec3(player);
                float posX = (float) (vec3.x - mc.getRenderManager().viewerPosX);
                float posY = (float) (vec3.y - mc.getRenderManager().viewerPosY);
                float posZ = (float) (vec3.z - mc.getRenderManager().viewerPosZ);
                double halfWidth = player.width / 2.0D + 0.18F;
                AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
                        posY + player.height + 0.18D, posZ + halfWidth);
                double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ},
                        {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};
                Vector3f projection;
                Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);
                for (double[] vec : vectors) {
                    projection = project2D((float) vec[0], (float) vec[1], (float) vec[2], scaleFactor);
                    if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
                        position.x = Math.min(position.x, projection.x);
                        position.y = Math.min(position.y, projection.y);
                        position.z = Math.max(position.z, projection.x);
                        position.w = Math.max(position.w, projection.y);
                    }
                }
                entityPosMap.put(player, new float[]{position.x, position.z, position.y, position.w});
                GlStateManager.popMatrix();
            }
        }
    };

    @EventLink
    public final Listener<EventBlur> eventBlur = e -> {
        if (blur.getValue()) {
            for (EntityLivingBase player : entityPosMap.keySet()) {
                if (!player.isInvisible()) {
                    glPushMatrix();
                    float[] positions = entityPosMap.get(player);
                    float x = positions[0];
                    float x2 = positions[1];
                    float y = positions[2];
                    float y2 = positions[3];
                    Gui.drawRect(x, y, x2, y2, -1);
                    glPopMatrix();
                }
            }
        }
    };

    @EventLink
    public final Listener<EventRender2D> eventRender2D = e -> {
        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);

        int colorInt = color.getRGB();

        if(mode.getValue() == Mode.FILLED || mode.getValue() == Mode.HOLLOW || mode.getValue() == Mode.BOTH || mode.getValue() == Mode.HEALTH_ONLY) {
            for (EntityLivingBase player : entityPosMap.keySet()) {
                if (!player.isInvisible()) {
                    glPushMatrix();
                    float[] positions = entityPosMap.get(player);
                    float x = positions[0];
                    float x2 = positions[1];
                    float y = positions[2];
                    float y2 = positions[3];
                    if(healthbar.getValue() || mode.getValue() == Mode.HEALTH_ONLY) {
                        Gui.drawRect(x - 2.5, y - 0.5F, x - 0.5F, y2 + 0.5F, 0x96000000);
                        float health = player.getHealth();
                        float maxHealth = player.getMaxHealth();
                        float healthPercentage = health / maxHealth;

                        float heightDif = y - y2;
                        float healthBarHeight = heightDif * healthPercentage;

                        int col = getColorFromPercentage(health, maxHealth);
                        Gui.drawRect(x - 2, y, x - 1, y - healthBarHeight, col);

                        double armorPercentage = player.getTotalArmorValue() / 20.0;
                        double armorBarWidth = (x2 - x) * armorPercentage;
                        Gui.drawRect(x, y2+ 0.5, x2 - 0.5f, y2 + 2.5f, 0x96000000);
                        if (armorPercentage > 0.0) {
                            Gui.drawRect(x + 0.5f, y2 + 1.0, x + armorBarWidth, y2 + 2.0, new Color(100, 100, 200).getRGB());
                        }
                    }

                    switch (mode.getValue()) {
                        case FILLED:
                            Color shit1 = new Color(colorInt);
                            Color shit2 = new Color(shit1.getRed(), shit1.getGreen(), shit1.getBlue(), 100);
                            Gui.drawRect(x, y, x2, y2, shit2.getRGB());
                            break;
                        case HOLLOW:
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            enableAlpha();
                            disableAlpha();

                            if(outline.getValue()) {
                                DrawUtil.drawHollowRectDefineWidth(x - 0.5f, y - 0.5f, x2 - 0.5f, y2 - 0.5f, 0.5f, 0x96000000);
                                DrawUtil.drawHollowRectDefineWidth(x + 0.5f, y + 0.5f, x2 + 0.5f, y2 + 0.5f, 0.5f, 0x96000000);
                            }

                            DrawUtil.drawHollowRectDefineWidth(x, y, x2, y2, 0.5f, colorInt);

                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            break;
                        case BOTH:
                            Color shit3 = new Color(colorInt);
                            Color shit4 = new Color(shit3.getRed(), shit3.getGreen(), shit3.getBlue(), 100);
                            Gui.drawRect(x, y, x2, y2, shit4.getRGB());
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            enableAlpha();
                            disableAlpha();

                            if(outline.getValue()) {
                                DrawUtil.drawHollowRectDefineWidth(x - 0.5f, y - 0.5f, x2 - 0.5f, y2 - 0.5f, 0.5f, 0x96000000);
                                DrawUtil.drawHollowRectDefineWidth(x + 0.5f, y + 0.5f, x2 + 0.5f, y2 + 0.5f, 0.5f, 0x96000000);
                            }

                            DrawUtil.drawHollowRectDefineWidth(x, y, x2, y2, 0.5f, colorInt);

                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            break;
                    }
                    float width = x2-x;
                    float height = y2-y;

                    float g = width/5.05f;
                    float h = height/5.05f;

                    if(glow.getValue()) RoundedUtils.shadow(x, y, width, height, 0,25, new Color(colorInt));

                    if(barry.getValue()) {
                        ResourceLocation barry = new ResourceLocation("monsoon/characters/barry.png");

                        mc.getTextureManager().bindTexture(barry);
                        glColor4f(1, 1, 1, 1);
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, x2 - x, y2 - y, x2 - x, y2 - y);
                    }
                    glPopMatrix();
                }
            }
        }
    };

    public static int getColorFromPercentage(float current, float max) {
        float percentage = (current / max) / 3;
        return Color.HSBtoRGB(percentage, 1.0F, 1.0F);
    }

    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        if (GLU.gluProject(x, y, z, modelMatrix, projectionMatrix, viewport, windowPosition)) {
            return new Vector3f(windowPosition.get(0) / scaleFactor,
                    (mc.displayHeight - windowPosition.get(1)) / scaleFactor, windowPosition.get(2));
        }

        return null;
    }

    public static void enableAlpha() {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    }

    public static void disableAlpha() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    private Vec3 getVec3(final EntityLivingBase var0) {
        final float timer = mc.getTimer().renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }

    private boolean validTarget(EntityLivingBase entity) {
        if(entity.isInvisible()) {
            return validTargetLayer2(entity) && targetInvisibles.getValue();
        } else {
            return validTargetLayer2(entity);
        }
    }

    private boolean validTargetLayer2(EntityLivingBase entity) {
        if(entity instanceof EntityPlayer) {
            return targetPlayers.getValue();
        } else if(entity instanceof EntityAnimal) {
            return targetAnimals.getValue();
        } else if(entity instanceof EntityMob) {
            return targetMonsters.getValue();
        } else if(entity instanceof EntityVillager || entity instanceof EntityArmorStand) {
            return false;
        } else return false;
    }

    enum Mode {
        HOLLOW,
        FILLED,
        BOTH,
        HEALTH_ONLY
    }

}
