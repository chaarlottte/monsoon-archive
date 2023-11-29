package wtf.monsoon.server.discord.command.impl

import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.interaction.MessageComponentInteraction
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater
import wtf.monsoon.server.Main
import wtf.monsoon.server.discord.command.Command


class Shutdown : Command("shutdown_server", "Get all users connected to Monsoon.") {


    override fun execute(interaction: SlashCommandInteraction) {
        if(interaction.user.idAsString in Main.discordBot.allowedUsers) {

            /*val msg = MessageBuilder()
                .setContent("Are you sure you want to shut down the server? This could cause data to be lost, and could eject users from the client.")
                .addComponents(
                    ActionRow.of(
                        Button.danger("shutdown_${interaction.user.idAsString}", "Confirm Shutdown"),
                        Button.secondary("cancelshutdown", "Cancel")
                    )
                )

            msg.send(interaction.channel.get())*/

            interaction.createImmediateResponder()
                .setContent("Are you sure you want to shut down the server? This could cause data to be lost, and could eject users from the client.")
                .addComponents(
                    ActionRow.of(
                        Button.danger("shutdown_${interaction.user.idAsString}", "Confirm Shutdown"),
                        Button.secondary("cancelshutdown_${interaction.user.idAsString}", "Cancel")
                    )
                )
                .respond()

            Main.discordBot.api.addMessageComponentCreateListener { event ->
                val messageComponentInteraction: MessageComponentInteraction = event.messageComponentInteraction
                if(messageComponentInteraction.customId.startsWith("shutdown")
                    && messageComponentInteraction.customId.endsWith(event.messageComponentInteraction.user.idAsString)) {
                    event.messageComponentInteraction.createImmediateResponder().setContent("Shutting down...").respond()
                    Main.shouldRun = false
                } else if(messageComponentInteraction.customId.startsWith("cancelshutdown_")
                    && messageComponentInteraction.customId.endsWith(event.messageComponentInteraction.user.idAsString)) {
                    event.messageComponentInteraction.message.delete()
                    event.messageComponentInteraction.createImmediateResponder().respond()
                }
            }
        } else {
            interaction.createImmediateResponder()
                .setContent("You don't have permission to do that!")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
        }
    }

}