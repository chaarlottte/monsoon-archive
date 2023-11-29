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

class CollisionFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        mc.thePlayer.onGround = true
        e.onGround = true
    }

    @EventListener
    val move = fun(e: EventMove) {
        e.y = 0.0
            .also { mc.thePlayer.motionY = 0.0 }
    }
}