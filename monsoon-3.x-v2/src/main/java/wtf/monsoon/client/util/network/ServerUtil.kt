package wtf.monsoon.client.util.network

import net.minecraft.client.multiplayer.ServerData
import wtf.monsoon.client.util.Util

object ServerUtil : Util() {

    fun isOnServer(ip: String): Boolean {
        return if (mc.isSingleplayer) false else getCurrentServerIP().endsWith(ip)
    }

    fun isConnectedToAnyServer(): Boolean {
        return if (mc.isSingleplayer) false else mc.currentServerData != null
    }

    fun getCurrentServerIP(): String {
        return if (mc.isSingleplayer) "Singleplayer" else mc.currentServerData.serverIP
    }

    fun isHypixel(): Boolean {
        return isOnServer("hypixel.net") || isOnServer("ilovecatgirls.xyz")
    }

    fun getCurrentServerIP(serverData: ServerData): String {
        return if (mc.isSingleplayer) "Singleplayer" else serverData.serverIP
    }

    fun isOnServer(ip: String?, serverData: ServerData): Boolean {
        return if (mc.isSingleplayer) false else getCurrentServerIP(serverData).endsWith(ip!!)
    }

    fun isHypixel(serverData: ServerData): Boolean {
        return isOnServer("hypixel.net", serverData) || isOnServer("ilovecatgirls.xyz", serverData)
    }

}