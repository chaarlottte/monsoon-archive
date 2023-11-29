package wtf.monsoon.server.handler

import wtf.monsoon.newcommon.community.User
import wtf.monsoon.newcommon.packet.handler.impl.IClientPacketHandler
import wtf.monsoon.newcommon.packet.impl.client.community.ClientCommunityMessageSend
import wtf.monsoon.newcommon.packet.impl.client.community.ClientPopulationRequest
import wtf.monsoon.newcommon.packet.impl.client.login.ClientLoginPacket
import wtf.monsoon.newcommon.packet.impl.server.community.ServerPopulationResponse
import wtf.monsoon.newcommon.packet.impl.server.community.ServerPopulationUpdate
import wtf.monsoon.newcommon.packet.impl.server.login.ServerInvalidAccessTokenResponse
import wtf.monsoon.newcommon.packet.impl.server.login.ServerLoginResponse
import wtf.monsoon.newcommon.vantage.api.models.AuthResponse
import wtf.monsoon.newcommon.vantage.api.utils.AuthUtil
import wtf.monsoon.server.Main
import wtf.monsoon.server.obj.Client
import wtf.monsoon.server.util.Verification
import java.sql.Wrapper
import java.util.*
import kotlin.random.Random


class ClientPacketHandler(private var client: Client) : IClientPacketHandler {
    override fun handle(packet: ClientLoginPacket) {
        try {
            println("<---------------------->")
            println("Login Request Received")
            println("Hostname: " + packet.hostName)
            println("IP: " + client.ip)
            println("<---------------------->")
            if (Main.skipAuth) {
                System.err.println("!!! SKIPPING AUTHENTICATION !!!")
                client.username = "unauthenticated-" + Random.nextInt()
                client.uuid = UUID.randomUUID()
                client.pcName = packet.systemName
                client.authenticated = true
                this.respond(true, AuthResponse(false, "Unauthenticated session - " + packet.hostName, "NA"), "Unauthenticated session - " + packet.hostName)
                this.client.userObj = User(this.client.username)
                Main.communityManager.addNewUser(this.client.uuid, this.client.userObj)
                this.client.communication.write(ServerPopulationResponse(Main.communityManager.userMap))
                return
            }

            val resp: AuthResponse = AuthUtil.authenticate(packet.username, packet.hardwareID)

            if (resp.isError) {
                this.respond(false, resp, resp.message)
                return
            }

            debug("Say hi to " + packet.systemName + "@" + packet.osName)
            debug("Online users: " + Main.server.clients.size)

            client.username = packet.username
            client.pcName = packet.systemName
            client.hwid = packet.hardwareID
            client.sessionToken = resp.sessionToken
            client.authenticated = true
            println("<------Logging in------>")
            println("User: " + packet.username)
            println("IP: " + client.ip)
            println("<---------------------->")
            this.respond(true, resp, "Logged in.")
            System.gc()
            this.client.userObj = User(this.client.username)
            Main.communityManager.addNewUser(this.client.uuid, this.client.userObj)
            this.client.communication.write(ServerPopulationResponse(Main.communityManager.userMap))
        } catch (e: Exception) {
            debug("Something went wrong while handling a packet from a client! - " + e.message)
        }
    }

    override fun handle(packet: ClientCommunityMessageSend) {
        if(client.authenticated)
            Main.communityManager.packet(packet, client)
    }

    override fun handle(packet: ClientPopulationRequest) {
        if(Verification.verifyAuthToken(packet.accessToken))
            this.client.communication.write(ServerPopulationResponse(Main.communityManager.userMap))
        else
            this.client.communication.write(ServerInvalidAccessTokenResponse())
    }

    private fun debug(msg: String) {
        val debug = true
        if (debug) {
            println("[Backend Debug] > $msg")
        }
    }

    private fun respond(success: Boolean, resp: AuthResponse, message: String) {
        client.communication.write(ServerLoginResponse(success, resp, message))
        if (!success) {
            Main.server.clients.remove(client)
            debug("FAILED REMOVING CLIENT FROM LIST")
        }
    }

}