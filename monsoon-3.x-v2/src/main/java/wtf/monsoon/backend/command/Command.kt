package wtf.monsoon.backend.command

import com.sun.jna.StringArray
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import wtf.monsoon.backend.Feature

abstract class Command(name: String, description: String) : Feature(name, description) {

    val token = name.lowercase();
    abstract fun process(args: MutableList<String>);

    open fun getAliases(): Array<String> {
        return arrayOf(token)
    }

    fun log(value: String, level: Level) {
        mc.thePlayer.addChatMessage(ChatComponentText(level.type + value))
    }

    enum class Level(val type: String) {
        INFO("${EnumChatFormatting.GRAY}<${EnumChatFormatting.AQUA}INFO${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} "),
        ERROR("${EnumChatFormatting.GRAY}<${EnumChatFormatting.RED}ERROR${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} "),
        WARN("${EnumChatFormatting.GRAY}<${EnumChatFormatting.YELLOW}WARN${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} ")
    }
}