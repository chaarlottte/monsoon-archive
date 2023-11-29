package wtf.monsoon.client.modules.movement.flight

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.exploit.disabler.NegativityDisabler
import wtf.monsoon.client.modules.movement.Flight

class MotionFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    private val speed = Setting<Double>("Speed", "The speed of the flight.", 0.5, 0.05, 2.0, 0.05)
    private val timerSpeed = Setting<Float>("Timer Speed", "The timer speed of the flight.", 1.0f, 0.1f, 2.0f, 0.1f)

    init {
        this.registerSettings(speed, timerSpeed)
    }

    override fun disable() {
        super.disable()
        mc.timer.timerSpeed = 1.0f
    }

    @EventListener
    val moveEvent = fun(e: EventMove) {
        player.setSpeed(e, speed.getValue())
        e.y =
            if (mc.gameSettings.keyBindJump.isKeyDown) speed.getValue() else (if (mc.gameSettings.keyBindSneak.isKeyDown) -speed.getValue() else 0.0)
                .also { mc.thePlayer.motionY = it }

        mc.timer.timerSpeed = timerSpeed.getValue()
    }
}