package wtf.monsoon.client

import wtf.monsoon.newcommon.community.User
import wtf.monsoon.newcommon.packet.Packet
import wtf.monsoon.newcommon.packet.impl.client.community.ClientCommunityMessageSend
import wtf.monsoon.newcommon.packet.impl.client.login.ClientLoginPacket
import wtf.monsoon.newcommon.packet.impl.server.community.ServerCommunityMessageSend
import wtf.monsoon.newcommon.packet.impl.server.community.ServerPopulationResponse
import wtf.monsoon.newcommon.packet.impl.server.community.ServerPopulationUpdate
import wtf.monsoon.newcommon.packet.impl.server.login.ServerLoginResponse
import wtf.monsoon.newcommon.util.Communication
import java.net.InetAddress
import java.net.Socket
import java.util.*
import kotlin.random.Random

fun main() {
      /*for (i in 0..3) {
        Thread {
            try {
                old()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }*/
    old()
}

fun old() {
    val socket = Socket("localhost", 18935)
    val communication = Communication(socket, false)
    var userMap: LinkedHashMap<UUID, User> = LinkedHashMap();
    // val hwid = HWIDUtil.getHWID()
    val hwid = "d4d26ef9606dde10a88ecc1cd21e6b7d6f04afee1a998c8db4337fd6ed590eca"

    communication.write(ClientLoginPacket(
        "quick",
        InetAddress.getLocalHost().hostName,
        System.getProperty("user.name"),
        System.getProperty("os.name"),
        hwid
    ))

    Thread {
        try {
            while (true) {
                Thread.sleep(1000)
                communication.write(
                    ClientCommunityMessageSend(
                        "hi! " + Random.nextFloat()
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }.start()

    while (true) {
        System.gc();
        val packet: Packet<*> = communication.read()
        if(packet is ServerCommunityMessageSend) {
            println("[${packet.message.time}] ${packet.message.author.unformattedFullName}: ${packet.message.message}")
        } else if(packet is ServerLoginResponse) {
            println("<------Logging in------>")
            println("Success: ${packet.isSuccess}")
            println("Info: ${packet.information}")
            println("Vantage auth info: ${packet.resp.message}")
            println("Session token: ${packet.resp.sessionToken}")
            println("<---------------------->")
        } else if(packet is ServerPopulationResponse) {
            println("received a fully new user map: " + packet.userMap.toString())
            userMap = packet.userMap
        } else if(packet is ServerPopulationUpdate) {
            when(packet.action) {
                ServerPopulationUpdate.Action.ADD -> {
                    println("added new user: ${packet.user.username}")
                    userMap[packet.uuid] = packet.user
                }
                ServerPopulationUpdate.Action.REMOVE -> {
                    println("removed user: ${packet.user.username}")
                    userMap.remove(packet.uuid)
                }
            }
        }
    }
}
