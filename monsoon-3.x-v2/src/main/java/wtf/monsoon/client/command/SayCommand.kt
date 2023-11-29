package wtf.monsoon.client.command

import wtf.monsoon.backend.command.Command

class SayCommand : Command("Say", "prints a message to chat") {
    override fun process(args: MutableList<String>) {
        var message: String = ""
        args.forEach { message += "$it " }
        mc.thePlayer.sendChatMessage(message)
    }
}