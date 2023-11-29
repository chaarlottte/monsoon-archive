package wtf.monsoon.client.modules.movement.flight

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight

class UpdatedNCPFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    private val motionYSetting: Setting<Double> = Setting("Motion Y", 0.275)
        .minimum(-0.0525)
        .maximum(0.3)
        .incrementation(0.0005)

    private val ncpLatestSpeed: Setting<Double> = Setting("Speed", 9.75)
        .minimum(1.0)
        .maximum(10.0)
        .incrementation(0.05)

    private val ncpLatestTimerSpeed: Setting<Float> = Setting("Timer Speed", 0.8f)
        .minimum(0.1f)
        .maximum(1.5f)
        .incrementation(0.1f)


    init {
        this.registerSettings(motionYSetting, ncpLatestSpeed, ncpLatestTimerSpeed)
    }

    private var ticks = 0

    override fun enable() {
        super.enable()
    }

    override fun disable() {
        super.disable()
        ticks = 0
        mc.timer.timerSpeed = 1f
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        if (!player.isOnGround) {
            mc.timer.timerSpeed = ncpLatestTimerSpeed.getValue()
            if (ticks == 0) {
                mc.thePlayer.motionY = motionYSetting.getValue()
                player.setSpeed(ncpLatestSpeed.getValue())
            }
            ticks++
        } else {
            player.jump()
            mc.timer.timerSpeed = 0.2f
        }
    }

}