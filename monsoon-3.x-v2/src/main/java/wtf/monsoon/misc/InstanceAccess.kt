package wtf.monsoon.misc

import net.minecraft.client.Minecraft
import wtf.monsoon.Wrapper
import wtf.monsoon.client.util.player.MonsoonPlayerObject

open class InstanceAccess {
    val mc: Minecraft = Minecraft.getMinecraft()
    val player: MonsoonPlayerObject = Wrapper.monsoon.player
}
