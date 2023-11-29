package wtf.monsoon.server.obj

import wtf.monsoon.newcommon.community.User
import wtf.monsoon.server.Main
import wtf.monsoon.server.handler.ClientPacketHandler
import wtf.monsoon.newcommon.packet.Packet
import wtf.monsoon.newcommon.packet.handler.PacketHandler
import wtf.monsoon.newcommon.packet.impl.client.community.ClientCommunityMessageSend
import wtf.monsoon.newcommon.packet.impl.client.login.ClientLoginPacket
import wtf.monsoon.newcommon.util.Communication
import wtf.monsoon.server.util.Stopwatch
import java.net.Socket
import java.util.*

class Client constructor(private val socket: Socket) : Thread() {

    val ip: String = socket.inetAddress.hostAddress
    var pcName = "not-resolved"
    var username = "not-resolved"
    var sessionToken = "not-resolved"
    val os: String = ""
    var uuid: UUID = UUID.randomUUID()
    var hwid: String = ""
    val ssh = false
    var connected = true
    var authenticated = false
    val debug = true
    var handler: ClientPacketHandler = ClientPacketHandler(this)
    var communication: Communication = Communication(socket, true)
    val lastMessage: Long = System.currentTimeMillis()
    var timeSinceMessagesPopulated = Stopwatch()
    lateinit var userObj: User

    init {
        name = "thread-" + socket.inetAddress.hostAddress + "-" + currentThread().id
        this.start()
    }

    override fun run() {
        debug("Incoming connection from $ip")
        // handler = ClientPacketHandler(this)
        // communication = Communication(socket, true)

        while (socket.isConnected) {
            try {
                val packet: Packet<*> = communication.read()
                packet.process(handler)

                if (socket.getInputStream().read() == -1) {
                    debug("Failed InputStreamRead == -1 $ip")
                    break
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                break
            }
        }
        connected = false
        Main.server.clients.remove(this)
        Main.communityManager.removeUser(this.uuid, this.userObj)
        debug("Connection by $ip has been closed")
    }

    private fun debug(obj: Any) {
        if (debug) println("[Backend Debug] > $obj")
    }
}

private fun <T : PacketHandler?> Packet<T>.process(handler: ClientPacketHandler) {
    if(this is ClientCommunityMessageSend) {
        handler.handle(this as ClientCommunityMessageSend)
    } else if(this is ClientLoginPacket) {
        handler.handle(this as ClientLoginPacket)
    }
}
