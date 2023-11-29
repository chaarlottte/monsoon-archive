package wtf.monsoon.server.discord.command.impl

import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import wtf.monsoon.server.Main
import wtf.monsoon.server.discord.command.Command

class GetConnectedUsers : Command("connected_users", "Get all users connected to Monsoon.") {

    override fun execute(interaction: SlashCommandInteraction) {
        if(interaction.user.idAsString in Main.discordBot.allowedUsers) {

            val embed: EmbedBuilder = EmbedBuilder()
                .setTitle("Monsoon - Connected Users")

            val builder = StringBuilder()
            builder.append("```").append("\n")

            Main.server.clients.forEach { client ->
                builder.append("<---------------------->").append("\n")
                builder.append("Username: ${client.username}").append("\n")
                builder.append("System name: ${client.pcName}").append("\n")
                builder.append("ID: ${client.uuid}").append("\n")
                builder.append("HWID: ${client.hwid}").append("\n")
                builder.append("IP Address: ${client.ip}").append("\n")
            }
            builder.append("<---------------------->").append("\n").append("```")
            embed.setDescription(builder.toString())
            interaction.createImmediateResponder()
                .addEmbed(embed)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
        } else {
            interaction.createImmediateResponder()
                .setContent("You don't have permission to do that!")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond()
        }
    }

}