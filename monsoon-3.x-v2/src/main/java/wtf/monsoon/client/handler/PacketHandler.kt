package wtf.monsoon.client.handler

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.handler.Handler
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.client.event.EventClientTick
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventUpdate
import wtf.monsoon.client.util.network.PacketSleepThread
import wtf.monsoon.client.util.network.PacketUtil

class PacketHandler : Handler("Packet Handler", "Handles all incoming/outgoing Minecraft packets.") {

    var blinking: Boolean = false
    private var heldBlinkPackets = mutableListOf<Packet<*>>()
    private var possibleBlinkPackets = mutableListOf<Class<out Packet<*>>>(
        C03PacketPlayer::class.java,
        C04PacketPlayerPosition::class.java,
        C05PacketPlayerLook::class.java,
        C06PacketPlayerPosLook::class.java,
    )

    var enablePingSpoof: Boolean = false
    var pingSpoofDelay: Long = 0L
    private var pingSpoofPacketMap = mutableMapOf<Long, Packet<*>>()

    init {
        //Wrapper.monsoon.bus.subscribe(this)
    }

    fun incoming(packet: Packet<*>): Pair<Boolean, Packet<*>> {
        val event = EventPacket(packet, EventPacket.PacketDirection.RECEIVE)
        if (this.shouldFireEvent(packet)) Wrapper.monsoon.bus.post(event)
        return Pair(event.cancelled, event.packet)
    }

    fun outgoing(packet: Packet<*>, callEvent: Boolean): Pair<Boolean, Packet<*>>  {
        val event = EventPacket(packet, EventPacket.PacketDirection.SEND)
        if (callEvent && this.shouldFireEvent(packet)) Wrapper.monsoon.bus.post(event)
        return Pair(event.cancelled, event.packet)
    }

    private fun shouldFireEvent(packet: Packet<*>): Boolean {
        return !this.handleBlink(packet) && !this.handlePingSpoof(packet)
    }

    private fun handleBlink(packet: Packet<*>) : Boolean {
        if(!this.blinking) return false
        if(this.possibleBlinkPackets.contains(packet.javaClass)) {
            this.heldBlinkPackets += packet
            return true
        }
        return false
    }

    private fun handlePingSpoof(packet: Packet<*>): Boolean {
        if(!this.enablePingSpoof) return false
        if(packet is C0FPacketConfirmTransaction || packet is C00PacketKeepAlive) {
            pingSpoofPacketMap[System.currentTimeMillis() + this.pingSpoofDelay] = packet
            return true
        }
        return false
    }

    @EventListener
    val onUpdate = fun(_: EventUpdate) {
        this.pingSpoofPacketMap.forEach { (t, u) ->
            if(t <= System.currentTimeMillis()) {
                PacketUtil.sendPacketNoEvent(u)
                this.pingSpoofPacketMap.remove(t)
            }
        }
    }
}