package wtf.monsoon.misc.via

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import wtf.monsoon.client.event.EventClientTick
import wtf.monsoon.client.event.EventGetMinimumMotion
import wtf.monsoon.client.event.EventMouseOver
import wtf.monsoon.client.event.EventPacket


class ViaMCPFixes {

    @EventListener
    val packetEvent = fun(e: EventPacket) {
        // Block placement fix
        if(ViaMCP.getInstance().version >= ProtocolVersion.v1_11.version) {
            if(e.packet is C08PacketPlayerBlockPlacement) {
                val packet = e.packet as C08PacketPlayerBlockPlacement

                packet.facingX /= 16.0f
                packet.facingY /= 16.0f
                packet.facingZ /= 16.0f

                e.packet = packet
            }
        }

        // Accidental flying fix
        if (ViaMCP.getInstance().version > ProtocolVersion.v1_8.version) {
            if (e.packet is C03PacketPlayer) {
                val packet = e.packet as C03PacketPlayer
                if (!packet.isMoving && !packet.rotating)
                    e.cancel()
            }
        }
    }

    @EventListener
    val minimumMotion = fun(e: EventGetMinimumMotion) {
        // Minimum Motion fix
        if (ViaMCP.getInstance().version > ProtocolVersion.v1_8.version) {
            e.minimumMotion = 0.003
        }
    }

    @EventListener
    val mouseOver = fun(e: EventMouseOver) {
        // Hitbox fix
        if (ViaMCP.getInstance().version > ProtocolVersion.v1_8.version) {
            e.expand -= 0.1f
        }
    }

}