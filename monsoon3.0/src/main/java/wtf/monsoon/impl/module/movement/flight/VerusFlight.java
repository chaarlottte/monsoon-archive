package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventBlockCollide;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.movement.Flight;

public class VerusFlight extends ModeProcessor {

    private final Setting<VerusMode> verusMode = new Setting<>("Verus Mode", VerusMode.AIRWALK)
            .describedBy("The mode of the Verus flight.");

    private final Setting<Boolean> fastVerus = new Setting<>("Fast", false)
            .describedBy("Whether to go faster on Verus.")
            .visibleWhen(() -> verusMode.getValue() == VerusMode.BRUE);

    private int ticks;

    private Timer timer = new Timer();

    public VerusFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (verusMode.getValue() == VerusMode.BRUE) {
            ticks = 0;
            player.setSpeed(0);
            mc.thePlayer.setVelocity(0, 0, 0);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setVelocity(0, 0, 0);
        player.setSpeed(0);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (verusMode.getValue()) {
            case AIRWALK:
                player.setOnGround(true);
                e.setOnGround(mc.thePlayer.ticksExisted % 2 == 0);
                mc.thePlayer.motionY = 0;
                e.setY(Math.round(mc.thePlayer.posY));
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.prevPosX, mc.thePlayer.posY - 1, mc.thePlayer.prevPosZ), 1, new ItemStack(Blocks.stone), 1, 1, 1));
                break;
            case BRUE:
                break;
        }
    };

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
            switch (verusMode.getValue()) {
                case BRUE:
                    if (ticks % 14 == 0 && player.isOnGround()) {
                        player.setSpeed(e, 1);
                        e.setY(0.42F);
                        mc.thePlayer.motionY = -(mc.thePlayer.posY - (mc.thePlayer.posY - (mc.thePlayer.posY % 0.015625)));
                    } else {
                        if (fastVerus.getValue()) {
                            if (player.isOnGround()) {
                                player.setSpeed(e, player.getBaseMoveSpeed() * 2.65f);
                            } else player.setSpeed(e, 0.41);
                        } else {
                            if (player.isOnGround()) {
                                if (mc.thePlayer.moveStrafing == 0) {
                                    float multiplier = (float) (1.0f + (mc.thePlayer.motionY < 0f ? mc.thePlayer.motionY * -6f : mc.thePlayer.motionY * 6f));
                                    if (mc.thePlayer.hurtTime > 0)
                                        player.setSpeed(e, player.getBaseMoveSpeed() * 1.7f);
                                    else player.setSpeed(e, player.getBaseMoveSpeed() * multiplier);
                                } else {
                                    player.setSpeed(e, player.getSpeed() * 0.9f);
                                }
                            } else player.setSpeed(e, 0.41);
                        }
                    }
                    ticks++;
                    break;
                case AIRWALK:
                    break;
            }
    };

    @EventLink
    public final Listener<EventBlockCollide> eventBlockCollideListener = e -> {
        switch (verusMode.getValue()) {
            case BRUE:
                if (e.getBlock() instanceof BlockAir && !mc.thePlayer.isSneaking()) {
                    final double x = e.getX(), y = e.getY(), z = e.getZ();

                    if (y < mc.thePlayer.posY) {
                        e.setCollisionBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
                    }
                }
                break;

            case AIRWALK:
                break;
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { verusMode, fastVerus };
    }

    private enum VerusMode {
        BRUE,
        AIRWALK
    }

}
