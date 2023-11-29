package wtf.monsoon.client.command

import net.minecraft.util.EnumChatFormatting
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.command.Command

class VClipCommand : Command("VClip", "Clip vertically") {
    override fun process(args: MutableList<String>) {
        if(args.size == 1) {
            val toClip = args[0].toDouble()
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + toClip, mc.thePlayer.posZ)
        } else {
            log("Syntax: .vclip <amount>", Level.ERROR)
        }
    }
}