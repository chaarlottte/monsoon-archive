package wtf.monsoon.impl.processor;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.*;
import net.hypixel.api.*;
import net.hypixel.api.reply.PunishmentStatsReply;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.processor.Processor;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.ServerUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.*;
import net.hypixel.api.apache.ApacheHttpClient;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class HypixelAPIProcessor extends Processor {

    @Getter @Setter
    private String apiKey = "";

    @Getter
    private HypixelAPI API;

    @Getter
    private final PunishmentData punishmentData = new PunishmentData();

    private final Timer banCheckTimer = new Timer(), keyCheckTimer = new Timer(), newKeyTimer = new Timer();

    private boolean fetchedApiKey = false;

    public HypixelAPIProcessor() {
        banCheckTimer.reset();
        keyCheckTimer.reset();
        newKeyTimer.reset();
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if(true) return;
        if(this.newKeyTimer.hasTimeElapsed(3000, false) && ServerUtil.isHypixel() && !fetchedApiKey) {
            mc.thePlayer.sendChatMessage("/api new");
            this.fetchedApiKey = true;
        }

        if(this.getApiKey().equals("")) return;

        if(this.keyCheckTimer.hasTimeElapsed(60000, true)) {
            this.checkKey();
        }

        if(this.banCheckTimer.hasTimeElapsed(3000, true)) {
            this.checkPunishments();
        }
    };


    @EventLink
    public final Listener<EventPacket> onPacketSend = e -> {

    };

    @EventLink
    public final Listener<EventConnectionSuccess> onConnectionSuccess = e -> {
        if(true) return;
        if(ServerUtil.isHypixel(e.getServerData())) {
            this.newKeyTimer.reset();
            this.fetchedApiKey = false;
        }
    };

    @EventLink
    public final Listener<EventDisconnected> onDisconnected = e -> {
        if(true) return;
        if(ServerUtil.isHypixel(e.getServerData())) {
            new Thread(() -> {
                String banType = this.checkBanType();
                switch (banType) {
                    case "unknown":
                        Wrapper.getNotifManager().notify(NotificationType.INFO, "Ban type unknown!", "The type of ban could not be retrieved.");
                        break;
                    case "watchdog":
                        Wrapper.getNotifManager().notify(NotificationType.INFO, "Likely Watchdog Ban", "This ban was likely caused by Watchdog.");
                        break;
                    case "staff":
                        Wrapper.getNotifManager().notify(NotificationType.INFO, "Likely Staff Ban", "This ban was likely by staff.");
                        break;
                }
            }).start();
            this.setApiKey("");
        }
    };

    private void checkPunishments() {
        // PlayerUtil.sendClientMessage("Checking punishment stats...");
        new Thread(() -> this.getAPI().getPunishmentStats().whenComplete(this.getPunishmentData().updatePunishments())).start();
    }

    private String checkBanType() {
        AtomicReference<String> banType = new AtomicReference<>("unknown");
        try {
            PunishmentStatsReply reply = this.getAPI().getPunishmentStats().get();
            int staffDaily = reply.getStaffRollingDaily(), watchdogDaily = reply.getWatchdogRollingDaily();
            int oldStaffDaily = this.getPunishmentData().getStaffDaily(), oldWatchdogDaily = this.getPunishmentData().getWatchdogDaily();

            System.out.println("Old Watchdog: " + oldWatchdogDaily);
            System.out.println("Current Watchdog: " + watchdogDaily);
            System.out.println("Old Staff: " + oldStaffDaily);
            System.out.println("Current Staff: " + staffDaily);
            if(watchdogDaily > oldWatchdogDaily && staffDaily <= oldStaffDaily) {
                banType.set("watchdog");
            } else if(staffDaily > oldStaffDaily && watchdogDaily <= oldWatchdogDaily) {
                banType.set("staff");
            }
            System.out.println("Ban type is considered " + banType.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return banType.get();
    }

    private void checkKey() {
        new Thread(() -> {
            this.getAPI().getKey().whenComplete((result, throwable) -> {
                if(throwable != null) {
                    throwable.printStackTrace();
                    this.setApiKey("");
                }
            });
        }).start();
    }

    public void updateApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.API = new HypixelAPI(new ApacheHttpClient(UUID.fromString(apiKey)));
        PlayerUtil.sendClientMessage("Got new API key: " + apiKey);
    }

    @Getter @Setter
    public class PunishmentData {
        private int watchdogLastMinute, watchdogDaily, watchdogTotal;
        private int staffDaily, staffTotal;

        public <T extends PunishmentStatsReply> BiConsumer<T, Throwable> updatePunishments() {
            return (result, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                this.setWatchdogDaily(result.getWatchdogRollingDaily());
                this.setWatchdogLastMinute(result.getWatchdogLastMinute());
                this.setWatchdogTotal(result.getWatchdogTotal());
                this.setStaffTotal(result.getStaffTotal());
                this.setStaffDaily(result.getStaffRollingDaily());
                // PlayerUtil.sendClientMessage("Watchdog daily: " + this.getWatchdogDaily());
                // PlayerUtil.sendClientMessage("Staff daily: " + this.getStaffDaily());
            };
        }
    }
}
