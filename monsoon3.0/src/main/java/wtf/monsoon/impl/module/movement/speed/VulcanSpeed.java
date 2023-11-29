package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

public class VulcanSpeed extends ModeProcessor {

    private boolean reset, prevOnGround;
    private int ticks;
    private double speed;

    private final Setting<Float> groundSpeedModifier = new Setting<>("Boost", 2.05f)
            .minimum(1.0f)
            .maximum(2.5f)
            .incrementation(0.05f);

    public VulcanSpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.getTimer().timerSpeed = 1.0f;
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        float groundModifier = groundSpeedModifier.getValue(), slowerModifier = 0.6498f;
        if(player.isMoving()) {
            if(player.isOnGround()) {
                speed = player.getBaseMoveSpeed() * groundModifier;
                e.setY(mc.thePlayer.motionY = 0.42f);
                prevOnGround = true;
                ticks++;
            } else {
                ticks = 0;
                // speed -= (player.getBaseMoveSpeed() * 0.65);

                // if(prevOnGround)
                if(prevOnGround) {
                    speed *= 0.54;
                    speed += 0.026;
                    prevOnGround = false;
                } else {
                    speed *= 0.91;
                    speed += 0.03 + (player.getBaseMoveSpeed() - 0.2873) * 0.08;
                }

                // speed -= (player.getBaseMoveSpeed() * (groundModifier * slowerModifier));

                if(mc.thePlayer.fallDistance < 1)
                    mc.getTimer().timerSpeed = 1.025f;


                if (e.getY() == 0.08307781780646721) {
                    // e.setY(mc.thePlayer.motionY = -0.08307781780646721);
                    // speed -= (player.getBaseMoveSpeed() * ((groundModifier * slowerModifier) + 10));
                    //prevOnGround = true;
                }
            }

            if (mc.thePlayer.hurtTime == 9) speed += 0.1;
            // player.setSpeed(e, Math.max(speed, player.getSpeed()));
            // PlayerUtil.sendClientMessage("a");
            player.setSpeed(e, Math.max(speed, player.getSpeed()));

            if(prevOnGround && !mc.thePlayer.onGround)
                prevOnGround = false;
        } else player.setSpeed(e, 0);
    };

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {

    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        /*if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                e.setCancelled(true);
                double motionX = packet.getMotionX() / 8000.0D;
                double motionY = packet.getMotionY() / 8000.0D;
                double motionZ = packet.getMotionZ() / 8000.0D;
                double penis = Math.sqrt(motionX * motionX + motionZ * motionZ);
                mc.thePlayer.motionY = motionY;
                this.speed = penis;
                player.setSpeed(this.speed);
            }
        }*/
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { groundSpeedModifier };
    }
}
