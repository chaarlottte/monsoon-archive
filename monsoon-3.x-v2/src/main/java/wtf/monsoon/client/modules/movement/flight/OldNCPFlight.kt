package wtf.monsoon.client.modules.movement.flight

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight
import wtf.monsoon.client.util.misc.Stopwatch

class OldNCPFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    private val speedVal = Setting<Double>("Initial Speed", "The starting speed of the flight.", 1.5, 0.3, 2.0, 0.05)
    private val timerSpeed = Setting<Float>("Timer Speed", "The timer speed of the flight.", 1.0f, 0.1f, 2.0f, 0.1f)

    private var speed: Double = 0.0

    private val timer: Stopwatch = Stopwatch()

    init {
        this.registerSettings(speedVal, timerSpeed)
    }

    override fun enable() {
        super.enable()
        this.speed = this.speedVal.getValue()
        this.timer.reset()
    }

    override fun disable() {
        super.disable()
        mc.timer.timerSpeed = 1.0f
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        if (this.timer.hasTimeElapsed(350)) {
            this.speed -= 0.0046f
            if (this.speed <= 0.15)
                this.speed = 0.15

            this.timer.reset()
        }
        mc.timer.timerSpeed = this.timerSpeed.getValue()

        if (mc.thePlayer.isCollidedVertically) {
            player.jump()
        } else {
            mc.thePlayer.motionY = 1E-4
            mc.thePlayer.jumpMovementFactor = 0.0f
            player.setSpeed(player.baseMoveSpeed.coerceAtLeast(this.speed / 159.let { this.speed -= it; this.speed } + 1 / 2))
        }

        val y1: Double = mc.thePlayer.posY - (if (player.isOnGround) 0 else 1E-10).toDouble()
        mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ)
    }
}