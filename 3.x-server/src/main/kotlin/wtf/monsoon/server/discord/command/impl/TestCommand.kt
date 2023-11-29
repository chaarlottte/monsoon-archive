package wtf.monsoon.server.discord.command.impl

import org.javacord.api.interaction.SlashCommandInteraction
import wtf.monsoon.server.discord.command.Command

class TestCommand : Command("test", "A command for testing") {

    override fun execute(interaction: SlashCommandInteraction) {
        interaction.createImmediateResponder()
            .setContent("Test command working!")
            .respond()
    }

}