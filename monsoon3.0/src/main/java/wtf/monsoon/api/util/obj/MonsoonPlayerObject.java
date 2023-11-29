package wtf.monsoon.api.util.obj;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.TargetStrafe;
import wtf.monsoon.impl.module.movement.Sprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class MonsoonPlayerObject {

    private Minecraft mc = Minecraft.getMinecraft();

    private final double WALK_SPEED = 0.221;
    private final List<Double> frictionValues = new ArrayList<>();
    private final double MIN_DIF = 1.0E-2;
    private final double BUNNY_DIV_FRICTION = 160.0D - MIN_DIF;
    private final double SWIM_MOD = 0.115D / WALK_SPEED;
    private final double AIR_FRICTION = 0.98;
    private final double WATER_FRICTION = 0.89;
    private final double LAVA_FRICTION = 0.535;

    private boolean sprintingServerSide;
    private boolean sneakingServerSide;
    private boolean invOpenServerSide;

    private boolean onGroundServerSide;

    private double posXServerSide, posYServerSide, posZServerSide;

    private float yawServerSide, pitchServerSide;

    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if(mc.thePlayer != null && mc.theWorld != null)
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
                baseSpeed *= 1.0D + 0.2D * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return baseSpeed;
    }

    public float getSpeed() { return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ); }

    public boolean isMoving() { return mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F; }

    public boolean isOnGround(float height) { return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty(); }

    public void setSpeed(double speed) {
        float direction = (float) Math.toRadians(MovementUtil.getDirection());

        if (isMoving()) {
            mc.thePlayer.motionX = -Math.sin(direction) * speed;
            mc.thePlayer.motionZ = Math.cos(direction) * speed;
        } else {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        }
    }

    public void setSpeed(double speed, float direction) {
        direction = (float) Math.toRadians(direction);
        if (isMoving()) {
            mc.thePlayer.motionX = -Math.sin(direction) * speed;
            mc.thePlayer.motionZ = Math.cos(direction) * speed;
        } else {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        }
    }

    public void setSpeed(EventMove event, double speed) {
        if(targetStrafeCheck(event, speed)) return;

        float direction = (float) Math.toRadians(MovementUtil.getDirection());
        if (isMoving()) {
            event.setX(mc.thePlayer.motionX = -Math.sin(direction) * speed);
            event.setZ(mc.thePlayer.motionZ = Math.cos(direction) * speed);
        } else {
            event.setX(mc.thePlayer.motionX = 0);
            event.setZ(mc.thePlayer.motionZ = 0);
        }
    }

    public void setSpeed(EventMove event, double speed, float direction) {
        if(targetStrafeCheck(event, speed)) return;

        direction = (float) Math.toRadians(direction);
        if (isMoving()) {
            event.setX(mc.thePlayer.motionX = -Math.sin(direction) * speed);
            event.setZ(mc.thePlayer.motionZ = Math.cos(direction) * speed);
        } else {
            event.setX(mc.thePlayer.motionX = 0);
            event.setZ(mc.thePlayer.motionZ = 0);
        }
    }

    public void setSpeedWithCorrection(EventMove event, double speed, double lastMotionX, double lastMotionZ) {
        setSpeedWithCorrection(event, speed, lastMotionX, lastMotionZ, 0.5);
    }

    public void setSpeedWithCorrection(EventMove event, double speed, double lastMotionX, double lastMotionZ, double modifier) {
        if(targetStrafeCheck(event, speed)) return;

        float direction = (float) Math.toRadians(MovementUtil.getDirection());

        if (isMoving()) {
            event.setX(mc.thePlayer.motionX = -Math.sin(direction) * speed);
            event.setZ(mc.thePlayer.motionZ = Math.cos(direction) * speed);
        } else {
            event.setX(mc.thePlayer.motionX = 0);
            event.setZ(mc.thePlayer.motionZ = 0);
        }

        if(event.getX() > 0 && event.getX() > lastMotionX) {
            event.setX(mc.thePlayer.motionX = (lastMotionX + (event.getX() - lastMotionX) * modifier));
        } else if(event.getX() < 0 && event.getX() < lastMotionX) {
            event.setX(mc.thePlayer.motionX = (lastMotionX - (lastMotionX - event.getX()) * modifier));
        }

        if(event.getZ() > 0 && event.getZ() > lastMotionZ) {
            event.setZ(mc.thePlayer.motionZ = (lastMotionZ + (event.getZ() - lastMotionZ) * modifier));
        } else if(event.getZ() < 0 && event.getZ() < lastMotionZ) {
            event.setZ(mc.thePlayer.motionZ = (lastMotionZ - (lastMotionZ - event.getZ()) * modifier));
        }
    }

    private boolean targetStrafeCheck(EventMove e, double speed) {
        if(Wrapper.getModule(TargetStrafe.class).isEnabled() && Wrapper.getModule(TargetStrafe.class).isStrafing()) {
            Wrapper.getModule(TargetStrafe.class).strafe(e, speed);
            return true;
        }
        return false;
    }

    public int getJumpBoostModifier() {
        PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.jump);
        if (effect != null)
            return effect.getAmplifier() + 1;
        return 0;
    }

    public double getJumpHeight(double baseJumpHeight) {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
            return 0.221 * (0.115D / 0.221) + 0.02F;
        } else if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F) * 0.1F;
        }
        return baseJumpHeight;
    }

    public float getJumpHeight(float baseJumpHeight) {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) {
            return 0.221f * (0.115f / 0.221f) + 0.02F;
        } else if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0F) * 0.1F;
        }
        return baseJumpHeight;
    }

    public void strafe() {
        setSpeed(getSpeed());
    }

    public void strafe(EventMove event) {
        setSpeed(event, getSpeed());
    }

    public boolean isOnGround() {
        return mc.thePlayer.onGround;
    }

    public void setOnGround(boolean onGround) {
        mc.thePlayer.onGround = onGround;
    }

    public void jump(float motionY) {
        mc.thePlayer.motionY = this.getJumpHeight(motionY);
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            mc.thePlayer.motionY += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (mc.thePlayer.isSprinting()) {
            Sprint sprint = Wrapper.getModule(Sprint.class);
            float f = (sprint.isEnabled() && sprint.omni.getValue() ? MovementUtil.getDirection() : mc.thePlayer.rotationYaw) * 0.017453292F;
            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }

        mc.thePlayer.isAirBorne = true;

        mc.thePlayer.triggerAchievement(StatList.jumpStat);

        if (mc.thePlayer.isSprinting()) {
            mc.thePlayer.addExhaustion(0.8F);
        } else {
            mc.thePlayer.addExhaustion(0.2F);
        }
    }

    public void jump() {
        jump(this.getJumpHeight(0.42f));
    }

    public void jump(EventMove eventMove, float motionY) {
        eventMove.setY(mc.thePlayer.motionY = this.getJumpHeight(motionY));
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            eventMove.setY(mc.thePlayer.motionY += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F));
        }

        if (mc.thePlayer.isSprinting()) {
            Sprint sprint = Wrapper.getModule(Sprint.class);
            float f = (sprint.isEnabled() && sprint.omni.getValue() ? MovementUtil.getDirection() : mc.thePlayer.rotationYaw) * 0.017453292F;
            eventMove.setX(mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.2F));
            eventMove.setZ(mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.2F));
        }

        mc.thePlayer.isAirBorne = true;

        mc.thePlayer.triggerAchievement(StatList.jumpStat);

        if (mc.thePlayer.isSprinting()) {
            mc.thePlayer.addExhaustion(0.8F);
        } else {
            mc.thePlayer.addExhaustion(0.2F);
        }
    }

    public void jump(EventMove eventMove) {
        jump(eventMove, this.getJumpHeight(0.42f));
    }

    public void friction(EventMove e, double moveSpeed, double lastDist) {
        frictionValues.clear();
        frictionValues.add(lastDist - (lastDist / BUNNY_DIV_FRICTION));
        frictionValues.add(lastDist - ((moveSpeed - lastDist) / 33.3));
        double materialFriction =
                mc.thePlayer.isInWater() ?
                        WATER_FRICTION :
                        mc.thePlayer.isInLava() ?
                                LAVA_FRICTION :
                                AIR_FRICTION;
        frictionValues.add(lastDist - (this.getBaseMoveSpeed() * (1.0 - materialFriction)));
        this.setSpeed(e, Collections.min(frictionValues));
    }


    @EventLink
    public Listener<EventPreMotion> eventPreMotionListener = e -> {
    };

    @EventLink
    public Listener<EventPacket> eventPacketListener = e -> {
        if(e.getDirection() == EventPacket.Direction.SEND) {

            if(e.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();

                this.setOnGroundServerSide(packet.isOnGround());
                this.setPosXServerSide(packet.getX());
                this.setPosYServerSide(packet.getY());
                this.setPosZServerSide(packet.getZ());
                this.setYawServerSide(packet.getYaw());
                this.setPitchServerSide(packet.getPitch());
            }

            if(e.getPacket() instanceof C0BPacketEntityAction) {
                C0BPacketEntityAction packet = (C0BPacketEntityAction) e.getPacket();
                switch (packet.getAction()) {
                    case START_SPRINTING:
                        setSprintingServerSide(true);
                        break;
                    case STOP_SPRINTING:
                        setSprintingServerSide(false);
                        break;
                    case START_SNEAKING:
                        setSneakingServerSide(true);
                        break;
                    case STOP_SLEEPING:
                        setSneakingServerSide(false);
                        break;
                    case OPEN_INVENTORY:
                        setInvOpenServerSide(true);
                        break;
                }
            }

            if(e.getPacket() instanceof C16PacketClientStatus) {
                C16PacketClientStatus packet = (C16PacketClientStatus) e.getPacket();
                switch (packet.getStatus()) {
                    case OPEN_INVENTORY_ACHIEVEMENT:
                        setInvOpenServerSide(true);
                        break;
                }
            }

            if(e.getPacket() instanceof C0DPacketCloseWindow) {
                C0DPacketCloseWindow packet = (C0DPacketCloseWindow) e.getPacket();
                if(packet.getWindowId() == mc.thePlayer.inventoryContainer.windowId) setInvOpenServerSide(false);
            }
        }
    };

}
