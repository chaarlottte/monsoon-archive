package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.network.Packet
import spritz.api.annotations.Excluded

data class EventPacket(@JvmField var packet: Packet<*>, @Excluded val direction: PacketDirection) : Event() {

    @Excluded
    override val cancellable: Boolean = true

    @Excluded
    enum class PacketDirection {
        SEND, RECEIVE
    }
}