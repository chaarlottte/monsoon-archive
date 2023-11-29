package wtf.monsoon.client.modules.movement

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.player.Scaffold
import kotlin.math.abs

class Sprint : Module("Sprint", "Automatically sprint.", Category.MOVEMENT) {

    val omni = Setting("Omni", "Whether to sprint in all directions.", false)

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        if (player.moving && !(Wrapper.monsoon.getModule(Scaffold::class.java).isEnabled() && !Wrapper.monsoon.getModule(Scaffold::class.java).allowSprinting.getValue())) {
            if (omni.getValue()) {
                mc.thePlayer.isSprinting = true
            } else {
                if (player.moving && mc.thePlayer.moveForward >= abs(mc.thePlayer.moveStrafing)) mc.thePlayer.isSprinting = true
            }
        }
    }

}