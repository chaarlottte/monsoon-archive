package wtf.monsoon.client.modules.movement.speed

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Speed

class VanillaSpeed(name: String, parent: MulticlassModule) : ModuleMode<Speed>(name, parent) {

    @EventListener
    val preMotion = fun(e: EventMove) {
        if(player.isOnGround) {
            player.jump()
        }

        player.setSpeed(e, player.speed.toDouble())
    }

}