package wtf.monsoon.client.command

import net.minecraft.util.EnumChatFormatting
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.command.Command

class ToggleCommand : Command("Toggle", "Toggles a module") {
    override fun process(args: MutableList<String>) {
        if(args.size == 1) {
            var fail = true;
            Wrapper.monsoon.moduleManager.forEach { _, module ->
                if(module.name.lowercase() == args[0].lowercase()) {
                    fail = true
                    module.toggle()
                    log("Toggled ${module.name} -> " + if (!module.isEnabled()) "${EnumChatFormatting.DARK_GREEN}Enabled" else "${EnumChatFormatting.DARK_RED}Disabled", Level.INFO)
                    return@forEach
                }
            }

            if(!fail)
                log("Module ${args[0]} not found!", Level.ERROR)
        } else {
            log("Syntax: .toggle <module>", Level.ERROR)
        }
    }

    override fun getAliases(): Array<String> {
        return arrayOf(token, "t")
    }
}