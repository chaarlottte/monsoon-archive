package wtf.monsoon.server.discord.command

import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import org.javacord.api.interaction.SlashCommandInteraction

abstract class Command(var name: String, var description: String) {
    open val builder: SlashCommandBuilder
        get() = SlashCommand.with(name, description)

    abstract fun execute(interaction: SlashCommandInteraction)
}