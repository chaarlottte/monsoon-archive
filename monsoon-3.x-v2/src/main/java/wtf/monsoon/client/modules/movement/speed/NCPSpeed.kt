package wtf.monsoon.client.modules.movement.speed

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.potion.Potion
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.modules.movement.Speed

class NCPSpeed(name: String, parent: MulticlassModule) : ModuleMode<Speed>(name, parent) {

    private var reset = false

    override var metadata: () -> String = { "NCP but so silly" }

    override fun disable() {
        super.disable()
        mc.timer.timerSpeed = 1.0f
    }

    @EventListener
    val move = fun(e: EventMove) {
        if (mc.thePlayer.ticksExisted % 20 <= 9) {
            mc.timer.timerSpeed = 1.05f
        } else {
            mc.timer.timerSpeed = 0.98f
        }

        if (player.moving) {
            if (player.isOnGround) {
                this.reset = false
                player.jump(e)
                player.setSpeed(e, player.speed * 1.01f)
                e.y = 0.41
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    player.setSpeed(
                        e,
                        player.speed * (1.0f + 0.1f * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1))
                    )
                }
            }
            player.setSpeed(e, player.speed * 1.0035f)

            if (player.speed < 0.277)
                reset = true

            if (reset)
                player.setSpeed(e, 0.277f)
        } else {
            e.x = 0.0.also { mc.thePlayer.motionX = it }
            e.z = 0.0.also { mc.thePlayer.motionZ = it }
            reset = true
        }
    }

}