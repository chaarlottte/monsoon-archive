package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.font.IFontRenderer;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.module.combat.Aura;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TargetHUD extends HUDModule {
    AbstractClientPlayer target, oldTarget;
    float health = 20, absorption = 0;

    public TargetHUD() {
        super("Target HUD", "Displays information about the current target", 400, 400);
    }

    Setting<TargetHUDTheme> theme = new Setting<>("Theme", TargetHUDTheme.NEW).describedBy("Them of the TargetHUD");

    Setting<Boolean> followPlayer = new Setting<>("Follow Player", false).describedBy("Whether to follow the target or not");

    Animation damageAnim = new Animation(() -> 450F, false, () -> Easing.CUBIC_IN),
            absorptionAnim = new Animation(() -> 400F, false, () -> Easing.CUBIC_IN),
            hasAbsorbtion = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT),
            targetRenderAnimation = new Animation(() -> 150f, false, () -> Easing.CUBIC_IN_OUT);

    private boolean shouldDraw;

    private Timer animTimer = new Timer();

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);

    float[] coords = null;
    float coordX = 0, coordY = 0;

    int maxX2 = 30;

    @EventLink
    public final Listener<EventRender3D> eventRender3D = e -> {
        if (target == null) {
            coords = null;
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        Vec3 vec3 = getVec3(target);
        float posX = (float) (vec3.x - mc.getRenderManager().viewerPosX);
        float posY = (float) (vec3.y - mc.getRenderManager().viewerPosY);
        float posZ = (float) (vec3.z - mc.getRenderManager().viewerPosZ);
        double halfWidth = target.width / 2.0D + 0.18F;
        AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
                posY + target.height + 0.18D, posZ + halfWidth);
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
        coords = new float[]{position.x, position.z, position.y, position.w};
    };

    private Vec3 getVec3(final EntityLivingBase var0) {
        final float timer = mc.getTimer().renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
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

    void drawSolidOutline() {

        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];

        Color cc1, cc2, cc3, cc4;
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }

        RoundedUtils.outline(coordX, coordY, getWidth(), getHeight(), 10, 1f, 2f,
                cc1,
                cc2,
                cc3,
                cc4
        );
    }

    void drawShadowOutline() {

        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];

        Color cc1, cc2, cc3, cc4;
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }

        RoundedUtils.shadowGradient(coordX + 1.5f, coordY + 1.5f, getWidth() - 3f, getHeight() - 3f, 10, 10f, 2f,
                cc1,
                cc2,
                cc3,
                cc4, false
        );
    }

    void drawNewTargetHUD(AbstractClientPlayer target) {
        RenderUtil.getDefaultHudRenderer(coordX,coordY,getWidth(),getHeight());

        float distance = (float) Math.round(mc.thePlayer.getDistanceToEntity(target) * 100f) / 100f;
        Wrapper.getFont().drawString(target.getCommandSenderName(), coordX + 28, coordY + 4, Color.WHITE, false);
        Wrapper.getFont().drawString(distance + "", coordX + 28, coordY + 16, Color.WHITE, false);

        GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(coordX + 6, coordY + 6, 3, 3, 3, 3, 20, 20, 24, 24f);
        mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(coordX + 6, coordY + 6, 15, 3, 3, 3, 20, 20, 24, 24f);
        GL11.glDisable(GL11.GL_BLEND);
        RoundedUtils.round(coordX + 6, coordY + 6 + 20 + 4 + 7, (absorption / 20f) * (getWidth() - 12), 4, 1f, new Color(250, 218, 82, 255));

        RoundedUtils.gradient(coordX + 6, coordY + 6 + 20 + 4, (health / 20f) * (getWidth() - 12), 4, 1.5f, 1f,
                ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.fadeBetween(10, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]),
                ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]), ColorUtil.fadeBetween(10, 180, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1])
        );
    }

    void drawOldTargetHUD(AbstractClientPlayer target) {
        FontRenderer small = Wrapper.getFontUtil().productSansSmaller;
        Gui.drawRect(coordX, coordY, coordX + getWidth(), coordY + getHeight(), 0x70000000);
        for (int i = 0; i < getWidth() - 2; i++) {
            RenderUtil.drawRect(coordX + i + 1, coordY + 1, 1, 1, Wrapper.getModule(HUD.class).getColorForArray2(i, i * 2));
        }

        for (int i = 0; i < (health / 20f) * (getWidth() - 4 - 20 - 8); i++) {
            RenderUtil.drawRect(coordX + 4 + 20 + 4 + i, coordY + 4 + 10 + 1, 1, 5, Wrapper.getModule(HUD.class).getColorForArray2(i, i * 2));
        }
        // RenderUtil.drawRect(coordX + 4 + 20 + 4, coordY + 4 + 10 + 1, (health / 20f) * (getWidth() - 4 - 20 - 8), 5, -1);
        RenderUtil.drawRect(coordX + 4 + 20 + 4, coordY + 4 + 10 + 1 + 2.5f, (absorption / 20f) * (getWidth() - 4 - 20 - 8), 2.5f, new Color(250, 218, 82, 255).getRGB());

        Wrapper.getFont().drawString(Math.round(target.getHealth()) / 2f + "", coordX + 28, coordY + 3, Color.WHITE, false);
        small.drawString(target.getNameClear().toLowerCase(), coordX + 28, coordY + getHeight() - 8, Color.WHITE, false);
        small.drawString("distance: " + Math.round(target.getDistanceToEntity(mc.thePlayer)), coordX + 28 + 20, coordY + 3, Color.WHITE, false);
        small.drawString("armor: " + target.getTotalArmorValue(), coordX + 28 + 20, coordY + 3 + small.getHeight() - 1, Color.WHITE, false);

        GlStateManager.resetColor();
        mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(coordX + 4, coordY + 4, 3, 3, 3, 3, 20, 20, 24, 24f);
        mc.getTextureManager().bindTexture(target.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(coordX + 4, coordY + 4, 15, 3, 3, 3, 20, 20, 24, 24f);
    }

    void drawExhiTargetHUD(AbstractClientPlayer target) {
        if(target.ticksExisted < 40 && mc.thePlayer.ticksExisted < 40) return;
        IFontRenderer fr = mc.fontRendererObj;
        Gui.drawRect(coordX, coordY, coordX + getWidth(), coordY + getHeight(), new Color(0, 0, 0, 170).getRGB());
        GuiInventory.drawEntityOnScreen((int) (coordX + 16), (int) (coordY + 28), 13, -target.rotationYaw, target.rotationPitch, target);

        GlStateManager.pushMatrix();
        GlStateManager.translate(coordX + 33, coordY + 2.5f, 0);
        GlStateManager.scale(0.85f, 0.85f, 1);
        fr.drawStringWithShadow(target.getNameClear(), 0, 0, -1);
        GlStateManager.popMatrix();

        float width = getWidth() - 33 - 10;
        float healthRect = width * MathHelper.clamp_float(target.getHealth() / target.getMaxHealth(), 0.0f, 1.0f);

        float percentage = (target.getHealth() / target.getMaxHealth()) / 3;
        Gui.drawRect(coordX + 33, coordY + 12f, coordX + 33 + healthRect, coordY + 15f, Color.HSBtoRGB(percentage, 1.0F, 1.0F));

        DrawUtil.drawHollowRect(coordX + 33, coordY + 12f, coordX + 33 + width, coordY + 15f, 0.6f, Color.BLACK.getRGB());
        float spacing = (getWidth() - 33 - 10) / 9;
        for(int i = 1; i < 9; i++) {
            DrawUtil.drawRect(coordX + 32.75 + (i * spacing), coordY + 12f, coordX + 33.25 + (i * spacing), coordY + 15f, Color.BLACK.getRGB());
        }

        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        StringBuilder line3 = new StringBuilder();

        line1.append("HP: ").append((int) target.getHealth());
        line1.append(" | ");
        line1.append("Dist: ").append((int) (mc.thePlayer.getDistanceToEntity(target)));

        line2.append("G: ").append(target.onGround ? "true" : "false");
        line2.append(" ");
        line2.append("CV: ").append(target.isCollidedVertically ? "true" : "false");

        line3.append("TCG: ").append(target.ticksExisted);
        line3.append(" ");
        line3.append("HURT: ").append(target.hurtTime);

        GlStateManager.pushMatrix();
        GlStateManager.translate(coordX + 33, coordY + 17.5f, 0);
        GlStateManager.scale(0.5f, 0.5f, 1);
        // GlStateManager.translate(-x, -y, 0);
        fr.drawStringWithShadow(line1.toString(), 0, 0, -1);
        fr.drawStringWithShadow(line2.toString(), 0, 8, -1);
        fr.drawStringWithShadow(line3.toString(), 0, 16, -1);
        GlStateManager.popMatrix();
    }

    void drawNoboLion(AbstractClientPlayer target) {
        if(target.ticksExisted < 40 && mc.thePlayer.ticksExisted < 40) return;
        this.maxX2 = 30;
        ScaledResolution sr = new ScaledResolution(mc);
        float healthPercentage = target.getHealth() / target.getMaxHealth();
        float startX = 20;
        if (target.getHeldItem() != null) maxX2 += 15;

        for(int i = 3; i >= 0; i--) {
            if (target.getCurrentArmor(i) != null) maxX2 += 15;
        }

        float percentage = (target.getHealth() / target.getMaxHealth()) / 3;
        final int healthColor = Color.HSBtoRGB(percentage, 1.0F, 1.0F);
        float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(target.getNameClear()) + 30);
        Gui.drawRect(this.getX(), this.getY(), this.getX() + maxX, this.getY() + 40, new Color(0, 0, 0, 0.3f).getRGB());
        Gui.drawRect(this.getX(), this.getY() + 38.5f, this.getX() + (maxX * healthPercentage), this.getY() + 40, healthColor);
        mc.fontRendererObj.drawStringWithShadow(target.getNameClear(), this.getX() + 25, this.getY() + 7, -1);
        int xAdd = 0;
        double multiplier = 0.85;
        // double multiplier = 1;
        GlStateManager.pushMatrix();
        GlStateManager.scale(multiplier, multiplier, multiplier);
        for(int i = 3; i >= 0; i--) {
            if (target.getCurrentArmor(i) != null) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(i), (int) (((this.getX() + 23) + xAdd) / multiplier), (int) ((this.getY() + 18) / multiplier));
                xAdd += 15;
            }
        }
        if (target.getHeldItem() != null)
            mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem(), (int) (((this.getX() + 23) + xAdd) / multiplier), (int) ((this.getY() + 18) / multiplier));
        GlStateManager.popMatrix();
        GuiInventory.drawEntityOnScreen((int) this.getX() + 12, (int) this.getY() + 33, 15, target.rotationYaw, target.rotationPitch, target);

    }

    void barry(AbstractClientPlayer target) {
        FontRenderer small = Wrapper.getFontUtil().productSansSmaller;
        //Gui.drawRect(coordX, coordY, coordX + getWidth(), coordY + getHeight(), 0x70000000);
        DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/barry.png"),coordX, coordY, getWidth(), getHeight(), new Color(255, 255, 255, 100));

        // RenderUtil.drawRect(coordX + 4 + 20 + 4, coordY + 4 + 10 + 1, (health / 20f) * (getWidth() - 4 - 20 - 8), 5, -1);
        DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/barry.png"),coordX + 4 + 20 + 4, coordY + 4 + 10 + 1, (health / 20f) * (getWidth() - 4 - 20 - 8), 5, Color.WHITE);
        // RenderUtil.drawRect(coordX + 4 + 20 + 4, coordY + 4 + 10 + 1 + 2.5f, (absorption / 20f) * (getWidth() - 4 - 20 - 8), 2.5f, new Color(250, 218, 82, 255).getRGB());

        Wrapper.getFont().drawString(Math.round(target.getHealth()) / 2f + "", coordX + 28, coordY + 3, Color.WHITE, false);
        small.drawString("barry", coordX + 28, coordY + getHeight() - 8, Color.WHITE, false);
        small.drawString("distance: " + Math.round(target.getDistanceToEntity(mc.thePlayer)), coordX + 28 + 20, coordY + 3, Color.WHITE, false);
        small.drawString("armor: " + target.getTotalArmorValue(), coordX + 28 + 20, coordY + 3 + small.getHeight() - 1, Color.WHITE, false);

        GlStateManager.resetColor();
        DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/barry.png"),coordX + 4, coordY + 4, 20, 20, Color.WHITE);
    }

    @Override
    public void render() {

        if (followPlayer.getValue()) {
            coordX = coords == null ? getX() : coords[0] + (coords[1] - coords[0]) / 2f - getWidth() / 2f;
            coordY = coords == null ? getY() : coords[2] + (coords[3] - coords[2]) / 2f - getHeight() / 2f;
        } else {
            coordX = getX();
            coordY = getY();
        }

        if (target == mc.thePlayer) {
            coordX = getX();
            coordY = getY();
        }

        if (Wrapper.getModule(Aura.class).isEnabled()) {
            target = Wrapper.getModule(Aura.class).getTarget() instanceof AbstractClientPlayer ? (AbstractClientPlayer) Wrapper.getModule(Aura.class).getTarget() : null;
        } else {
            target = mc.pointedEntity instanceof AbstractClientPlayer ? (AbstractClientPlayer) mc.pointedEntity : null;
        }
        if (mc.currentScreen instanceof GuiChat) {
            target = mc.thePlayer;
        }

        if (target == null) {
            health = 0;
            absorption = 0;
            if(targetRenderAnimation.getState()) animTimer.reset();
            targetRenderAnimation.setState(false);
            // shouldDraw = animTimer.hasTimeElapsed(150, false);
        } else {
            oldTarget = target;
            targetRenderAnimation.setState(true);
            shouldDraw = true;
        }

        if((target == null && oldTarget == null) || !shouldDraw) return;
        else if(target == null && oldTarget != null) target = oldTarget;

        damageAnim.setState(target.getHealth() != health);
        absorptionAnim.setState(target.getAbsorptionAmount() != absorption);
        hasAbsorbtion.setState(target.getAbsorptionAmount() != 0);

        if (target.getHealth() < health) {
            float diff = health - target.getHealth();
            health -= diff * (float) damageAnim.getAnimationFactor();
        }
        if (target.getHealth() > health) {
            float diff = target.getHealth() - health;
            health += diff * (float) damageAnim.getAnimationFactor();
        }

        if (target.getAbsorptionAmount() < absorption) {
            float diff = absorption - target.getAbsorptionAmount();
            absorption -= diff * (float) absorptionAnim.getAnimationFactor();
        }
        if (target.getAbsorptionAmount() > absorption) {
            float diff = target.getAbsorptionAmount() - absorption;
            absorption += diff * (float) absorptionAnim.getAnimationFactor();
        }

        RenderUtil.scaleXY(getX() + getWidth() / 2f, getY() + getHeight() / 2f, targetRenderAnimation, () -> {
            switch (theme.getValue()) {
                case NEW:
                    this.drawNewTargetHUD(target != null ? target : oldTarget);
                    break;
                case OLD:
                    this.drawOldTargetHUD(target != null ? target : oldTarget);
                    break;
                case EXHIBITION:
                    this.drawExhiTargetHUD(target != null ? target : oldTarget);
                    break;
                case NOBOLION:
                    this.drawNoboLion(target != null ? target : oldTarget);
                    break;
                case BARRY:
                    this.barry(target != null ? target : oldTarget);
                    break;
            }
        });
    }

    @Override
    public void blur() {
        if ((target == null && oldTarget == null) || !shouldDraw) return;

        RenderUtil.scaleXY(getX() + getWidth() / 2f, getY() + getHeight() / 2f, targetRenderAnimation, () -> {
            switch (theme.getValue()) {
                case NEW:
                    RoundedUtils.glRound(coordX, coordY, getWidth(), getHeight(), 10, Wrapper.getPallet().getBackground().getRGB());
                    break;
                case OLD:
                    RenderUtil.drawRect(coordX, coordY, getWidth(), getHeight(), 0x50000000);
                    break;
            }
        });
    }

    @Override
    public float getWidth() {
        switch (theme.getValue()) {
            case NEW:
            case OLD:
            case BARRY:
                if (target != null) {
                    float absorption = (float) absorptionAnim.getAnimationFactor() * target.getAbsorptionAmount();
                    return Math.max(Wrapper.getFont().getStringWidth(target.getCommandSenderName()), 110);
                }
                return 110;
            case EXHIBITION:
                if(target != null) return Math.max((33 + mc.fontRendererObj.getStringWidth(target.getNameClear()) + 5), 100);
                else return 100;
            case NOBOLION:
                return maxX2;
        }

        return 0;
    }

    @Override
    public float getHeight() {
        switch (theme.getValue()) {
            case NEW:
                return (40 + (float) hasAbsorbtion.getAnimationFactor() * 7);
            case OLD:
            case BARRY:
                return 28;
            case EXHIBITION:
                return 32;
            case NOBOLION:
                return 40;
        }

        return 0;
    }

    public enum TargetHUDTheme {
        NEW, OLD, EXHIBITION, NOBOLION, BARRY
    }
}
