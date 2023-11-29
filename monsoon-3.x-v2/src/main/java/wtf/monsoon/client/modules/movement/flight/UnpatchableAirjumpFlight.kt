package wtf.monsoon.client.modules.movement.flight

import com.viaversion.viaversion.api.protocol.packet.Direction
import me.bush.eventbuskotlin.EventListener
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S12PacketEntityVelocity
import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight
import wtf.monsoon.client.util.network.PacketUtil
import wtf.monsoon.client.util.player.SelfDamageObject


class UnpatchableAirjumpFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    private lateinit var selfDamage: SelfDamageObject

    private var startedDamage: Boolean = false
    private var damaged: Boolean = false
    private var jumped: Boolean = false
    private var jumpedAgain: Boolean = false
    private var hold: Boolean = false

    private var fallY: Double = 0.0

    private var map: MutableMap<S12PacketEntityVelocity, MutableList<Packet<*>>> = HashMap()
    private var key: S12PacketEntityVelocity? = null

    init {
        this.registerSettings()
    }

    override fun enable() {
        super.enable()
        this.startedDamage = false
        this.damaged = false
        this.jumped = false
        this.jumpedAgain = false
        this.hold = true
        this.fallY = mc.thePlayer.posY
    }

    override fun disable() {
        super.disable()
        mc.timer.timerSpeed = 1.0f
    }

    @EventListener
    val preMotion = fun(_: EventPreMotion) {
        if(!this.startedDamage) {
            // damage to receive velocity packets
            val customDamage = false
            if(customDamage) {
                this.selfDamage = SelfDamageObject(SelfDamageObject.DamageType.JUMP)
                this.selfDamage.start()
            } else {
                player.oldNCPDamage()
                this.damaged = true
            }
            this.startedDamage = true
        } else if(this.startedDamage && !this.damaged) {
            this.damaged = this.selfDamage.isReady()
            Wrapper.monsoon.log("damaged: ${this.damaged}", Monsoon.Level.DEBUG)
        } else if(!this.jumped && this.damaged && this.hold) {
            // initial jump after receiving velocity
            mc.thePlayer.jump()
            this.fallY = mc.thePlayer.posY
            this.jumped = true
        } else {
            // when in air
            if(mc.thePlayer.posY <= this.fallY && this.hold && this.key != null) {
                Wrapper.monsoon.log("ready to jump again", Monsoon.Level.DEBUG)
                // if you're at the same Y as when you jumped, process velocity to "jump" again
                this.map[this.key!!]?.forEach(PacketUtil::sendPacketNoEvent)
                this.hold = false

                /*if(!this.jumpedAgain) {
                    mc.thePlayer.jump()
                    this.jumpedAgain = true
                    Wrapper.monsoon.log("jumped again", Monsoon.Level.DEBUG)
                } else {
                    Wrapper.monsoon.log("later nerds B)", Monsoon.Level.DEBUG)
                    this.parent.toggle()
                }*/
            }
        }
    }

    @EventListener
    val packetEvent = fun(e: EventPacket) {
        println("NIGGGGGERRRRRRRR")
        if(e.direction == EventPacket.PacketDirection.RECEIVE) {
            if (e.packet is S12PacketEntityVelocity) {
                val packet = e.packet as S12PacketEntityVelocity
                this.key = packet
                Wrapper.monsoon.log("$packet", Monsoon.Level.INFO)
                Wrapper.monsoon.log("${this.key}", Monsoon.Level.INFO)
                this.map[packet] = mutableListOf<Packet<*>>()
                this.map[this.key!!]?.add(e.packet)
                e.cancel()

                if(this.key != null) {
                    this.hold = true
                }
                // Wrapper.monsoon.log("${this.key}", Monsoon.Level.INFO)
            } else {
                if (this.hold && this.key != null) {
                    this.map[this.key!!]?.add(e.packet)
                    e.cancel()
                }
            }
        } else {
            if (this.hold && this.key != null) {
                this.map[this.key!!]?.add(e.packet)
                e.cancel()
                // Wrapper.monsoon.log("nonull ${this.key}", Monsoon.Level.INFO)
            } else {
                // if(this.hold)
                   //  Wrapper.monsoon.log("null ${this.key}", Monsoon.Level.INFO)
            }
        }
    }

    @EventListener
    val moveEvent = fun(e: EventMove) {

    }
}