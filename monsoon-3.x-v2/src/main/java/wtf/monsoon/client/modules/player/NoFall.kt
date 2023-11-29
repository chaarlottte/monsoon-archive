package wtf.monsoon.client.modules.player

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.network.play.client.C03PacketPlayer
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight
import wtf.monsoon.client.util.network.PacketUtil
import wtf.monsoon.client.util.math.getClosestMultipleOfDivisor

class NoFall : Module("No Fall", "Take no fall damage", Category.PLAYER) {
    val mode = Setting<Mode>("Mode", Mode.EDIT)

    private val fallSpeed = Setting<Double>("Fall Speed", "Speed at which you will fall", 0.5, 0.1, 1.0, 0.05)
        .visibleWhen { mode.getValue() == Mode.VULCAN }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        when(mode.getValue()) {
            Mode.EDIT -> {
                if (mc.thePlayer.fallDistance > 3)
                    e.onGround = true
            }
            Mode.PACKET -> {
                if (mc.thePlayer.fallDistance > 3)
                    PacketUtil.sendPacketNoEvent(C03PacketPlayer(true))
            }
            Mode.VERUS -> {
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3 && !Wrapper.monsoon.moduleManager.getModule(Flight::class.java).isEnabled()) {
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX *= 0.7
                    mc.thePlayer.motionZ *= 0.7
                    mc.thePlayer.fallDistance = 0.0f
                    mc.thePlayer.setPosition(e.x, e.y, e.z)
                    e.onGround = true
                }
            }
            Mode.DIVISOR -> e.y = getClosestMultipleOfDivisor(e.y, player.groundDivisor);
            Mode.VULCAN -> {
                if(mc.thePlayer.fallDistance >= 3.7) {
                    e.onGround = true
                    mc.thePlayer.motionY = -fallSpeed.getValue()
                    mc.thePlayer.fallDistance = 0f
                }
            }

            else -> {}
        }
    }

    @EventListener
    val move = fun(e: EventMove) {
        when (mode.getValue()) {
            Mode.Y_CHANGE ->
                if (mc.thePlayer.fallDistance > 3) {
                    e.y -= (mc.thePlayer.fallDistance - 0.5)
                    PacketUtil.sendPacket(C03PacketPlayer(true))
                }

            else -> {}
        }
    }

    enum class Mode {
        EDIT, PACKET, VERUS, Y_CHANGE, DIVISOR, VULCAN
    }
}