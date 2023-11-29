package wtf.monsoon.impl.module.visual;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

public class ChestESP extends Module {

    public static ChestESP INSTANCE;

    public Setting<Mode> mode = new Setting<>("Mode", Mode.HOLLOW)
            .describedBy("The mode of ESP");

    public Setting<Boolean> blur = new Setting<>("Blur", true)
            .describedBy("Whether to draw blurred rect");

    public Setting<Boolean> glow = new Setting<>("Glow", true)
            .describedBy("Whether to render the glow.");

    public Setting<Boolean> outline = new Setting<>("Outlines", true)
            .describedBy("Whether to draw the outlines");

    public Setting<EnumColor> color = new Setting<>("Color", EnumColor.MONSOON)
            .describedBy("The color of the ESP");

    public Setting<Color> customColor = new Setting<>("CustomColor", new Color(0, 140, 255))
            .describedBy("The color of the ESP")
            .visibleWhen(() -> color.getValue() == EnumColor.CUSTOM);

    public ChestESP() {
        super("Chest ESP", "Helps you see chests in the world.", Category.VISUAL);
        INSTANCE = this;
    }

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final Map<TileEntity, float[]> entityPosMap = new HashMap<>();
    private static final Map<TileEntity, float[][]> entities = new HashMap<>();


    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    public final Listener<EventRender3D> eventRender3D = e -> {
        if (mode.getValue() == Mode.FILLED || mode.getValue() == Mode.HOLLOW || mode.getValue() == Mode.BOTH) {
            ScaledResolution sr = new ScaledResolution(mc);
            entities.keySet().removeIf(player -> !mc.theWorld.loadedTileEntityList.contains(player));
            if (!entityPosMap.isEmpty())
                entityPosMap.clear();
            int scaleFactor = sr.getScaleFactor();
            for (Object fuck : mc.theWorld.loadedTileEntityList) {
                if (fuck instanceof TileEntityChest) {
                    TileEntityChest player = (TileEntityChest) fuck;
                    GlStateManager.pushMatrix();
                    Vec3 vec3 = getVec3(player);
                    float posX = (float) (vec3.x - mc.getRenderManager().viewerPosX);
                    float posY = (float) (vec3.y - mc.getRenderManager().viewerPosY);
                    float posZ = (float) (vec3.z - mc.getRenderManager().viewerPosZ);
                    AxisAlignedBB bb = new AxisAlignedBB(posX + 1, posY + 1, posZ + 1, posX, posY, posZ);
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
                } else if (fuck instanceof TileEntityEnderChest) {
                    TileEntityEnderChest player = (TileEntityEnderChest) fuck;
                    GlStateManager.pushMatrix();
                    Vec3 vec3 = getVec3(player);
                    float posX = (float) (vec3.x - mc.getRenderManager().viewerPosX);
                    float posY = (float) (vec3.y - mc.getRenderManager().viewerPosY);
                    float posZ = (float) (vec3.z - mc.getRenderManager().viewerPosZ);
                    AxisAlignedBB bb = new AxisAlignedBB(posX + 1, posY + 1, posZ + 1, posX, posY, posZ);
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
        }
    };

    @EventLink
    public final Listener<EventBlur> eventBlur = e -> {
        if (blur.getValue()) {
            for (TileEntity player : entityPosMap.keySet()) {
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
    };

    @EventLink
    public final Listener<EventRender2D> eventRender2D = e -> {
        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        Color alphaColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);

        int colorInt = color.getRGB();

        if(mode.getValue() == Mode.FILLED || mode.getValue() == Mode.HOLLOW || mode.getValue() == Mode.BOTH) {
            for (TileEntity player : entityPosMap.keySet()) {

                if(this.color.getValue().equals(EnumColor.CHEST_TYPE)) {
                    if(player instanceof TileEntityEnderChest) {
                        colorInt = new Color(211, 3, 252).getRGB();
                    } else {
                        colorInt = new Color(252, 248, 3).getRGB();
                    }
                }

                glPushMatrix();
                float[] positions = entityPosMap.get(player);
                float x = positions[0];
                float x2 = positions[1];
                float y = positions[2];
                float y2 = positions[3];

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

                float g = width/5;
                float h = height/5;

                if(glow.getValue()) RoundedUtils.shadow(x, y, width, height, 0,10, new Color(colorInt));
                glPopMatrix();
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

    private Vec3 getVec3(final TileEntity var0) {
        final float timer = mc.getTimer().renderPartialTicks;
        final double x = var0.getPos().getX() + (0) * timer;
        final double y = var0.getPos().getY() + (0) * timer;
        final double z = var0.getPos().getZ() + (0) * timer;
        return new Vec3(x, y, z);
    }

    enum Mode {
        HOLLOW,
        FILLED,
        BOTH
    }

    enum EnumColor {
        CUSTOM(() -> ChestESP.INSTANCE.customColor.getValue().getRGB()),
        ASTOLFO(() -> ColorUtil.astolfoColors(14, 14)),
        MONSOON(() -> ColorUtil.fadeBetween(new Color(0, 140, 255).getRGB(), new Color(0, 255, 255).getRGB(), System.currentTimeMillis() % 1500 / (1500 / 2.0f))),
        RAINBOW(() -> ColorUtil.rainbow(4)),
        CHEST_TYPE(() -> -1);

        private final Supplier<Integer> colour;

        EnumColor(Supplier<Integer> colour) {
            this.colour = colour;
        }

        public int getColor() {
            return colour.get();
        }
    }

}
