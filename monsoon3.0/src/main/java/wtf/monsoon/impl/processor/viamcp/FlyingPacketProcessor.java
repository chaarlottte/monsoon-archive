package wtf.monsoon.impl.processor.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import viamcp.ViaMCP;
import wtf.monsoon.api.processor.Processor;
import wtf.monsoon.impl.event.EventPacket;

public class FlyingPacketProcessor extends Processor {

    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if (ViaMCP.getInstance().getVersion() > ProtocolVersion.v1_8.getVersion()) {
            if (e.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = ((C03PacketPlayer) e.getPacket());

                if (!packet.isMoving() && !packet.isRotating()) {
                    e.setCancelled(true);
                }
            }
        }
    };

}
