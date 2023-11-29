package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPreMotion;

public class PacketFlight extends ModeProcessor {


    public PacketFlight(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -Math.sin(yaw) * 0.27f;
        double z = Math.cos(yaw) * 0.27f;
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
        mc.thePlayer.motionY = 0.0;
        mc.getTimer().timerSpeed = 1.1f;
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.motionY , mc.thePlayer.motionZ + z, false));
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.motionY - 490, mc.thePlayer.motionZ + z, true));
        mc.thePlayer.posX += x;
        mc.thePlayer.posZ += z;
    };


}
