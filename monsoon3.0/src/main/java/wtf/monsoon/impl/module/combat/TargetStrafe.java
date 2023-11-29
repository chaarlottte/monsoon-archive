package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.module.movement.Flight;
import wtf.monsoon.impl.module.movement.Speed;

import java.awt.*;

public class TargetStrafe extends Module {

    public int direction = 1;
    public boolean canMove;
    public double movespeed2, forward, strafe;

    public float currentYaw;

    public Timer timer = new Timer();

    @Getter @Setter
    private boolean strafing;

    public Setting<Shape> shape = new Setting<Shape>("Shape", Shape.CIRCLE)
            .describedBy("Only speed");

    public Setting<Boolean> onlySpeed = new Setting<>("Only Speed", false)
            .describedBy("Only speed");

    public Setting<Double> range = new Setting<>("Range", 2D)
            .minimum(0.1D)
            .maximum(6D)
            .incrementation(0.1D)
            .describedBy("Range");

    public TargetStrafe() {
        super("Target Strafe", "Automatically strafe your opponents.", Category.COMBAT);
    }

    public void onEnable() {
        super.onEnable();
        this.setStrafing(false);
    }

    public void onDisable() {
        super.onDisable();
        this.setStrafing(false);
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        Aura aura = Wrapper.getModule(Aura.class);
        if (mc.gameSettings.keyBindJump.isKeyDown() && aura.isEnabled() && aura.getTarget() != null && !aura.getTarget().isDead) {
            if (aura.getTarget() == null) {
                return;
            }
            float[] rotations = getRotationsEntity(aura.getTarget());
            movespeed2 = getBaseMoveSpeed();
            if (aura.getTarget().getDistanceToEntity(mc.thePlayer) < range.getValue()) {
                if (Wrapper.getModule(Flight.class).isEnabled()) {
                    return;
                } else {
                    if (mc.gameSettings.keyBindRight.isPressed()) {
                        direction = -1;
                    }
                    if (mc.gameSettings.keyBindLeft.isPressed()) {
                        direction = 1;
                    }
                    canMove = true;
                }
                if (!aura.getTarget().isDead)
                    canMove = false;

                if (onlySpeed.getValue()) {
                    if (Wrapper.getModule(Speed.class).isEnabled()) {
                        if (mc.gameSettings.keyBindRight.isPressed()) {
                            direction = -1;
                        }
                        if (mc.gameSettings.keyBindLeft.isPressed()) {
                            direction = 1;
                        }
                        canMove = true;
                        if (Wrapper.getModule(Speed.class).isEnabled()) {
                            if (direction == -1 || direction == 1) {
                                //mc.gameSettings.keyBindJump.pressed = true;
                            }
                        }
                    } else {
                        return;
                    }
                }

                if (mc.gameSettings.keyBindRight.isPressed()) {
                    direction = -1;
                }
                if (mc.gameSettings.keyBindLeft.isPressed()) {
                    direction = 1;
                }

                canMove = true;
                if (Wrapper.getModule(Speed.class).isEnabled()) {
                    if (direction == -1 || direction == 1) {
                        //mc.gameSettings.keyBindJump.pressed = true;
                    }
                }

                if (canMove) {
                    this.currentYaw = rotations[0];
                    this.forward = 0.0;
                    this.strafe = direction;
                    this.strafe(e, movespeed2);
                    this.setStrafing(true);
                } else {
                    currentYaw = mc.thePlayer.rotationYaw;
                }
            } else {
                if (mc.gameSettings.keyBindJump.isKeyDown() && aura.getTarget().getDistanceToEntity(mc.thePlayer) < aura.getRange().getValue()) {
                    this.currentYaw = rotations[0];
                    this.forward = 1.0;
                    this.strafe = direction;
                    this.strafe(e, movespeed2);
                    this.setStrafing(true);
                } else {
                    currentYaw = mc.thePlayer.rotationYaw;
                    this.setStrafing(false);
                }
                canMove = false;
            }
        } else this.setStrafing(false);
    };

    @EventLink
    public final Listener<EventRender3D> eventRender3dListener = e -> {
        Aura aura = Wrapper.getModule(Aura.class);
        if (aura.getTarget() != null && aura.getTarget().getDistanceToEntity(mc.thePlayer) < aura.getRange().getValue() && aura.isEnabled() && !aura.getTarget().isDead) {
            switch (shape.getValue()) {
                case CIRCLE:
                    // RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), Color.BLACK, 3.7f, 1);
                    // RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), (aura.getTarget().hurtTime > 0) ? new Color(ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme()[0].getRGB(), ColorUtil.getClientAccentTheme()[1].getRGB(), System.currentTimeMillis() % 1500 / (1500 / 2.0f))) : Color.WHITE, 3f, 1);
                    RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), Color.WHITE, 1f, 1);
                    break;
                case DECAGON:
                    // RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), Color.BLACK, 3.7f, 9);
                    // RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), (aura.getTarget().hurtTime > 0) ? new Color(ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme()[0].getRGB(), ColorUtil.getClientAccentTheme()[1].getRGB(), System.currentTimeMillis() % 1500 / (1500 / 2.0f))) : Color.WHITE, 3f, 9);
                    RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), Color.BLACK, 3.7f, 9);
                    RenderUtil.drawCircle(aura.getTarget(), e.getPartialTicks(), range.getValue(), Color.WHITE, 1f, 9);
                    break;
            }
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {

    };


    public double getBaseMoveSpeed() {
        return player.getSpeed();
    }


    public void strafe(EventMove event, double moveSpeed) {
        double fow = forward;
        double stra = strafe;
        float ya = currentYaw;
        if (fow != 0.0D) {
            if (strafe > 0.0D) {
                ya += ((fow > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                ya += ((fow > 0.0D) ? 45 : -45);
            }
            stra = 0.0D;
            if (fow > 0.0D) {
                fow = 1.0D;
            } else if (fow < 0.0D) {
                fow = -1.0D;
            }
        }
        if (stra > 1.0D) {
            stra = 1.0D;
        } else if (stra < 0.0D) {
            stra = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((ya + 90.0F)));
        double mz = Math.sin(Math.toRadians((ya + 90.0F)));
        event.setX(mc.thePlayer.motionX = (fow * moveSpeed * mx + stra * moveSpeed * mz));
        event.setZ(mc.thePlayer.motionZ = fow * moveSpeed * mz - stra * moveSpeed * mx);
    }

    public float[] getRotationsEntity(EntityLivingBase entity) {
        return getRotations(entity.posX + randomNumber(0.03D, -0.03D), entity.posY + entity.getEyeHeight() - 0.4D + randomNumber(0.07D, -0.07D), entity.posZ + randomNumber(0.03D, -0.03D));
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public float[] getRotations(double posX, double posY, double posZ) {
        EntityLivingBase player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - player.posY + player.getEyeHeight();
        double z = posZ - player.posZ;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
        return new float[]{yaw, pitch};
    }

    enum Shape {
        CIRCLE, DECAGON
    }

}
