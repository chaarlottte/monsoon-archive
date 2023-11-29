package wtf.monsoon.impl.module.combat;

import com.viaversion.viaversion.util.MathUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.*;
import wtf.monsoon.impl.module.player.Scaffold;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Aura extends Module {

    private final Setting<String> attackSettings = new Setting<>("Attack Settings", "Attack Settings")
            .describedBy("Configure your attacks.");

    private final Setting<Boolean> randomization = new Setting<>("Randomization", true)
            .describedBy("Randomize attack times.")
            .childOf(attackSettings);

    private Setting<Double> aps = new Setting<>("APS", 10D)
            .minimum(1D)
            .maximum(20D)
            .incrementation(0.5D)
            .describedBy("The amount of times to attack per second")
            .visibleWhen(() -> !this.randomization.getValue())
            .childOf(attackSettings);

    private Setting<Double> minCps = new Setting<Double>("Min APS", 10D)
            .minimum(1D)
            .maximum(20D)
            .incrementation(0.5D)
            .describedBy("The amount of times to attack per second")
            .visibleWhen(this.randomization::getValue)
            .childOf(attackSettings);

    private Setting<Double> maxCps = new Setting<Double>("Max APS", 10D) {
        @Override
        public void setValue(Double d) {
            this.value = Math.max(minCps.getValue(), d);
        }
    }
            .minimum(1D)
            .maximum(20D)
            .incrementation(0.5D)
            .describedBy("The amount of times to attack per second")
            .visibleWhen(this.randomization::getValue)
            .childOf(attackSettings);

    @Getter private Setting<Double> range = new Setting<>("Range", 4D)
            .minimum(2D)
            .maximum(6D)
            .incrementation(0.1D)
            .describedBy("The range to attack")
            .childOf(attackSettings);

    private final Setting<BlockMode> blockMode = new Setting<>("Block Mode", BlockMode.FAKE)
            .describedBy("The autoblock mode.")
            .childOf(attackSettings);

    private final Setting<AttackStage> attackStage = new Setting<>("Attack Stage", AttackStage.PRE)
            .describedBy("The attack stage.")
            .childOf(attackSettings);

    private final Setting<String> bypassSettings = new Setting<>("Bypass Settings", "Bypass Settings")
            .describedBy("Settings that involve bypasses.");

    private final Setting<Boolean> hitDelay = new Setting<>("1.9+ Hit Delay", false)
            .describedBy("Whether or not to use the 1.9+ hit delay when attacking.")
            .childOf(bypassSettings);

    private final Setting<Boolean> moveFix = new Setting<>("Move Fix", false)
            .describedBy("Fix the move speed when attacking")
            .childOf(bypassSettings);

    private final Setting<Boolean> gcdFix = new Setting<>("GCD Fix", false)
            .describedBy("Whether to enable a GCD fix.")
            .childOf(bypassSettings);

    private final Setting<Boolean> noRots = new Setting<>("No Rotations", false)
            .describedBy("Don't set rotations.")
            .childOf(bypassSettings);

    private final Setting<Boolean> whileInventoryOpen = new Setting<>("While Inventory Open", true)
            .describedBy("Don't attack when in a GUI.")
            .childOf(bypassSettings);

    private final Setting<String> targets = new Setting<>("Targets", "Targets")
            .describedBy("Set valid targets for Aura.");

    private final Setting<Boolean> targetPlayers = new Setting<>("Players", true)
            .describedBy("Target players.")
            .childOf(targets);

    private final Setting<Boolean> targetAnimals = new Setting<>("Animals", false)
            .describedBy("Target animals.")
            .childOf(targets);

    private final Setting<Boolean> targetMonsters = new Setting<>("Monsters", false)
            .describedBy("Target monsters.")
            .childOf(targets);

    private final Setting<Boolean> targetInvisibles = new Setting<>("Invisibles", false)
            .describedBy("Target invisibles.")
            .childOf(targets);

    private final Setting<Boolean> targetThruWalls = new Setting<>("Through Walls", true)
            .describedBy("Target entities through walls.")
            .childOf(targets);

    private final Setting<String> misc = new Setting<>("Miscellaneous", "Miscellaneous")
            .describedBy("Miscellaneous settings for Aura.");

    private final Setting<Boolean> prediction = new Setting<>("Prediction", true)
            .describedBy("Predict where the entity will move, and rotate accordingly.")
            .childOf(misc);

    private final Setting<Boolean> smoothRotations = new Setting<>("Smooth Rotations", true)
            .describedBy("Rotate smoothly.")
            .childOf(misc);

    private final Setting<Boolean> hvhMode = new Setting<>("HVH Mode", false)
            .describedBy("Enable HvH mode.")
            .childOf(misc);

    private boolean blocking = false;

    @Getter
    private EntityLivingBase target;

    private final Timer attackTimer = new Timer();

    private float finalYaw;
    private float finalPitch;

    private int ticksSinceLastSwingDelayPacket = 0, blockingTicks;

    private long lastRandomizedAttackTime;

    public Aura() {
        super("Aura", "Automatically hit entities.", Category.COMBAT);
        this.setMetadata(() -> "R " + (int) range.getValue().doubleValue() + " APS " + (int) aps.getValue().doubleValue());

        aps = aps.visibleWhen(() -> !hitDelay.getValue());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        attackTimer.reset();

        finalYaw = mc.thePlayer.rotationYaw;
        finalPitch = mc.thePlayer.rotationPitch;
        mc.gameSettings.keyBindUseItem.pressed = false;
        blocking = true;
        blockingTicks = 0;
        ticksSinceLastSwingDelayPacket = 0;
        if(this.randomization.getValue()) {
            double randomCps = MathUtils.randomNumber(maxCps.getValue(), Math.max(minCps.getValue(), maxCps.getValue() - 1));
            this.lastRandomizedAttackTime = (long) (1000 / randomCps);
        } else {
            this.lastRandomizedAttackTime = (long) (1000 / aps.getValue());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.releaseBlock();
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if(Wrapper.getModule(Scaffold.class).isEnabled()) {
            this.target = null;
            return;
        }
        target = this.getSingleTarget();

        if(target == null) {
            this.releaseBlock();
            this.blockingTicks = 0;
            return;
        }

        if (!moveFix.getValue()) {
            float[] rots = this.getRotations(target);
            //finalYaw = processRotation((float) ((rots[0]) + MathUtils.randomNumber(7, -7)));
            //finalPitch = processRotation((float) ((rots[1]) + MathUtils.randomNumber(7, -7)));
            finalYaw = processRotation((float) ((rots[0]) ));
            finalPitch = processRotation((float) ((rots[1])));
        }

        if(prediction.getValue()) {
            finalYaw = (float) (finalYaw + ((Math.abs(target.posX - target.lastTickPosX) - Math.abs(target.posZ - target.lastTickPosZ)) * (2 / 3)) * 2);
            finalPitch = (float) (finalPitch + ((Math.abs(target.posY - target.lastTickPosY) - Math.abs(target.getEntityBoundingBox().minY - target.lastTickPosY)) * (2 / 3)) * 2);
        }

        if(smoothRotations.getValue()) {
            float sens = (float) ((Math.pow(Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F, 3) * 8.0F) * 0.15F);
            finalYaw = interpolateRotation(mc.thePlayer.rotationYaw, finalYaw, 360);
            finalPitch = interpolateRotation(mc.thePlayer.rotationPitch, finalPitch, 90);
            finalYaw = Math.round(finalYaw / sens) * sens;
            finalPitch = Math.round(finalPitch / sens) * sens;
        }

        if(!noRots.getValue()) {
            e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalYaw);
            e.setPitch(mc.thePlayer.rotationPitchHead = finalPitch);
        } else {
            mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalYaw;
            mc.thePlayer.rotationPitchHead = finalPitch;
        }

        if (mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest) {
            if (!whileInventoryOpen.getValue()) {
                this.releaseBlock();
                return;
            }
        }

        if (Wrapper.getModule(Scaffold.class).isEnabled()) return;


        this.preAutoblock();
        if(attackStage.getValue().equals(AttackStage.PRE) && hitTimerDone()) this.attack(target);

        ticksSinceLastSwingDelayPacket++;
    };

    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        if(attackStage.getValue().equals(AttackStage.POST) && hitTimerDone()) this.attack(target);
        this.postAutoblock();
    };

    @EventLink
    public final Listener<EventStrafing> eventStrafingListener = e -> {
        if (moveFix.getValue()) {
            float[] rots = this.getRotations(target);
            finalYaw = processRotation(rots[0]);
            finalPitch = processRotation(rots[1]);

            e.setYaw(finalYaw);
            e.setPitch(finalPitch);
        }
    };

    @EventLink
    public final Listener<EventRender2D> eventRender2DListener = e -> {
        if(hvhMode.getValue() && hitTimerDone()) this.attack(target);
    };

    @EventLink
    public final Listener<EventRender3D> eventRender2D = e -> {
        if(target != null) this.sigmaTargetThing(this.target);
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if(e.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle packet = (S45PacketTitle) e.getPacket();
            if(packet.getType() == S45PacketTitle.Type.SUBTITLE) {
                if(packet.getMessage().getFormattedText().contains("˙")) {
                    ticksSinceLastSwingDelayPacket = 0;
                }
            }
        }
    };

    private void attack(EntityLivingBase e) {
        if(e == null) return;
        if(ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
            mc.thePlayer.swingItem();
            PacketUtil.sendPacket(new C02PacketUseEntity(e, C02PacketUseEntity.Action.ATTACK));
        } else {
            PacketUtil.sendPacket(new C02PacketUseEntity(e, C02PacketUseEntity.Action.ATTACK));
            mc.thePlayer.swingItem();
        }

        this.ticksSinceLastSwingDelayPacket = 0;

        if(hitDelay.getValue()) {
            if(hitTimerDone(false) && !this.hitDelayOver()) {
                mc.thePlayer.swingNoPacket();
                // mc.thePlayer.playSound(e.getHurtSound(), e.getSoundVolume(), e.getSoundPitch());
            }
        }
    }

    private void swingAndAttack(EntityLivingBase entityIn) {
        if(entityIn == null) return;
        if(ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
            mc.thePlayer.swingItem();
            PacketUtil.sendPacket(new C02PacketUseEntity(entityIn, C02PacketUseEntity.Action.ATTACK));
        } else {
            // mc.playerController.attackEntity(mc.thePlayer, entityIn);
            PacketUtil.sendPacket(new C02PacketUseEntity(entityIn, C02PacketUseEntity.Action.ATTACK));
            mc.thePlayer.swingItem();
        }
        this.ticksSinceLastSwingDelayPacket = 0;
        // for (int i = 0; i < 2; i++) mc.thePlayer.onCriticalHit(entityIn);
    }

    private boolean hitTimerDone() {
        return this.hitTimerDone(hitDelay.getValue());
    }

    private boolean hitTimerDone(final boolean hitDelay) {
        boolean sex = ((this.attackTimer.hasTimeElapsed(this.lastRandomizedAttackTime, false) && !hitDelay) || (this.hitDelayOver() && hitDelay));
        boolean returnVal = false;
        if(sex) {
            returnVal = true;
            this.attackTimer.reset();
            if(this.randomization.getValue()) {
                double randomCps = MathUtils.randomNumber(maxCps.getValue(), Math.min(minCps.getValue(), maxCps.getValue() - 1));
                this.lastRandomizedAttackTime = (long) (1000 / randomCps);
            } else {
                this.lastRandomizedAttackTime = (long) (1000 / aps.getValue());
            }
        } else {
            if(hitDelay) {
                if(this.attackTimer.hasTimeElapsed((long) (1000 / aps.getValue()), false)) {
                    mc.thePlayer.swingNoPacket();
                    this.attackTimer.reset();
                }
            }
        }
        return returnVal;
    }

    private boolean hitDelayOver() {
        return ticksSinceLastSwingDelayPacket >= 2;
    }

    private void preAutoblock() {
        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && target != null) {
            switch (blockMode.getValue()) {
                case VANILLA:
                case H_V_H:
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    blocking = true;
                    break;
                case N_C_P:
                    break;
                case WATCHDOG:
                    if(this.blockingTicks == 0) {
                        PacketUtil.sendBlocking(true, true);
                        this.blockingTicks = 6;
                    } else if(this.blockingTicks == 4) {
                        PacketUtil.releaseUseItem(true);
                    }
                    this.blockingTicks--;
                    break;
                case VERUS:
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem(), new BlockPos(-1, -1, -1)));
                    blocking = true;
                    break;
                case CONTROL:
                    this.releaseBlock();
                    break;
            }
        }
    }

    private void postAutoblock() {
        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && target != null) {
            switch (blockMode.getValue()) {
                case N_C_P:
                    if(!blocking) {
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        blocking = true;
                    }
                    break;
                case CONTROL:
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    blocking = true;
                    break;
            }
        }
    }

    private void releaseBlock() {
        if(blocking) {
            switch (blockMode.getValue()) {
                case N_C_P:
                case VANILLA:
                case VERUS:
                case H_V_H:
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
                case CONTROL:
                    mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                    break;
                case WATCHDOG:
                    this.blockingTicks = 0;
                    break;
            }
        }
        blocking = false;
    }

    public float[] getRotations(EntityLivingBase target) {
        if (target == null) return new float[]{ mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
        final double xDist = target.posX - mc.thePlayer.posX;
        final double zDist = target.posZ - mc.thePlayer.posZ;
        final AxisAlignedBB entityBB = target.getEntityBoundingBox().expand(0.10000000149011612, 0.10000000149011612, 0.10000000149011612);
        final double playerEyePos = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        final double yDist = (playerEyePos > entityBB.maxY) ? (entityBB.maxY - playerEyePos) : ((playerEyePos < entityBB.minY) ? (entityBB.minY - playerEyePos) : 0.0);
        final double fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
        float yaw = interpolateRotation(finalYaw, (float) (StrictMath.atan2(zDist, xDist) * 57.29577951308232) - 90.0f, 45f);
        float pitch = interpolateRotation(finalPitch, (float) (-(StrictMath.atan2(yDist, fDist) * 57.29577951308232)), 45f);

        pitch = Math.min(pitch, 90);
        pitch = Math.max(pitch, -90);

        return new float[] { yaw, pitch } ;
    }

    private static float interpolateRotation(final float prev, final float now, final float maxTurn) {
        float var4 = MathHelper.wrapAngleTo180_float(now - prev);
        if (var4 > maxTurn) {
            var4 = maxTurn;
        }
        if (var4 < -maxTurn) {
            var4 = -maxTurn;
        }
        return prev + var4;
    }

    private float processRotation(float value) {
        float toReturn = value;
        if(gcdFix.getValue()) {
            double m = 0.005 * mc.gameSettings.mouseSensitivity;
            double f = m * 0.6 + 0.2;
            double gcd = m * m * m * 1.2;
            toReturn -= toReturn % gcd;
            return toReturn;
        } else return toReturn;
    }

    public EntityLivingBase getSingleTarget() {
        List<EntityLivingBase> targets = mc.theWorld.getLoadedEntityLivingBases().stream()
                .filter(entity -> entity != Minecraft.getMinecraft().thePlayer)
                .filter(entity -> entity.ticksExisted > 0)
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range.getValue())
                .filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity))
                .filter(entity -> !entity.isDead)
                .filter(entity -> entity.getHealth() > 0)
                .filter(this::validTarget)
                .sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity)))
                .collect(Collectors.toList());
        if(targets.isEmpty()) return null;
        else return targets.get(0);
    }

    public List<EntityLivingBase> getMultipleTargets(int amount) {
        List<EntityLivingBase> targets = mc.theWorld.getLoadedEntityLivingBases().stream()
                .filter(entity -> entity != Minecraft.getMinecraft().thePlayer)
                .filter(entity -> entity.ticksExisted > 0)
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range.getValue())
                .filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity))
                .filter(entity -> !entity.isDead)
                .filter(entity -> entity.getHealth() > 0)
                .filter(this::validTarget)
                .sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity)))
                .collect(Collectors.toList());

        List<EntityLivingBase> toReturn = new ArrayList<>();

        for(int i = 0; i < Math.max(amount, targets.size()); i++) {
            EntityLivingBase e = targets.get(i);
            if(e != null) {
                toReturn.add(e);
            }
        }

        return toReturn;
    }

    private boolean validTarget(EntityLivingBase entity) {
        if(entity.isInvisible()) {
            return validTargetLayer2(entity) && targetInvisibles.getValue() && !entity.getDisplayName().getUnformattedText().contains("7kailoras");
        } else {
          return validTargetLayer2(entity) && !entity.getDisplayName().getUnformattedText().contains("7kailoras");
        }
    }

    private boolean validTargetLayer2(EntityLivingBase entity) {
        if(!entity.canEntityBeSeen(Minecraft.getMinecraft().thePlayer)) {
            return validTargetLayer3(entity) && targetThruWalls.getValue();
        } else {
            return validTargetLayer3(entity);
        }
    }

    private boolean validTargetLayer3(EntityLivingBase entity) {
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

    private void sigmaTargetThing(EntityLivingBase target) {

        final float partialTicks = mc.getTimer().renderPartialTicks;

        EntityLivingBase player = target;

        if(target == null) return;

        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);

        if (mc.getRenderManager() == null || player == null) return;

        final double x = player.prevPosX + (player.posX - player.prevPosX) * partialTicks - (mc.getRenderManager()).renderPosX;
        final double y = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + Math.sin(System.currentTimeMillis() / 2E+2) + 1 - (mc.getRenderManager()).renderPosY;
        final double z = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks - (mc.getRenderManager()).renderPosZ;

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDepthMask(false);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (float i = 0; i <= Math.PI * 2 + ((Math.PI * 2) / 32.F); i += (Math.PI * 2) / 32.F) {
            double vecX = x + 0.67 * Math.cos(i);
            double vecZ = z + 0.67 * Math.sin(i);

            ColorUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
            GL11.glVertex3d(vecX, y, vecZ);
        }

        for (float i = 0; i <= Math.PI * 2 + (Math.PI * 2) / 32.F; i += (Math.PI * 2) / 32.F) {
            double vecX = x + 0.67 * Math.cos(i);
            double vecZ = z + 0.67 * Math.sin(i);

            ColorUtil.color(ColorUtil.withAlpha(color, (int) (255 * 0.25)));
            GL11.glVertex3d(vecX, y, vecZ);

            ColorUtil.color(ColorUtil.withAlpha(color, 0));
            GL11.glVertex3d(vecX, y - Math.cos(System.currentTimeMillis() / 2E+2) / 2.0F, vecZ);
        }

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        ColorUtil.glColor(Color.WHITE.getRGB());
    }


    enum BlockMode {
        FAKE, H_V_H, VANILLA, CONTROL, VERUS, WATCHDOG, N_C_P
    }

    enum EventTime {
        EventPreMotion, EventPostMotion, EventRender2D
    }

    enum AttackStage {
        PRE, POST
    }

}
