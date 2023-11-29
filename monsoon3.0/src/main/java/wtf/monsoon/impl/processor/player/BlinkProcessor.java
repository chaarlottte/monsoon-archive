package wtf.monsoon.impl.processor.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import wtf.monsoon.api.processor.Processor;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class BlinkProcessor extends Processor {

    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    @Setter private boolean blinking, dispatch;

    @EventLink
    public final Listener<EventPacket> onPacketSend = event -> {

        if (mc.thePlayer == null) {
            this.packets.clear();
            return;
        }

        if (mc.thePlayer.isDead || mc.isSingleplayer()) {
            this.packets.forEach(PacketUtil::sendPacketNoEvent);
            this.packets.clear();
            this.blinking = false;
            return;
        }

        final Packet<?> packet = event.getPacket();

        if(packet instanceof C03PacketPlayer) {
            if(this.blinking && !this.dispatch) {
                this.packets.add(packet);
                event.setCancelled(true);
            } else {
                this.packets.forEach(PacketUtil::sendPacketNoEvent);
                this.packets.clear();
                this.dispatch = false;
            }
        }
    };

    public void dispatch() {
        this.dispatch = true;
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = event -> {
        if(mc.thePlayer.ticksExisted <= 1) {
            packets.clear();
            this.blinking = false;
        }
    };

}
