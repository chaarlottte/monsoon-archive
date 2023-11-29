package wtf.monsoon.backend.manager

import wtf.monsoon.backend.command.Command
import wtf.monsoon.client.event.EventChatMessage
import java.util.*

class CommandManager : LinkedHashMap<Class<out Command>, Command>() {

    private val prefix = "."

    fun process(e: EventChatMessage) {
        val message: String = e.message

        if (message.startsWith(prefix)) {
            e.cancel()
            val args: Array<String> = message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val commandName = args[0].substring(prefix.length)
            val commandArgs = args.copyOfRange(1, args.size)

            for(c in this.values) {
                for(a in c.getAliases()) {
                    if(commandName == a) {
                        c.process(commandArgs.toMutableList())
                        return
                    }
                }
                if(commandName == c.token) {
                    c.process(commandArgs.toMutableList())
                    return
                }
            }
        }
    }
}