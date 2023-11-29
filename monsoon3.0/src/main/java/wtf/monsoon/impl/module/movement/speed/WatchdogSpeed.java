package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockSlab;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventStrafing;
import wtf.monsoon.impl.module.combat.Velocity;
import wtf.monsoon.impl.module.movement.Speed;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class WatchdogSpeed extends ModeProcessor {

    private final Setting<String> wdOptions = new Setting<String>("Watchdog Options", "Watchdog Options")
            .describedBy("Watchdog options.");

    public final Setting<WatchdogMode> watchdogMode = new Setting<>("Watchdog Mode", WatchdogMode.HOP)
            .describedBy("How to control speed on Hypixel")
            .childOf(wdOptions);

    private final Setting<Double> speedWatchdog = new Setting<Double>("Speed", 1.7)
            .minimum(1D)
            .maximum(2D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.")
            .visibleWhen(() -> watchdogMode.getValue() == WatchdogMode.HOP)
            .childOf(wdOptions);

    private final Setting<Boolean> safeStrafe = new Setting<>("Safe Strafe", false)
            .describedBy("Whether to enable safe strafe.")
            .visibleWhen(() -> watchdogMode.getValue() == WatchdogMode.HOP)
            .childOf(wdOptions);

    private final Setting<Double> strafeModifier = new Setting<Double>("Strafe Modifier", 0.5)
            .minimum(0.1D)
            .maximum(1.0D)
            .incrementation(0.05D)
            .describedBy("The amount to modify strafe.")
            .visibleWhen(() -> (watchdogMode.getValue() == WatchdogMode.HOP && safeStrafe.getValue()) || watchdogMode.getValue() == WatchdogMode.HOP_SMOOTH || watchdogMode.getValue() == WatchdogMode.TEST)
            .childOf(wdOptions);

    private final Setting<Double> speedNoStrafe = new Setting<>("Speed (nostrafe)", 1.95)
            .minimum(1.5D)
            .maximum(2.25D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.")
            .visibleWhen(() -> watchdogMode.getValue() == WatchdogMode.NO_STRAFE || watchdogMode.getValue() == WatchdogMode.DORT)
            .childOf(wdOptions);

    private final Setting<Boolean> strafeOnDamage = new Setting<>("Strafe on Damage", true)
            .describedBy("Strafe on damage")
            .visibleWhen(() -> watchdogMode.getValue() == WatchdogMode.NO_STRAFE)
            .childOf(wdOptions);

    private final Setting<Float> timerSpeed = new Setting<>("Strafe Ticks", 2.0f)
            .minimum(1.0f)
            .maximum(3.0f)
            .incrementation(0.05f)
            .describedBy("Timer speed of the strafe")
            .visibleWhen(() -> watchdogMode.getValue() == WatchdogMode.TEST)
            .childOf(wdOptions);


    public WatchdogSpeed(Module parentModule) {
        super(parentModule);
    }

    private double speed, lastDist, baseSpeed, boostSpeed;
    private int strafeTicks, stage;

    private boolean prevOnGround, canStrafe;
    private double lastMotionX, lastMotionZ;

    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();

    private boolean isGrounded = false;
    private int increment = 0;

    @Override
    public void onEnable() {
        super.onEnable();

        lastMotionX = mc.thePlayer.motionX;
        lastMotionZ = mc.thePlayer.motionZ;
        canStrafe = false;
        strafeTicks = 999;
        stage = 0;
        baseSpeed = player.getBaseMoveSpeed();
        this.isGrounded = false;
        this.increment = 0;
        this.packets.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.getTimer().timerSpeed = 1.0f;
        mc.thePlayer.jumpMovementFactor  = 0.02F;

        this.packets.forEach(PacketUtil::sendPacketNoEvent);
        this.packets.clear();
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (watchdogMode.getValue()) {
            case GROUND:
                if(player.isOnGround() && !(mc.thePlayer.ticksExisted % 4 == 0) &&  !(mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, -1, 0)).getBlock() instanceof BlockSlab)) mc.getTimer().timerSpeed = 4.0f;
                else mc.getTimer().timerSpeed = 1.0f;
                break;
            case HOP:
            case NO_STRAFE:
            case DORT:
            case TEST:
                double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
                strafeTicks++;
                break;
        }
        if(mc.thePlayer.ticksExisted <= 5)
            this.packets.clear();
    };

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
            switch (watchdogMode.getValue()) {
                case HOP:
                    if (player.isMoving()) {
                        speed = player.getBaseMoveSpeed();
                        if (mc.thePlayer.onGround && !prevOnGround) {
                            prevOnGround = true;
                            e.setY(0.41999998688698);
                            mc.thePlayer.motionY = 0.42;
                            speed *= speedWatchdog.getValue();
                        } else if (prevOnGround) {
                            double difference = (0.76D * (lastDist - player.getBaseMoveSpeed()));
                            speed = lastDist - difference;
                            prevOnGround = false;
                        } else {
                            speed = lastDist - lastDist / 159D;
                        }

                        if (mc.thePlayer.hurtTime > 0) {
                            speed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) + 0.0245F;
                        }

                        if (safeStrafe.getValue() && !player.isOnGround()) {
                            player.setSpeedWithCorrection(e, Math.max(player.getSpeed(), speed), lastMotionX, lastMotionZ, strafeModifier.getValue());
                        } else {
                            player.setSpeed(e, Math.max(player.getSpeed(), speed));
                        }

                        lastMotionX = e.getX();
                        lastMotionZ = e.getZ();
                    }
                    break;

                case NO_STRAFE:
                    /*if (player.isMoving()) {
                        speed = player.getBaseMoveSpeed();
                        if (mc.thePlayer.onGround && !prevOnGround) {
                            prevOnGround = true;
                            e.setY(player.getJumpHeight(0.41999998688698));
                            mc.thePlayer.motionY = player.getJumpHeight(0.42);
                            if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) speed *= (speedNoStrafe.getValue() * 0.8);
                            else speed *= speedNoStrafe.getValue();
                            canStrafe = true;
                        } else if (prevOnGround) {
                            double difference = (0.76D * (lastDist - player.getBaseMoveSpeed()));
                            speed = lastDist - difference;
                            canStrafe = true;
                            prevOnGround = false;
                        } else {
                            canStrafe = false;
                            speed = lastDist - lastDist / 159D;
                        }

                        if (mc.thePlayer.hurtTime > 0) {
                            speed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) + 0.0245F;
                        }

                        if(canStrafe) {
                            player.setSpeed(e, Math.max(player.getSpeed(), speed));
                        } else if(strafeTicks <= 18) {
                            if(strafeTicks <= 6) player.setSpeed(e, Math.max(this.boostSpeed, Math.max(player.getBaseMoveSpeed(), speed)));
                            else {
                                player.setSpeed(e, Math.max(player.getSpeed(), speed));
                            }
                        }

                        canStrafe = false;

                        lastMotionX = e.getX();
                        lastMotionZ = e.getZ();
                    } else {
                        player.setSpeed(e, 0);
                    }*/

                    if (player.isMoving()) {
                        if (mc.thePlayer.onGround && !prevOnGround) {
                            speed = player.getBaseMoveSpeed();
                            prevOnGround = true;
                            e.setY(0.41999998688698);
                            mc.thePlayer.motionY = 0.42;
                            if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) speed *= (speedNoStrafe.getValue() * 0.85);
                            else speed *= speedNoStrafe.getValue();
                        } else if (prevOnGround) {
                            double difference = (0.76D * (lastDist - player.getBaseMoveSpeed()));
                            speed = lastDist - difference;
                        } else {
                            speed = lastDist - lastDist / 159D;
                        }

                        if (mc.thePlayer.hurtTime > 0)
                            speed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ) + 0.0245F;


                        if(mc.thePlayer.onGround || prevOnGround)
                            player.setSpeed(e, Math.max(player.getSpeed(), speed));

                        lastMotionX = e.getX();
                        lastMotionZ = e.getZ();
                        if(this.prevOnGround && !mc.thePlayer.onGround)
                            prevOnGround = false;
                    } else {
                        player.setSpeed(e, 0);
                    }
                    break;
                case HOP_SMOOTH:
                    if (player.isMoving()) {
                        if (player.isOnGround()) {
                            prevOnGround = true;
                            if (player.isMoving()) {
                                e.setY(0.41999998688698);
                                mc.thePlayer.motionY = 0.42;
                                speed *= 0.91;
                                speed += 0.2 + mc.thePlayer.getAIMoveSpeed();
                            }
                        } else if (prevOnGround) {
                            speed *= 0.54;
                            speed += 0.026;
                            prevOnGround = false;
                        } else {
                            speed *= 0.91;
                            speed += 0.025 + (player.getBaseMoveSpeed() - 0.2873) * 0.08;
                        }

                        if (mc.thePlayer.hurtTime == 9) speed += 0.1;

                        if (mc.thePlayer.fallDistance < 1) {
                            player.setSpeedWithCorrection(e, speed, lastMotionX, lastMotionZ, strafeModifier.getValue());
                        } else {
                            speed = player.getSpeed();
                        }
                    }

                    lastMotionX = e.getX();
                    lastMotionZ = e.getZ();
                    break;
                case TEST:
                    if(player.isMoving()) {
                        speed = player.getBaseMoveSpeed();
                        if(mc.thePlayer.onGround) {
                            player.jump(e);
                        }
                        player.strafe(e);
                    }
                    break;
                case DORT:
                    double base = player.getBaseMoveSpeed();
                    if (player.isMoving()) {
                        if (mc.thePlayer.onGround) {
                            e.setY(mc.thePlayer.motionY = 0.42F);
                            player.setSpeed(e, base * 1.7);
                        } else {
                            if (mc.thePlayer.hurtTime > 0) {
                                player.friction(e, player.getSpeed() * 1.06125f, lastDist);
                                stage = 10;
                            }
                            if (stage > 0) {
                                player.setSpeed(e, Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ));
                                stage--;
                            }
                            // Disabler disabler = Client.singleton().moduleManager().getByClass(Disabler.class);
                            // if (disabler.isEnabled()) MovementUtil.handleWatchdogStrafing();
                        }
                    }
                    break;
            }
    };

    @EventLink
    public final Listener<EventStrafing> eventStrafingListener = e -> {
        switch (this.watchdogMode.getValue()) {
            case TEST:
                break;
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                strafeTicks = 0;
                e.setCancelled(true);
                double motionX = packet.getMotionX() / 8000.0D;
                double motionY = packet.getMotionY() / 8000.0D;
                double motionZ = packet.getMotionZ() / 8000.0D;
                double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
                mc.thePlayer.motionY = motionY;
                this.boostSpeed = Math.min(speed, 0.5);
                player.setSpeed(this.boostSpeed);
            }
        }

        if(this.watchdogMode.getValue() == WatchdogMode.TEST) {
            if(e.getPacket() instanceof C03PacketPlayer) {
                if (this.isGrounded) {
                    //this.packets.add(e.getPacket());
                    //e.setCancelled(true);
                }
            }
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { wdOptions };
    }

    private enum WatchdogMode {
        HOP, HOP_SMOOTH, DORT, GROUND, NO_STRAFE, TEST
    }
}
