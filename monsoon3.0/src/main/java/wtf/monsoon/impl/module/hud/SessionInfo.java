package wtf.monsoon.impl.module.hud;

import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.font.IFontRenderer;
import wtf.monsoon.api.util.misc.DateUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.DrawUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SessionInfo extends HUDModule {

    private Setting<SessionInfoTheme> theme = new Setting<>("Theme", SessionInfoTheme.NEW)
            .describedBy("Them of the SessionInfo");

    public SessionInfo() {
        super("Session Info", "Shows information about your current session", 4, 24);
    }

    public int kills = 0, deaths = 0;
    private boolean hasRecordedDeath;

    @Override
    public void render() {
        switch (theme.getValue()) {
            case NEW:
                this.renderNewSessionInfo("render");
                break;
            case OLD:
                this.renderOldSessionInfo("render");
                break;
        }
    }

    @Override
    public void blur() {
        switch (theme.getValue()) {
            case NEW:
                this.renderNewSessionInfo("blur");
                break;
            case OLD:
                this.renderOldSessionInfo("blur");
                break;
        }
    }

    private void renderOldSessionInfo(String stage) {
        switch(stage) {
            case "render":
                //RenderUtil.getDefaultHudRenderer(this);
                long currentTime = System.currentTimeMillis() - Wrapper.getSessionTime();

                long hours = TimeUnit.MILLISECONDS.toHours(currentTime);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentTime));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime));

                StringBuilder stringBuilder = new StringBuilder();

                if(hours > 0) {
                    stringBuilder.append(hours).append("h ");
                }

                if(minutes > 0) {
                    stringBuilder.append(minutes).append("m ");
                }

                if(seconds > 0) {
                    stringBuilder.append(seconds).append("s");
                }

                String time = stringBuilder.toString();

                IFontRenderer fr = Wrapper.getFontUtil().productSansSmall;
                Map<String, String> elements = new HashMap<>();
                elements.put("Welcome, " + Wrapper.getMonsoonAccount().getUsername() + "!", "");
                elements.put("Username", mc.thePlayer.getGameProfile().getName());
                elements.put("Kills",  kills + "");
                elements.put("Deaths", deaths + "");
                elements.put("KDR", (Math.max(1, kills) / Math.max(1, deaths)) + "");
                // elements.put("Ping", mc.getCurrentServerData() != null ? mc.getCurrentServerData().pingToServer + "ms" : "-1ms");
                elements.put("Session Time", time);

                AtomicReference<Float> y = new AtomicReference<>(getY() + 5.5f);

                DrawUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), new Color(0, 0, 0, 100).getRGB());
                DrawUtil.drawRect(getX() + 2, getY() + 2, getX() + getWidth() - 1, getY() + 3, Wrapper.getPallet().getMain().getRGB());

                elements.forEach((key, value) -> {
                    fr.drawString(key, getX() + 4, y.get(), Color.WHITE, false);
                    fr.drawString(value, getX() + getWidth() - fr.getStringWidth(value) - 4, y.get(), Color.WHITE, false);

                    y.set(y.get() + 10);
                });
                break;
            case "blur":
                //RoundedUtils.glRound(this, 0, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
                break;
        }

        if(mc.thePlayer.getHealth() <= 0 || mc.thePlayer.isDead) {
            if(!hasRecordedDeath) {
                deaths++;
                hasRecordedDeath = true;
            }
        } else {
            if(mc.thePlayer.ticksExisted < 10) {
                hasRecordedDeath = false;
            }
        }
    }


    private void renderNewSessionInfo(String stage) {
        switch(stage) {
            case "render":
                RenderUtil.getDefaultHudRenderer(this);
                long currentTime = System.currentTimeMillis() - Wrapper.getSessionTime();

                long hours = TimeUnit.MILLISECONDS.toHours(currentTime);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentTime));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime));

                StringBuilder stringBuilder = new StringBuilder();

                if(hours > 0) {
                    stringBuilder.append(hours).append("h ");
                }

                if(minutes > 0) {
                    stringBuilder.append(minutes).append("m ");
                }

                if(seconds > 0) {
                    stringBuilder.append(seconds).append("s");
                }

                String time = stringBuilder.toString();

                Map<String, String> elements = new HashMap<>();
                elements.put(FontUtil.UNICODES_UI.CLOCK, time);
                elements.put(FontUtil.UNICODES_UI.TAG, Wrapper.getMinecraft().getSession().getUsername());
                elements.put(FontUtil.UNICODES_UI.USER, mc.getCurrentServerData() != null ? mc.getCurrentServerData().pingToServer + "ms" : "-1ms");
                elements.put(FontUtil.UNICODES_UI.PLUS, kills + (kills == 1 ? " Kill" : " Kills"));

                AtomicReference<Float> y = new AtomicReference<>(getY() + 3);

                elements.forEach((icon, value) -> {
                    Wrapper.getFontUtil().productSans.drawString(value, getX() + getWidth() - Wrapper.getFontUtil().productSans.getStringWidth(value) - 8, y.get(), Color.WHITE, false);
                    Wrapper.getFontUtil().entypo18.drawString(icon, getX() + 8, y.get() + 2, Color.WHITE, false);

                    y.set(y.get() + ((getHeight() - 6) / elements.size()));
                });
                break;
            case "blur":
                RoundedUtils.glRound(this, 7, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
                break;
        }
    }

    @Override
    public float getWidth() {
        switch (theme.getValue()) {
            case NEW:
                float userWidth = Wrapper.getFontUtil().productSans.getStringWidth(Minecraft.getMinecraft().getSession().getUsername());
                return userWidth + 40 > 100 ? userWidth + 40 : 80;
            case OLD:
            default:
                return 116;
        }
    }

    @Override
    public float getHeight() {
        switch (theme.getValue()) {
            case NEW:
                return 60;
            case OLD:
            default:
                return 67.5f;
        }
    }

    enum SessionInfoTheme {
        NEW, OLD
    }
}