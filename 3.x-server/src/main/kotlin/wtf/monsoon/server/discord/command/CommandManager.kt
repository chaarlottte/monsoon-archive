package wtf.monsoon.server.discord.command

import wtf.monsoon.server.discord.command.impl.*

class CommandManager {

    val commands: List<Command> = mutableListOf<Command>(
        TestCommand(),
        ParamsTest(),
        GetConnectedUsers(),
        Shutdown(),
    )

}