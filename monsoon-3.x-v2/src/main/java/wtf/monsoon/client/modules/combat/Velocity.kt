package wtf.monsoon.client.modules.combat

import com.mojang.authlib.GameProfile
import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.event.EventUpdate
import wtf.monsoon.client.util.misc.StringUtil
import wtf.monsoon.client.util.network.PacketUtil
import java.util.*

class Velocity : Module("Velocity", "Take reduced or zero knockback.", Category.COMBAT) {

    private val mode: Setting<Mode> = Setting("Mode", "The mode of velocity.", Mode.CANCEL)

    private val horMod: Setting<Int> = Setting("Horizontal", "Horizontal velocity modifier.", 0)
        .minimum(0)
        .maximum(100)
        .incrementation(1)
        .visibleWhen { mode.getValue() == Mode.CANCEL }

    private val vertMod: Setting<Int> = Setting("Vertical", "Vertical velocity modifier.", 0)
        .minimum(0)
        .maximum(100)
        .incrementation(1)
        .visibleWhen { mode.getValue() == Mode.CANCEL }

    private val strength: Setting<Double> = Setting("Strength", "The strength of velocity.", 3.0)
        .minimum(1.0)
        .maximum(10.0)
        .incrementation(1.0)
        .visibleWhen { mode.getValue() == Mode.DRAG_CLICK }

    private var receivedVelocity = false

    override var metadata: () -> String = {
        when (mode.getValue()) {
            Mode.DRAG_CLICK -> "${StringUtil.formatEnum(mode.getValue())}, ${strength.getValue()}"
            Mode.WATCHDOG -> "Watchdog"
            Mode.CANCEL -> "${horMod.getValue()}% ${vertMod.getValue()}%"
            else -> ""
        }
    }

    override fun disable() {
        super.disable()
        receivedVelocity = false
    }

    @EventListener
    val packet = fun(it: EventPacket) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return
        }

        if (it.packet is S12PacketEntityVelocity) {
            val packet = it.packet as S12PacketEntityVelocity

            if (packet.entityID == mc.thePlayer.entityId) {
                receivedVelocity = true
            }
            when (mode.getValue()) {
                Mode.CANCEL -> {
                    if (horMod.getValue() === 0 && vertMod.getValue() === 0) it.cancel()
                    packet.motionX = (packet.motionX * 0.01 * horMod.getValue()).toInt()
                    packet.motionY = (packet.motionY * 0.01 * vertMod.getValue()).toInt()
                    packet.motionZ = (packet.motionZ * 0.01 * horMod.getValue()).toInt()
                }

                Mode.WATCHDOG -> {
                    // if (!(Wrapper.getModule(Speed::class.java).isEnabled() && Wrapper.getModule(Speed::class.java).getMode().equals(Mode.WATCHDOG))) {
                        if (packet.entityID == mc.thePlayer.entityId) {
                            it.cancel()
                            mc.thePlayer.motionY = packet.motionY / 8000.0
                        }
                    // }
                }
                else -> {}
            }
        } else if (it.packet is S27PacketExplosion) {
            when (mode.getValue()) {
                Mode.CANCEL -> it.cancel()
                Mode.WATCHDOG -> if (mc.thePlayer.hurtTime > 0) {
                    it.cancel()
                    mc.thePlayer.motionY += 0.001 - Math.random() / 100f
                }
                else -> {}
            }
        }
    }

    @EventListener
    val preMotion = fun(_: EventPreMotion) {
        if (mode.getValue() === Mode.DRAG_CLICK) {
            if (mc.thePlayer.hurtTime === 10 && receivedVelocity) {
                for (i in 0..9) {
                    val fakePlayer = EntityOtherPlayerMP(mc.theWorld, GameProfile(UUID.randomUUID(), "a"))
                    PacketUtil.sendPacketNoEvent(C0APacketAnimation())
                    PacketUtil.sendPacketNoEvent(C02PacketUseEntity(fakePlayer, C02PacketUseEntity.Action.ATTACK))
                    mc.thePlayer.motionX *= 1.0 / strength.getValue()
                    mc.thePlayer.motionZ *= 1.0 / strength.getValue()
                }
            }
        }

        if (mc.thePlayer.hurtTime === 0) {
            receivedVelocity = false
        }
    }

    internal enum class Mode {
        CANCEL, WATCHDOG, DRAG_CLICK
    }
}