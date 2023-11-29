package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.ServerUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.hud.SessionInfo;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KillInsults extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.DEFAULT);
    private final Setting<String> label1 = new Setting<>("Reload killsults from", "Reload killsults from");
    private final Setting<String> label2 = new Setting<>("killsults.txt with .reload", "killsults.txt with .reload");

   private final List<String> killsults = new ArrayList<>(), furrysults = new ArrayList<>();
   private List<String> customKillsults = new ArrayList<>();
   private final List<EntityPlayer> attackedCache = new ArrayList<>();

    public KillInsults() {
        super("Kill Insults", "Insult your enemies :angry:", Category.PLAYER);
        this.loadKillsults();
    }

    Timer timer = new Timer();

    @Override
    public void onEnable() {
        super.onEnable();
        this.killsults.clear();
        this.furrysults.clear();
        this.addDefaultKillsults();
    }

    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        if(mc.thePlayer.ticksExisted < 10) {
            this.attackedCache.clear();
        }
    };

    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if(e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            if(packet.getChatComponent().getUnformattedText().contains(mc.getSession().getUsername())) {
                try {
                    String msg = packet.getChatComponent().getUnformattedText();
                    if(msg.contains("by")) {
                        msg = msg.replace(mc.getSession().getUsername(), "#");
                        if(msg.split("by")[1].contains("#")) {
                            if(!this.attackedCache.isEmpty()) {
                                this.triggerKillsult(msg);
                            }
                        }
                    } else if(msg.contains("morreu")) {
                        msg = msg.replace(mc.getSession().getUsername(), "#");
                        if(msg.split("morreu")[1].contains("#")) {
                            if(!this.attackedCache.isEmpty()) {
                                this.triggerKillsult(msg);
                            }
                        }
                    } else if(msg.contains("por")) {
                        msg = msg.replace(mc.getSession().getUsername(), "#");
                        if(msg.split("por")[1].contains("#")) {
                            if(!this.attackedCache.isEmpty()) {
                                this.triggerKillsult(msg);
                            }
                        }
                    } else if(msg.contains(mc.getSession().getUsername())) {
                        EntityPlayer lastHit = this.getLastHit(msg);
                        if(lastHit != null)
                            if(msg.contains(lastHit.getCommandSenderName()))
                                if(!this.attackedCache.isEmpty())
                                    this.triggerKillsult(msg);
                    }
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }
        }

        if(e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
            if(packet.getEntityFromWorld(mc.theWorld) != null) {
                if (packet.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer) {
                    // this.lastHit = (EntityPlayer) packet.getEntityFromWorld(mc.theWorld);
                    EntityPlayer player = (EntityPlayer) packet.getEntityFromWorld(mc.theWorld);
                    if(!this.attackedCache.contains(player)) this.attackedCache.add(player);
                }
            }
        }
    };

    public void loadKillsults() {
        this.killsults.clear();
        this.addDefaultKillsults();
        File killsultsFile = new File("monsoon/killsults.txt");
        if(!killsultsFile.exists()) {
            this.addDefaultKillsults();
            try {
                killsultsFile.createNewFile();

                FileWriter fileWriter = new FileWriter(killsultsFile);
                for(String s : this.killsults) {
                    fileWriter.write(s + "\n");
                }
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception ignored) {}
        } else {
            try {
                this.customKillsults = FileUtils.readLines(new File("monsoon/killsults.txt"));
            } catch (Exception ignored) {}
        }
    }

    private void addDefaultKillsults() {
        this.killsults.add("%s clearly doesn't use Monsoon.");
        this.killsults.add("What's wrong, %s? Can't click fast enough?");
        this.killsults.add("\"I don't hack, I Rise.\" yea rise dees nutz lmao");
        this.killsults.add("%s is so braindead, they could be a novoline user.");
        this.killsults.add("Hey %s, get Monsoon client and maybe you won't suck so much lmao");
        this.killsults.add("Monsoon > all. Get Monsoon, %s.");
        this.killsults.add("%s should really go purchase Monsoon client.");
        this.killsults.add("You can't run from Monsoon. You can't hide from Monsoon. But, you can get Monsoon!");
        this.killsults.add("%s git gud noob");
        this.killsults.add("%s is all \"L\" this and \"L\" that. How bout you L some b*tches?");
        this.killsults.add("rawr x3 nuzzwes pounces on %s uwu %s so wawm");
        this.killsults.add("Childhood obesity is an epidemic, %s needs help.");
        this.killsults.add("%s, don't you know you can only win by using Monsoon?");
        this.killsults.add("%s uses vape but still screams \"LLLLLL\" when they see a cheater. Not cool.");
        this.killsults.add("i just banged %s's mum");
        this.killsults.add("%s more like total fricking noob lmao gottem");
        this.killsults.add("%s's brain is smaller than a singularity.");
        this.killsults.add("Come on, %s. Just get Monsoon already. We know you want to.");
        this.killsults.add("Monsoon > %s");

        this.furrysults.add("rawr x3 nuzzwes pounces on %s uwu they so wawm");
        this.furrysults.add("%s just got destroyed by a furry");
        this.furrysults.add("%s UwU");
        this.furrysults.add("%s OwO");
        this.furrysults.add("UwU %s");
        this.furrysults.add("OwO %s");
        this.furrysults.add("%s haiiiii :3 hiiiii >~< haiiiii :33333");
        this.furrysults.add("%s is a furry");
        this.furrysults.add("%s browses e621");
        this.furrysults.add("hey %s, want to get in fursuits and breed? ;3");
        this.furrysults.add("%s is definitely a bottom");
        this.furrysults.add("would %s top me? pwease? >~<");
        this.furrysults.add("%s meow");
        this.furrysults.add("%s owns multiple baddragon products");
    }

    public void triggerKillsult(String msg) {
        EntityPlayer lastHit = this.getLastHit(msg);
        if(lastHit != null) {
            String message = killsults.remove(0);
            switch (mode.getValue()) {
                case DEFAULT:
                    message = killsults.remove(0);
                    break;
                case FURRY:
                    message = furrysults.remove(0);
                    break;
                case CUSTOM:
                    message = customKillsults.remove(0);
                    break;
                case MONSOON:
                    message = "monsoon\u05FC.wtf > %s";
                    break;
            }
            if(message.contains("%s")) {
                message = String.format(message, lastHit.getCommandSenderName());
            }
            if(ServerUtil.isHypixel())
                mc.thePlayer.sendChatMessage("/ac " + message);
            else
                mc.thePlayer.sendChatMessage(message);

            killsults.add(message);
            this.attackedCache.remove(lastHit);
            lastHit = null;
        }
    }

    private EntityPlayer getLastHit(String msg) {
        for(EntityPlayer x : this.attackedCache) {
            if(msg.contains(x.getCommandSenderName())) return x;
        }
        return null;
    }

    enum Mode{
        DEFAULT,
        FURRY,
        MONSOON,
        CUSTOM
    }
}