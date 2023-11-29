package wtf.monsoon.api.util.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import wtf.monsoon.api.util.Util;

import java.util.HashMap;
import java.util.Map;

public final class ServerUtil extends Util {

    public static boolean isOnServer(String ip) {
        if (mc.isSingleplayer())
            return false;

        return getCurrentServerIP().endsWith(ip);
    }

    public static String getCurrentServerIP() {
        if (mc.isSingleplayer())
            return "Singleplayer";

        return mc.getCurrentServerData().serverIP;
    }

    public static boolean isHypixel() {
        return isOnServer("hypixel.net") || isOnServer("ilovecatgirls.xyz");
    }

    public static String getCurrentServerIP(ServerData serverData) {
        if (mc.isSingleplayer())
            return "Singleplayer";

        return serverData.serverIP;
    }

    public static boolean isOnServer(String ip, ServerData serverData) {
        if (mc.isSingleplayer())
            return false;

        return getCurrentServerIP(serverData).endsWith(ip);
    }

    public static boolean isHypixel(ServerData serverData) {
        return isOnServer("hypixel.net", serverData) || isOnServer("ilovecatgirls.xyz", serverData);
    }
}
