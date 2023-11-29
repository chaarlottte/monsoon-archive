package wtf.monsoon.client.modules.movement.flight

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight

class VanillaFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    override fun disable() {
        super.disable()
        mc.thePlayer.capabilities.isFlying = false;
    }

    @EventListener
    val preMotion = fun(_: EventPreMotion) {
        mc.thePlayer.capabilities.isFlying = true;
    }

}