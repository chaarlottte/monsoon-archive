package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.ServerUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatSpammer extends Module {

    @Getter @Setter
    private String username = "";

    @Getter
    private final Setting<Mode> mode = new Setting<>("Mode", Mode.HYPIXEL)
            .describedBy("The manner in which to spam users.");

    @Getter
    private final Setting<Long> delay = new Setting<>("Delay", 100L)
            .minimum(0L)
            .maximum(5000L)
            .incrementation(50L)
            .describedBy("The manner in which to spam users.");

    public ChatSpammer() {
        super("Chat Spammer", "Automatically chat", Category.PLAYER);
    }

    private final Timer timer = new Timer();

    private int stage;

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
        stage = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        switch(mode.getValue()) {
            case HYPIXEL:
                if(username.equals("")) {
                    Wrapper.getNotifManager().notify(NotificationType.ERROR, "Spammer", "Please do .spam <username> to set a user to spam.");
                    this.toggle();
                }
                if(timer.hasTimeElapsed(delay.getValue(), true)) {
                    switch (stage) {
                        case 0:
                            mc.thePlayer.sendChatMessage("/party " + this.getUsername());
                            stage++;
                            break;
                        case 1:
                            mc.thePlayer.sendChatMessage("/party disband");
                            stage = 0;
                            break;
                    }
                }
                break;
            case JIBBERISH:
                if(timer.hasTimeElapsed(delay.getValue(), true))
                    mc.thePlayer.sendChatMessage("monsoon > " + ServerUtil.getCurrentServerIP() + " | " + StringUtil.getRandomString(5));

                break;
        }
    };

    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        String[] badStrings = {
                "-----------------------------------------------------",
                " to the party! They have 60 seconds to accept.",
                " has disbanded the party!"
        };
        if(e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            if(packet.getChatComponent().getUnformattedText().contains("-----------------------------------------------------")) {
                e.setCancelled(true);
            }

            if(packet.getChatComponent().getUnformattedText().contains("You cannot invite that player since they're not online.")
                || packet.getChatComponent().getUnformattedText().contains("You are not in a party right now.")) {
                // Wrapper.getNotifManager().notify(NotificationType.ERROR, "Spammer", "Couldn't spam player, toggling...");
                // this.username = "";
                // this.toggle();
            }
        }
    };

    public enum Mode {
        HYPIXEL, JIBBERISH
    }
}