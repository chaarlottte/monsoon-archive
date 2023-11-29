package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.server.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.AES256;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.movement.LongJump;
import wtf.monsoon.impl.module.movement.Speed;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AutoHypixel extends Module {

    private JSONObject obj;

    @Getter
    @Setter
    private String apiKey;

    @Getter
    private final Setting<Boolean> autoPlay = new Setting<>("Auto Play", true)
            .describedBy("Whether to enable auto play.");

    @Getter
    private final Setting<AutoPlayMode> autoPlayMode = new Setting<>("Auto Play Mode", AutoPlayMode.SOLO_INSANE)
            .describedBy("Auto Play mode")
            .childOf(autoPlay);
    @Getter
    private final Setting<Boolean> staffWarns = new Setting<>("Staff Analyzer", true)
            .describedBy("Whether to enable staff analyzer.");

    private final Setting<String> blacklistedMapsContainer = new Setting<>("Blacklisted Maps", "Blacklisted Maps")
            .describedBy("Skywars maps to blacklist.");

    @Getter private List<String> blacklistedMaps = new ArrayList<>();

    private final String[] strings = new String[]{"1st Killer - ", "1st Place - ", "You died! Want to play again? Click here!", " - Damage Dealt - ", "1st - ", "Winning Team - ", "Winners: ", "Winner: ", "Winning Team: ", " win the game!", "1st Place: ", "Last team standing!", "Winner #1 (", "Top Survivors", "Winners - "};


    public AutoHypixel() {
        super("Auto Hypixel", "Automatically Hypixel", Category.PLAYER);
        try {
            blacklistedMaps = FileUtils.readLines(new File("monsoon/blacklisted_maps.txt"));
        } catch (Exception exception) {}
    }

    Timer timer = new Timer();

    @Override
    public void onEnable() {
        super.onEnable();
        mc.thePlayer.sendChatMessage("/api new");
    }

    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        /*if (staffWarns.getValue() && timer.hasTimeElapsed(60000, true)) {
            new Thread(() -> {
                try {
                    obj = readJsonFromUrl("https://api.hypixel.net/watchdogStats?key=" + apiKey);

                    String bans = obj.get("watchdog_lastMinute").toString();
                    //NotificationManager.show(new Notification(NotificationType.INFO, "Staff Analyzer", "Staff have banned " + bans + " players in the last minute.", 1));
                    PlayerUtil.sendClientMessage("Staff have banned " + bans + " players in the last minute.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }*/
    };

    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if (autoPlay.getValue()) {
            if (e.getPacket() instanceof S02PacketChat) {
                try {
                    S02PacketChat packet = (S02PacketChat) e.getPacket();
                    for (String string : strings) {
                        if (packet.getChatComponent().getUnformattedText().contains(string)) {
                            Wrapper.getNotifManager().notify(NotificationType.INFO, "Auto Play", "Sending you to the next game...");
                            mc.thePlayer.sendChatMessage(getAutoPlay());
                        }
                    }

                    if (packet.getChatComponent().getUnformattedText().contains("A player has been removed from your lobby.")) {
                        mc.thePlayer.sendChatMessage("what is go on? noboline ban?");
                        Wrapper.getNotifManager().notify(NotificationType.WARNING, "Ban detected", "Sending you to the next game to avoid a staff ban...");
                        mc.thePlayer.sendChatMessage(getAutoPlay());
                    }
                } catch (Exception ignored) {}
            }
            if (e.getPacket() instanceof S45PacketTitle) {
                try {
                    S45PacketTitle packetTitle = (S45PacketTitle) e.getPacket();
                    if (packetTitle.getMessage().getUnformattedText().toLowerCase().contains("died")) {
                        Wrapper.getNotifManager().notify(NotificationType.INFO, "Auto Play", "Sending you to the next game...");
                        mc.thePlayer.sendChatMessage(getAutoPlay());
                    }
                } catch (Exception ignored) {}
            }
            if(e.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) e.getPacket();
                if(packet.getChatComponent().getUnformattedText().contains("You are sending commands too fast! Please slow down.")) {
                    e.setCancelled(true);
                }
            }
        }

        /*if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
            if (mc.getNetHandler().doneLoadingTerrain) {
                if (Wrapper.getModule(Speed.class).isEnabled()) {
                    PlayerUtil.sendClientMessage("Disabled speed due to lagback");
                    Wrapper.getModule(Speed.class).setEnabled(false);
                }

                if (Wrapper.getModule(LongJump.class).isEnabled()) {
                    PlayerUtil.sendClientMessage("Disabled speed due to lagback");
                    Wrapper.getModule(LongJump.class).setEnabled(false);
                }
            }
        }*/
    };

    public void addBlacklistedMap(String mapName) {
        blacklistedMaps.add(mapName);
        new Thread(() ->{
            File file = new File("monsoon/blacklisted_maps.txt");
            try {
                if (!file.exists())
                    file.createNewFile();


                FileWriter fileWriter = new FileWriter(file);
                for(String s : this.blacklistedMaps) {
                    fileWriter.write(s + "\n");
                }
                fileWriter.flush();
                fileWriter.close();

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    private String getAutoPlay() {
        switch (autoPlayMode.getValue()) {
            case SOLO_INSANE:
                return "/play solo_insane";
            case SOLO_NORMAL:
                return "/play solo_normal";
            case DOUBLES_INSANE:
                return "/play doubles_insane";
            case DOUBLES_NORMAL:
                return "/play doubles_normal";
        }

        return "lol the monsoon dev is incompetent and couldnt make autoplay";
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private enum AutoPlayMode {
        SOLO_INSANE,
        SOLO_NORMAL,
        DOUBLES_INSANE,
        DOUBLES_NORMAL
    }
}