package wtf.monsoon.client.command

import net.minecraft.util.EnumChatFormatting
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.command.Command

class ConfigCommand : Command("Config", "Interact with configs") {
    override fun process(args: MutableList<String>) {
        if(args.size == 2) {
            val action = args[0]
            val configName = args[1]

            when(action) {
                "save" -> {
                    if(Wrapper.monsoon.fileManager.saveConfig(configName)) {
                        log("Saved config $configName", Level.INFO)
                    } else {
                        log("Couldn't save config $configName!", Level.WARN)
                    }
                }
                "load" -> {
                    if(Wrapper.monsoon.fileManager.loadConfig(configName)) {
                        log("Loaded config $configName", Level.INFO)
                    } else {
                        log("Couldn't load config $configName!", Level.WARN)
                    }
                }
            }
        } else {
            log("Syntax: .config <action> <configname>", Level.ERROR)
        }
    }
}