package wtf.monsoon.impl.module.visual;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.shader.OutlineShader;
import wtf.monsoon.impl.event.EventRenderHotbar;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderESP extends Module {

    public Setting<Mode> mode = new Setting<>("Mode", Mode.SHADER)
            .describedBy("Whether to render the outlines.");

    public Setting<Boolean> outline = new Setting<>("Outline", true)
            .visibleWhen(() -> mode.getValue() == Mode.SHADER)
            .describedBy("Whether to render the outlines.");

    public Setting<Boolean> filled = new Setting<>("Filled", true)
            .visibleWhen(() -> mode.getValue() == Mode.SHADER)
            .describedBy("Whether to render filled inside of players.");

    public Setting<Float> lineWidth = new Setting<>("Line Width", 1.0f)
            .minimum(0.5f)
            .maximum(3.0f)
            .incrementation(0.1f)
            .visibleWhen(() -> outline.getValue() && mode.getValue() == Mode.SHADER)
            .describedBy("Whether to render filled inside of players.");

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


    public OutlineShader outlineShader;
    public Framebuffer framebuffer;
    public float lastScaleFactor, lastScaleWidth, lastScaleHeight;

    public ShaderESP() {
        super("Shader ESP", "Looks hot.", Category.VISUAL);
        outlineShader = new OutlineShader();
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    public final Listener<EventRenderHotbar.Pre> eventRender2D = event -> {
        if(mode.getValue() == Mode.SHADER) {
            if (mc.thePlayer.ticksExisted < 60) {
                return;
            }

            ScaledResolution sr = new ScaledResolution(mc);

            GlStateManager.enableAlpha();
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            if (framebuffer != null) {
                framebuffer.framebufferClear();

                if (lastScaleFactor != sr.getScaleFactor() || lastScaleWidth != sr.getScaledWidth() || lastScaleHeight != sr.getScaledHeight()) {
                    framebuffer.deleteFramebuffer();
                    framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                    framebuffer.framebufferClear();
                }

                lastScaleFactor = sr.getScaleFactor();
                lastScaleWidth = sr.getScaledWidth();
                lastScaleHeight = sr.getScaledHeight();
            } else {
                framebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
            }

            framebuffer.bindFramebuffer(false);
            boolean previousShadows = mc.gameSettings.field_181151_V;
            mc.gameSettings.field_181151_V = false;

            mc.entityRenderer.setupCameraTransform(mc.getTimer().renderPartialTicks, 0);

            Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);

            int colorInt = color.getRGB();

            List<EntityLivingBase> targets = mc.theWorld.getLoadedEntityLivingBases().stream()
                    .filter(entity -> entity != Minecraft.getMinecraft().thePlayer)
                    .filter(entity -> entity.ticksExisted > 15)
                    .filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity))
                    .filter(this::validTarget)
                    .sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity)))
                    .collect(Collectors.toList());

            for (Entity entity : targets) {
                if (entity != null && entity != mc.thePlayer) {
                    if (!(entity instanceof EntityLivingBase)) continue;
                    mc.getRenderManager().renderEntityStatic(entity, mc.getTimer().renderPartialTicks, false);
                }
            }

            mc.gameSettings.field_181151_V = previousShadows;
            GlStateManager.enableBlend();
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            framebuffer.unbindFramebuffer();
            mc.getFramebuffer().bindFramebuffer(true);
            mc.entityRenderer.disableLightmap();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.pushMatrix();

            outlineShader.setColour(new Color(colorInt));
            outlineShader.setWidth(lineWidth.getValue());
            outlineShader.setFill(filled.getValue() ? 1 : 0);
            outlineShader.setOutline(outline.getValue() ? 1 : 0);
            outlineShader.startShader();

            mc.entityRenderer.setupOverlayRendering();

            glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
            glBegin(GL_QUADS);
            glTexCoord2d(0, 1);
            glVertex2d(0, 0);
            glTexCoord2d(0, 0);
            glVertex2d(0, sr.getScaledHeight());
            glTexCoord2d(1, 0);
            glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
            glTexCoord2d(1, 1);
            glVertex2d(sr.getScaledWidth(), 0);
            glEnd();

            glUseProgram(0);
            glPopMatrix();

            GlStateManager.popMatrix();
            GlStateManager.popAttrib();

            mc.entityRenderer.setupOverlayRendering();
        } else if(mode.getValue() == Mode.MINECRAFT_OUTLINE) {
            if (Config.isFastRender()) {
                Wrapper.getNotifManager().notify(NotificationType.WARNING, "Shader ESP", "Please disable fast render to use Shader ESP!");
                this.toggle();
            }
        }
    };

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

    public boolean shouldRenderMinecraftOutlin() {
        return this.isEnabled() && mode.getValue() == Mode.MINECRAFT_OUTLINE;
    }

    enum Mode {
        SHADER, MINECRAFT_OUTLINE
    }
}
