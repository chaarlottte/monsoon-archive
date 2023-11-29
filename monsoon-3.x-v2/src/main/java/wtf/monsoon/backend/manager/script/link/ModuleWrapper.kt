package wtf.monsoon.backend.manager.script.link

import net.minecraft.util.EnumChatFormatting
import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier
import wtf.monsoon.backend.module.Module

/**
 * @author surge
 * @since 21/04/2023
 */
class ModuleWrapper(@Excluded val module: Module) {

    @Identifier("get_name")
    fun getName() = module.name

    @Identifier("get_metadata")
    fun getMetadata() = module.metadata.invoke()

    @Identifier("get_name_and_metadata")
    fun getNameAndMetadata() = getName() + if (getMetadata().isNotEmpty()) "${EnumChatFormatting.GRAY} ${getMetadata()}" else ""

    @Identifier("is_enabled")
    fun isEnabled() = module.isEnabled()

    @Identifier("get_animation_factor")
    fun getAnimationFactor() = module.animation.getAnimationFactor()

}