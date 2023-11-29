package wtf.monsoon.client.command

import wtf.monsoon.backend.command.Command
import kotlin.math.cos
import kotlin.math.sin

class HClipCommand : Command("HClip", "Clip horizontally") {
    override fun process(args: MutableList<String>) {
        if(args.size == 1) {
            val toClip = args[0].toDouble()
            val direction = Math.toRadians(mc.thePlayer.rotationYaw.toDouble()).toFloat()
            mc.thePlayer.setPosition(
                mc.thePlayer.posX - sin(direction.toDouble()) * toClip,
                mc.thePlayer.posY,
                mc.thePlayer.posZ + cos(direction.toDouble()) * toClip
            )
        } else {
            log("Syntax: .vclip <amount>", Level.ERROR)
        }
    }
}