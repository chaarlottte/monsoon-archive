package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.luaj.vm2.ast.Str;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnpatchableAirjumpFlight extends ModeProcessor {


    public UnpatchableAirjumpFlight(Module parentModule) {
        super(parentModule);
    }

    private boolean startedDamage = false;
    private boolean damaged = false;
    private boolean jumped = false;
    private boolean jumpedAgain = false;
    private boolean hold = false;

    private double fallY = 0.0;

    private final Map<S12PacketEntityVelocity, List<Packet<?>>> map = new HashMap<>();
    private S12PacketEntityVelocity key = null;

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
        this.fallY = mc.thePlayer.posY;
    }

    private void reset() {
        this.startedDamage = false;
        this.damaged = false;
        this.jumped = false;
        this.jumpedAgain = false;
        this.hold = false;
        this.fallY = mc.thePlayer.posY;
        this.key = null;
        this.map.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.getTimer().timerSpeed = 1.0f;
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if(mc.thePlayer.ticksExisted <= 5) {
            this.reset();
            return;
        }
        if (!this.startedDamage) {
            // damage to receive velocity packets
            boolean customDamage = false;
            if (customDamage) {
                // this.selfDamage = new SelfDamageObject(SelfDamageObject.DamageType.JUMP);
                // this.selfDamage.start();
            } else {
                PlayerUtil.oldNCPDamage();
                this.damaged = true;
            }
            this.startedDamage = true;
        } else if (this.startedDamage && !this.damaged) {
            // this.damaged = this.selfDamage.isReady();
            PlayerUtil.sendClientMessage("damaged: " + this.damaged);
        } else if (!this.jumped && this.hold) {
            // initial jump after receiving velocity
            mc.thePlayer.jump();
            this.jumped = true;
        } else {
            // when in air
            if (mc.thePlayer.motionY < this.fallY && mc.thePlayer.motionY < 0 && this.hold && this.key != null) {
                if(!this.jumpedAgain && this.map.containsKey(this.key)) {
                    this.hold = false;
                    PlayerUtil.sendClientMessage("ready to jump again");
                    mc.thePlayer.jump();
                    // if you're at the same Y as when you jumped, process velocity to "jump" again
                    for (Packet<?> packet : this.map.get(this.key)) {

                        if (PacketUtil.isInbound(packet)) {
                            PacketUtil.processPacket((Packet<INetHandler>) packet);
                            if(packet instanceof S12PacketEntityVelocity) {
                                PlayerUtil.sendClientMessage("processed S12!!!");
                            } else {
                                // PlayerUtil.sendClientMessage("processed inbound packet");
                            }
                        } else if (PacketUtil.isOutbound(packet)) {
                            PacketUtil.sendPacketNoEvent(packet);
                            // PlayerUtil.sendClientMessage("processed outbound packet");
                        }
                    }
                    this.jumpedAgain = true;
                }
            }

            if(this.jumpedAgain && mc.thePlayer.onGround)
                this.getParentModule().toggle();
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacket = e -> {
        if(mc.thePlayer.ticksExisted <= 5) {
            this.reset();
            return;
        }
        if (e.getDirection() == EventPacket.Direction.RECEIVE) {
            if (e.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                // packet.setMotionY(Math.abs(packet.getMotionY()) * 100);
                PlayerUtil.sendClientMessage(packet.getMotionX() + " " + packet.getMotionY() + " " + packet.getMotionZ());
                this.key = packet;
                this.map.put(this.key, new ArrayList<>());
                this.map.get(this.key).add(e.getPacket());
                e.cancel();

                if (this.key != null) {
                    this.hold = true;
                }
            } else {
                if (this.hold && this.key != null) {
                    this.map.get(this.key).add(e.getPacket());
                    e.cancel();
                }
            }
        } else {
            if (this.hold && this.key != null) {
                this.map.get(this.key).add(e.getPacket());
                e.cancel();
            }
        }
    };
}
