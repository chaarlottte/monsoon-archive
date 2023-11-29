package wtf.monsoon.client.util.network

import net.minecraft.network.Packet

class PacketSleepThread(private val packet: Packet<*>, private val delay: Long) : Thread() {
    override fun run() {
        try {
            sleep(delay)
            PacketUtil.sendPacketNoEvent(packet)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}