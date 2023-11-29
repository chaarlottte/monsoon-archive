package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventBlockCollide;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Speed;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class VulcanFlight extends ModeProcessor {

    @Getter
    private final Setting<Mode> mode = new Setting<>("Vulcan Mode", Mode.FAST)
            .describedBy("How to control flight");

    private final Setting<Float> speed = new Setting<>("Vulcan Speed", 0.77f)
            .minimum(0.1f)
            .maximum(1.0f)
            .incrementation(0.01f)
            .describedBy("The speed to go");

    private final Setting<Integer> iterationCount = new Setting<>("Iteration Count", 8)
            .minimum(1)
            .maximum(20)
            .incrementation(1)
            .describedBy("Iterations to release")
            .visibleWhen(() -> mode.getValue() == Mode.FAST);

    private int ticks, vticks, stage = 0;

    private Timer timer = new Timer();

    private double startX, startY, startZ;

    private double lastTickX, lastTickY, lastTickZ;
    private double lastSentX, lastSentY, lastSentZ;
    private double lastMotionX, getLastMotionZ;
    private float startYaw, startPitch;
    private boolean waitFlag = false, started = false, damaged = false, playedFakeDmg = false;
    private int jumps = 0;

    public VulcanFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        playedFakeDmg = false;
        switch (mode.getValue()) {
            case AIRWALK:
                startX = mc.thePlayer.posX;
                startY = mc.thePlayer.posY;
                startZ = mc.thePlayer.posZ;
                ticks = 0;
                // mc.thePlayer.jump();
                break;
            case TEST:
                if (mc.thePlayer.onGround)
                    mc.thePlayer.motionY = 0.42f;
                break;
            case FAST:
                this.ticks = 0;
                this.waitFlag = false;
                this.started = false;
                this.damaged = false;
                this.jumps = 0;
                this.startYaw = mc.thePlayer.rotationYaw;
                this.startPitch = mc.thePlayer.rotationPitch;
                mc.getTimer().timerSpeed = 1.0f;
                this.damage();
                break;
            case GLIDE:
                this.startY = mc.thePlayer.posY;
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ);
                break;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        player.setSpeed(0.0);
        mc.thePlayer.setVelocity(0, 0,0);
        mc.getTimer().timerSpeed = 1f;

        switch (mode.getValue()) {
            case AIRWALK:
                mc.thePlayer.motionY = -0.09800000190735147;
                break;
            case FAST:
                // PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.lastTickX, this.lastTickY, this.lastTickZ, false));
                break;
        }
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (mode.getValue()) {
            case AIRWALK:
                ticks++;
                if (ticks >= 4) {
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(e.getY() / 0.5) * 0.5, mc.thePlayer.posZ);
                    player.setSpeed(0.2694 * (1 + speed.getValue()));
                }
                if ((ticks <= 20 && ticks >= 0 && ticks >= 4) || mc.thePlayer.posY % 0.5 == 0.0) {
                    double mathGround2 = Math.round(e.getY() / 0.015625) * 0.015625;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mathGround2, mc.thePlayer.posZ);
                    e.setY(mathGround2);
                    // e.setOnGround(mc.thePlayer.ticksExisted % 4 == 0);
                }
                mc.thePlayer.cameraYaw = 0.1f;
                break;
            case TEST:
                e.setOnGround(true);
                break;
            case FAST:
                if(this.damage()) return;

                e.setYaw(this.startYaw);
                e.setPitch(this.startPitch);

                mc.thePlayer.jumpMovementFactor = 0.00f;
                if (!this.started && !this.waitFlag) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.0784, mc.thePlayer.posZ, 0f, 0f, false));
                    // e.setY(mc.thePlayer.posY - 0.0784);
                    PlayerUtil.sendClientMessage("waiting for flag");
                    this.waitFlag = true;
                }

                if(this.started) {
                    mc.getTimer().timerSpeed = 1.0f;
                    if (!mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.thePlayer.motionY = speed.getValue() * 2;
                        }
                    }

                    this.ticks++;
                    if(this.ticks > 4 ) {
                        this.ticks = 4;
                    }

                    mc.getTimer().timerSpeed = 1.2f;
                    player.setSpeed(speed.getValue() * 2);
                }
                break;
            case GLIDE:
                if (mc.thePlayer.posY > this.startY) {
                    if (mc.thePlayer.fallDistance > 2) {
                        e.setOnGround(true);
                        mc.thePlayer.fallDistance = 0;
                    }
                    if (mc.thePlayer.ticksExisted % 3 != 0)
                        mc.thePlayer.motionY = -0.0991;
                    else
                        mc.thePlayer.motionY += 0.026;
                } else {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ);
                }
                break;
        }
    };

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        switch (mode.getValue()) {
            case AIRWALK:
                break;
            case TEST:
                mc.getTimer().timerSpeed = 0.1f;
                e.setY(mc.thePlayer.motionY = 0);
                player.setSpeed(e, player.getBaseMoveSpeed() * 7);
                break;
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        switch (mode.getValue()) {
            case AIRWALK:
                if(e.getPacket() instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();

                    if(mc.thePlayer.ticksExisted > 20 && !mc.isSingleplayer()) {
                        if (Math.abs(packet.getX() - startX) + Math.abs(packet.getY() - startY) + Math.abs(packet.getZ() - startZ) < 4.0) {
                            e.setCancelled(true);
                        }
                    }
                }
                break;
            case TEST:
                if (e.getPacket() instanceof C03PacketPlayer) {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        e.setCancelled(true);
                    }
                }
                if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                    this.getParentModule().toggle();
                }
                break;
            case FAST:
                if(e.getPacket() instanceof C03PacketPlayer && this.waitFlag)
                    e.setCancelled(true);

                if (this.started) {
                    if(e.getPacket() instanceof C03PacketPlayer && !(e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)) {
                        C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
                        double deltaX = packet.x - this.lastSentX;
                        double deltaY = packet.y - this.lastSentY;
                        double deltaZ = packet.z - this.lastSentZ;
                        double sqrt = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                        if (sqrt > iterationCount.getValue()) {
                            PlayerUtil.sendClientMessage(sqrt + "");
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.lastTickX, this.lastTickY, this.lastTickZ, false));
                            this.lastSentX = lastTickX;
                            this.lastSentY = lastTickY;
                            this.lastSentZ = lastTickZ;
                        } else {
                            e.setCancelled(true);
                        }
                        this.lastTickX = packet.x;
                        this.lastTickY = packet.y;
                        this.lastTickZ = packet.z;
                    } else if(e.getPacket() instanceof C03PacketPlayer) {
                        e.setCancelled(true);
                    }
                }

                if(e.getPacket() instanceof S08PacketPlayerPosLook && this.waitFlag) {
                    PlayerUtil.sendClientMessage("flagged");
                    S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
                    this.lastSentX = packet.getX();
                    this.lastSentY = packet.getY();
                    this.lastSentZ = packet.getZ();
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
                    mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
                    e.setCancelled(true);
                    this.started = true;
                    this.waitFlag = false;
                }

                if(e.getPacket() instanceof C0FPacketConfirmTransaction) {
                    C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction) e.getPacket();
                    if(packet.uid > -31767 && packet.uid <= -30769) {
                        e.setCancelled(true);
                        PacketUtil.sendPacketNoEvent(packet);
                    }
                }
                break;
        }
    };

    @EventLink
    public final Listener<EventBlockCollide> eventBlockCollideListener = e -> {

    };

    private boolean damage() {
        mc.getTimer().timerSpeed = 1.0f;
        if (this.damaged) {
            this.jumps = 999;
            if(!this.playedFakeDmg) {
                PlayerUtil.fakeDamage();
                this.playedFakeDmg = true;
            }
            return false;
        }
        mc.thePlayer.jumpMovementFactor = 0.00f;
        if (mc.thePlayer.onGround) {
            if (this.jumps >= 4) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                this.damaged = true;
                this.jumps = 999;
                if(!this.playedFakeDmg) {
                    PlayerUtil.fakeDamage();
                    this.playedFakeDmg = true;
                }
                return false;
            }
            this.jumps++;
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
            mc.thePlayer.jump();
        }
        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        return true;
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { mode, speed };
    }

    enum Mode {
        GLIDE,
        FAST,
        AIRWALK,
        TEST
    }

}
