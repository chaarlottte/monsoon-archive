package wtf.monsoon.server

import wtf.monsoon.newcommon.community.User
import wtf.monsoon.server.manager.impl.CommunityManager
import wtf.monsoon.server.obj.Client
import java.net.ServerSocket
import java.util.Objects
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList


class MonsoonServer : Thread() {

    var clients: CopyOnWriteArrayList<Client> = CopyOnWriteArrayList<Client>()

    override fun run() {
        val server = ServerSocket(18935)

        while(true) {
            try {
                val socket = server.accept()
                val client = Client(socket)

                this.clients.removeIf { Objects.equals(client.username, it.username) }
                this.clients.add(client)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}