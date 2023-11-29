package wtf.monsoon.server.manager.impl

import wtf.monsoon.newcommon.community.Message
import wtf.monsoon.newcommon.community.User
import wtf.monsoon.newcommon.packet.Packet
import wtf.monsoon.newcommon.packet.impl.client.community.ClientCommunityMessageSend
import wtf.monsoon.newcommon.packet.impl.server.community.ServerCommunityMessageSend
import wtf.monsoon.newcommon.packet.impl.server.community.ServerPopulationUpdate
import wtf.monsoon.newcommon.packet.impl.server.misc.ServerErrorResponse
import wtf.monsoon.newcommon.util.type.EvictingList
import wtf.monsoon.server.Main
import wtf.monsoon.server.manager.Manager
import wtf.monsoon.server.obj.Client
import java.util.*


class CommunityManager : Manager() {
    private val messages: EvictingList<Message> = EvictingList(200)
    var userMap: LinkedHashMap<UUID, User> = LinkedHashMap<UUID, User>()

    override fun packet(packet: Packet<*>, client: Client) {
        if (packet is ClientCommunityMessageSend) {
            if(client.userObj in this.userMap.values) {
                val message = Message(
                    client.userObj,
                    packet.message
                )
                messages.add(message)
                Main.server.clients.forEach { c ->
                    c.communication.write(ServerCommunityMessageSend(message))
                }

                println("Messages: " + messages.size)
            } else {
                client.communication.write(ServerErrorResponse("User not in server map. Perhaps try relogging?"))
            }
        }
    }

    fun addNewUser(uuid: UUID, user: User) {
        this.userMap[uuid] = user
        Main.server.clients.forEach { c ->
            c.communication.write(ServerPopulationUpdate(user, uuid, ServerPopulationUpdate.Action.ADD))
        }
    }

    fun removeUser(uuid: UUID, user: User) {
        this.userMap.remove(uuid)
        Main.server.clients.forEach { c ->
            c.communication.write(ServerPopulationUpdate(user, uuid, ServerPopulationUpdate.Action.REMOVE))
        }
    }
}
