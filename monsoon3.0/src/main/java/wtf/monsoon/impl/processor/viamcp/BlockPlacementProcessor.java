package wtf.monsoon.impl.processor.viamcp;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import viamcp.ViaMCP;
import wtf.monsoon.api.processor.Processor;
import wtf.monsoon.impl.event.EventPacket;

import java.awt.*;

public class BlockPlacementProcessor extends Processor {

    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if (ViaMCP.getInstance().getVersion() >= ProtocolVersion.v1_11.getVersion()) {
            if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement packet = ((C08PacketPlayerBlockPlacement) e.getPacket());

                packet.facingX /= 16.0F;
                packet.facingY /= 16.0F;
                packet.facingZ /= 16.0F;

                e.setPacket(packet);
            }
        }
    };

}
