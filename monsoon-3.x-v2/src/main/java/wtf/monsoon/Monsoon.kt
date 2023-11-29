package wtf.monsoon

import me.bush.eventbuskotlin.*
import me.bush.eventbuskotlin.listener
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.input.Keyboard
import wtf.monsoon.backend.manager.*
import wtf.monsoon.backend.manager.script.ScriptManager
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.client.event.EventClientTick
import wtf.monsoon.client.event.EventKey
import wtf.monsoon.client.handler.PacketHandler
import wtf.monsoon.client.ui.kpanel.UIScreen
import wtf.monsoon.client.ui.test.TestScreen
import wtf.monsoon.client.util.player.MonsoonPlayerObject
import wtf.monsoon.client.util.ui.NVGWrapper
import wtf.monsoon.misc.protection.ProtectionManager
import java.net.Proxy

class Monsoon {

    var version = "3.0R-DEV"

    lateinit var bus: EventBusNoCache
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var targetsManager: TargetsManager
    lateinit var fileManager: FileManager
    lateinit var altManager: AltManager
    lateinit var protectionManager: ProtectionManager
    lateinit var scriptManager: ScriptManager

    lateinit var packetHandler: PacketHandler

    var proxy: Proxy = Proxy.NO_PROXY

    lateinit var nvg: NVGWrapper
    lateinit var dropdownGui: UIScreen

    val player: MonsoonPlayerObject = MonsoonPlayerObject()

    /*val eventKey = listener<EventKey>(receiveCancelled = false) {
        Wrapper.monsoon.moduleManager.forEach { (_, module) ->
            if (module.key.getValue().code == it.key)
                module.toggle()
        }

        if(it.key == Keyboard.KEY_RCONTROL)
            Minecraft.getMinecraft().displayGuiScreen(TestScreen());

    }

    val clientTick = listener<EventClientTick>(receiveCancelled = false) {
        Wrapper.monsoon.moduleManager.forEach { (_, module) ->
            if(module is MulticlassModule) {
                module.updateModes()
            }
        }
    }*/

    @EventListener
    val eventKey = fun(it: EventKey) {
        Wrapper.monsoon.moduleManager.forEach { (_, module) ->
            if (module.key.getValue().code == it.key)
                module.toggle()
        }

        if(it.key == Keyboard.KEY_RCONTROL)
            Minecraft.getMinecraft().displayGuiScreen(TestScreen());

    }

    @EventListener
    val clientTick = { _: EventClientTick ->
        Wrapper.monsoon.moduleManager.forEach { (_, module) ->
            if(module is MulticlassModule) {
                module.updateModes()
            }
        }
    }

    fun log(value: String, level: Level) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText(level.type + value))
    }

    fun <T : Module> getModule(clazz: Class<T>): T {
        return moduleManager[clazz] as T
    }

    enum class Level(val type: String) {
        INFO("${EnumChatFormatting.GRAY}<${EnumChatFormatting.AQUA}monsoon${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} "),
        ERROR("${EnumChatFormatting.GRAY}<${EnumChatFormatting.RED}err${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} "),
        WARN("${EnumChatFormatting.GRAY}<${EnumChatFormatting.YELLOW}warn${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} "),
        DEBUG("${EnumChatFormatting.GRAY}<${EnumChatFormatting.YELLOW}debug${EnumChatFormatting.GRAY}>${EnumChatFormatting.WHITE} ")
    }
}