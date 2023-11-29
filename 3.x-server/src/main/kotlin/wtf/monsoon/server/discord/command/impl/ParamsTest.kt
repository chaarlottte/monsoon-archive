package wtf.monsoon.server.discord.command.impl

import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import wtf.monsoon.server.discord.command.Command
import java.util.*

class ParamsTest : Command("params_test", "A command for testing") {
    override val builder: SlashCommandBuilder
        get() {
            val builder = SlashCommand.with(name, description)
            builder.addOption(SlashCommandOption.createStringOption("name", "lolool", true))
            return builder
        }

    override fun execute(interaction: SlashCommandInteraction) {
        interaction.createImmediateResponder()
            .setContent("You said: ${interaction.getArgumentStringValueByName("name").get()}")
            .respond()
    }

}