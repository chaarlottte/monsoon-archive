package wtf.monsoon.backend.manager.script.link

import net.minecraft.client.Minecraft
import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier

/**
 * @author surge
 * @since 27/03/2023
 */
open class Link {

    @Excluded
    val mc = Minecraft.getMinecraft()

    @Identifier("null_check")
    fun nullCheck(): Boolean {
        return mc.thePlayer == null || mc.theWorld == null
    }

}