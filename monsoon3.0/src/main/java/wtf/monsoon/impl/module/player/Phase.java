package wtf.monsoon.impl.module.player;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPreMotion;

public class Phase extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.VCLIP)
            .describedBy("The mode of the scaffold.");

    public Phase() {
        super("Phase", "Phase through blocks.", Category.PLAYER);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (mode.getValue()) {
            case PACKET:
                double strength = 0.6d;
                mc.thePlayer.stepHeight = 0;
                double mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                double mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                double x = (double) mc.thePlayer.movementInput.moveForward * strength * mx + (double) mc.thePlayer.movementInput.moveStrafe * strength * mz;
                double z = (double) mc.thePlayer.movementInput.moveForward * strength * mz - (double) mc.thePlayer.movementInput.moveStrafe * strength * mx;

                if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder()) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ, false));
                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                }
                break;
            case PACKETLESS:
                mc.getTimer().timerSpeed = 1;
                if (mc.thePlayer.isCollidedHorizontally && player.isMoving() && mc.thePlayer.onGround) {
                    strength = 0.545d;
                    mc.thePlayer.stepHeight = 0;
                    mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                    mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
                    x = (double) mc.thePlayer.movementInput.moveForward * strength * mx + (double) mc.thePlayer.movementInput.moveStrafe * strength * mz;
                    z = (double) mc.thePlayer.movementInput.moveForward * strength * mz - (double) mc.thePlayer.movementInput.moveStrafe * strength * mx;
                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY - 0.07, mc.thePlayer.posZ + z);
                }
                break;
            case VCLIP:
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 4, mc.thePlayer.posZ);
                this.toggle();
                break;
        }
    };

    private enum Mode {
        VCLIP,
        PACKET,
        PACKETLESS
    }

}
