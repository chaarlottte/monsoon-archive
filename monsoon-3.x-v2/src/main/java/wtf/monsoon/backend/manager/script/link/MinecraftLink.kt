package wtf.monsoon.backend.manager.script.link

import net.minecraft.client.Minecraft
import spritz.api.annotations.Identifier

/**
 * @author surge
 * @since 21/04/2023
 */
class MinecraftLink : Link() {

    @Identifier("get_debug_fps")
    fun getDebugFPS() = Minecraft.getDebugFPS()

}