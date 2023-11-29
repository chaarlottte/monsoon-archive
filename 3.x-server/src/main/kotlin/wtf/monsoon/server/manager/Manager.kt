package wtf.monsoon.server.manager

import wtf.monsoon.newcommon.packet.Packet
import wtf.monsoon.server.obj.Client

abstract class Manager {
    abstract fun packet(packet: Packet<*>, client: Client)
}
