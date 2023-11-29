package wtf.monsoon.backend

import net.minecraft.client.Minecraft
import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier
import wtf.monsoon.Wrapper
import wtf.monsoon.client.util.player.MonsoonPlayerObject

/**
 * @author surge
 * @since 09/02/2023
 */
open class Feature(@Identifier("name") @JvmField val name: String, @Identifier("description") @JvmField val description: String) {

    @Excluded
    protected val mc: Minecraft = Minecraft.getMinecraft()

    @Excluded
    val player: MonsoonPlayerObject = Wrapper.monsoon.player

}