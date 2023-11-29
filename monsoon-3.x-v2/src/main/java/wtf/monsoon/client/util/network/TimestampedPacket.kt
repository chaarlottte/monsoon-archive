package wtf.monsoon.client.util.network

import net.minecraft.network.Packet

class TimestampedPacket(val packet: Packet<*>, private val sendTime: Long) {

    fun isReady(): Boolean {
        return System.currentTimeMillis() >= this.sendTime
    }

    fun release(list: MutableList<TimestampedPacket>, event: Boolean = false) {
        if(event) PacketUtil.sendPacket(this.packet)
        else PacketUtil.sendPacketNoEvent(this.packet)
        list.remove(this)
    }

    fun releaseIfReady(list: MutableList<TimestampedPacket>, event: Boolean = false) {
        if(this.isReady())
            this.release(list, event)
    }

}