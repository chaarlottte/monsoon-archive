package wtf.monsoon.server.discord

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import wtf.monsoon.server.discord.command.CommandManager


class MonsoonBot : Thread() {

    var allowedUsers = arrayOf("1091404198077276160", "464814561392853010", "633676336719986709", "815239835321106442")

    private val token: String = "MTA5MjE4ODQ5NzI5NDY3MjA5NQ.Gn83zc.UOSRzRR9ixXDF1kGj_W7jHa-yLQwswuYIHtt0I"
    lateinit var api: DiscordApi
    private lateinit var commandManager: CommandManager

    override fun run() {
        this.commandManager = CommandManager()

        this.api = DiscordApiBuilder().setToken(this.token).login().join()
        this.addSlashCommands()

        api.addSlashCommandCreateListener { event: SlashCommandCreateEvent ->
            this.commandManager.commands.forEach {
                if(it.name == event.slashCommandInteraction.commandName) {
                    it.execute(event.slashCommandInteraction)
                }
            }
        }
    }

    private fun addSlashCommands() {
        this.commandManager.commands.forEach { it.builder.createGlobal(api).join() }
    }

}