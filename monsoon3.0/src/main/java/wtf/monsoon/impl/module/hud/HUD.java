package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventRender2D;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Surge
 * @since 29/12/2022
 */
public class HUD extends Module {

    public static Setting<Watermark> watermark = new Setting<>("Watermark", Watermark.LOGO)
            .describedBy("How to draw the watermark");

    public static Setting<BasicTextElement> coordinates = new Setting<>("Coordinates", BasicTextElement.SIMPLE)
            .describedBy("How to draw your coordinates");

    public static Setting<BasicTextElement> speed = new Setting<>("Speed", BasicTextElement.SIMPLE)
            .describedBy("How to draw your speed");

    public static Setting<BasicTextElement> build = new Setting<>("Build", BasicTextElement.SIMPLE)
            .describedBy("How to draw the client's build");

    public static Setting<BasicTextElement> uid = new Setting<>("UID", BasicTextElement.SIMPLE)
            .describedBy("How to draw your UID");

    public static Setting<BasicTextElement> time = new Setting<>("Time", BasicTextElement.SIMPLE)
            .describedBy("How to draw the time");

    public Setting<Object> hudModuleOptions = new Setting<>("Draggables", new Object())
            .describedBy("Settings for the draggable HUD modules");

    public Setting<Boolean> hudModuleOutline = new Setting<>("Outline", true)
            .describedBy("Outline the HUD modules")
            .childOf(hudModuleOptions);

    public Setting<Boolean> hudModuleShadow = new Setting<>("Shadow", false)
            .describedBy("Draw a shadow around the HUD modules")
            .childOf(hudModuleOptions);

    public Setting<Boolean> hudModuleBackground = new Setting<>("Background", true)
            .describedBy("Draw a background on the HUD modules")
            .childOf(hudModuleOptions);

    public Setting<Boolean> blur = new Setting<>("Blur", false)
            .describedBy("Blurs various interfaces");

    public Setting<Integer> blurStrength = new Setting<>("Intensity", 6)
            .minimum(1)
            .maximum(10)
            .incrementation(1)
            .describedBy("How intense the blur is")
            .childOf(blur);

    public HUD() {
        super("HUD", "uhhhh hud you know what it does", Category.HUD);
    }

    @EventLink
    private final Listener<EventRender2D> render2DListener = event -> {
        if (!mc.gameSettings.showDebugInfo) {
            float topLeftOffset = 2f;
            float bottomLeftOffset = event.getSr().getScaledHeight() - Wrapper.getFontUtil().productSans.getHeight() - 2f;
            float bottomRightOffset = event.getSr().getScaledHeight() - Wrapper.getFontUtil().productSans.getHeight() - 2f;

            // watermark
            switch (watermark.getValue()) {
                case CSGO: {
                    String text = (EnumChatFormatting.WHITE + "Monsoon" + EnumChatFormatting.RESET + "sense " + EnumChatFormatting.WHITE + Wrapper.getMonsoon().getVersion() + " - " + Wrapper.getMonsoonAccount().getUsername() + " - " + Minecraft.getDebugFPS() + " fps").toLowerCase();
                    float width = Wrapper.getFontUtil().productSansSmall.getStringWidth(text);

                    RenderUtil.rect(2f, topLeftOffset, width + 13, 16.5f, new Color(40, 40, 40, 255));

                    topLeftOffset += 2f;

                    RenderUtil.rect(4f, topLeftOffset, width + 9, 12.5f, new Color(15, 15, 15, 255));

                    topLeftOffset += 1f;

                    Wrapper.getFontUtil().productSansSmallBold.drawString(text, 6, topLeftOffset, new Color(getColor()), false);

                    topLeftOffset += 10.5f;

                    for (int i = 0; i < width + 9; i++) {
                        RenderUtil.rect(4 + i, topLeftOffset, 1, 1f, new Color(getColor()));
                    }

                    break;
                }

                case SIMPLE: {
                    Wrapper.getFontUtil().productSans.drawStringWithShadow("M" + EnumChatFormatting.WHITE + "onsoon " + Wrapper.getMonsoon().getVersion() + EnumChatFormatting.DARK_GRAY + " (" + EnumChatFormatting.WHITE + Minecraft.getDebugFPS() + " FPS" + EnumChatFormatting.DARK_GRAY + ")", 2f, topLeftOffset, new Color(getColor()));

                    topLeftOffset += Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case LOGO: {
                    mc.getTextureManager().bindTexture(new ResourceLocation("monsoon/utterly insane logo.png"));
                    RenderUtil.renderTexture(2f, 2f, (int) (517 / 4f), (int) (286 / 4f));

                    topLeftOffset += 286 / 4f;

                    break;
                }

                case LOGO2: {
                    mc.getTextureManager().bindTexture(new ResourceLocation("monsoon/utterly insane logo2.png"));
                    RenderUtil.renderTexture(2f, 2f, (int) (302 / 2f), (int) (217 / 2f));

                    topLeftOffset += 286 / 4f;

                    break;
                }
            }

            // coordinates
            switch (coordinates.getValue()) {
                case SIMPLE: {
                    String text = "X: " + EnumChatFormatting.WHITE + (int) mc.thePlayer.posX + EnumChatFormatting.RESET + " " + "Y: " + EnumChatFormatting.WHITE + (int) mc.thePlayer.posY + EnumChatFormatting.RESET + " " + "Z: " + EnumChatFormatting.WHITE + (int) mc.thePlayer.posZ + EnumChatFormatting.RESET + " ";

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2f, bottomLeftOffset, new Color(getColor()));

                    bottomLeftOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case SQUARE: {
                    String text = "XYZ " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + (int) mc.thePlayer.posX + ", " + (int) mc.thePlayer.posY + ", " + (int) mc.thePlayer.posZ + EnumChatFormatting.GRAY + "]";

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2f, bottomLeftOffset, new Color(getColor()));

                    bottomLeftOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case DASH: {
                    String text = "XYZ " + EnumChatFormatting.GRAY + "- " + (int) mc.thePlayer.posX + ", " + (int) mc.thePlayer.posY + ", " + (int) mc.thePlayer.posZ;

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2f, bottomLeftOffset, new Color(getColor()));

                    bottomLeftOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }
            }

            // speed
            switch (speed.getValue()) {
                case SIMPLE: {
                    String text = "BPS " + EnumChatFormatting.WHITE + new DecimalFormat("#.##").format(PlayerUtil.getPlayerSpeed());

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2f, bottomLeftOffset, new Color(getColor()));

                    bottomLeftOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case SQUARE: {
                    String text = "BPS " + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + new DecimalFormat("#.##").format(PlayerUtil.getPlayerSpeed()) + EnumChatFormatting.GRAY + "]";

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2f, bottomLeftOffset, new Color(getColor()));

                    bottomLeftOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case DASH: {
                    String text = "BPS " + EnumChatFormatting.GRAY + "- " + new DecimalFormat("#.##").format(PlayerUtil.getPlayerSpeed());

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text, 2f, bottomLeftOffset, new Color(getColor()));

                    bottomLeftOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }
            }

            String timeString = DateTimeFormatter.ofPattern("h:mm").format(LocalDateTime.now()) + " " + DateTimeFormatter.ofPattern("a").format(LocalDateTime.now());

            // time
            switch (time.getValue()) {
                case SIMPLE: {
                    StringBuilder text = new StringBuilder()
                            .append("Time ")
                            .append(EnumChatFormatting.WHITE)
                            .append(timeString);

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case SQUARE: {
                    StringBuilder text = new StringBuilder()
                            .append("Time ")
                            .append(EnumChatFormatting.GRAY)
                            .append("[")
                            .append(EnumChatFormatting.WHITE)
                            .append(timeString)
                            .append(EnumChatFormatting.GRAY)
                            .append("]");

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case DASH: {
                    StringBuilder text = new StringBuilder()
                            .append("Time ")
                            .append(EnumChatFormatting.GRAY)
                            .append("- ")
                            .append(timeString);

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }
            }

            // build
            switch (build.getValue()) {
                case SIMPLE: {
                    StringBuilder text = new StringBuilder()
                            .append("Build ")
                            .append(EnumChatFormatting.WHITE)
                            .append(Wrapper.getMonsoon().getVersion());

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case SQUARE: {
                    StringBuilder text = new StringBuilder()
                            .append("Build ")
                            .append(EnumChatFormatting.GRAY)
                            .append("[")
                            .append(EnumChatFormatting.WHITE)
                            .append(Wrapper.getMonsoon().getVersion())
                            .append(EnumChatFormatting.GRAY)
                            .append("]");

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case DASH: {
                    StringBuilder text = new StringBuilder()
                            .append("Build ")
                            .append(EnumChatFormatting.GRAY)
                            .append("- ")
                            .append(Wrapper.getMonsoon().getVersion());

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }
            }

            // uid
            switch (uid.getValue()) {
                case SIMPLE: {
                    StringBuilder text = new StringBuilder()
                            .append("UID ")
                            .append(EnumChatFormatting.WHITE)
                            .append(Wrapper.getMonsoonAccount().getUid());

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case SQUARE: {
                    StringBuilder text = new StringBuilder()
                            .append("UID ")
                            .append(EnumChatFormatting.GRAY)
                            .append("[")
                            .append(EnumChatFormatting.WHITE)
                            .append(Wrapper.getMonsoonAccount().getUid())
                            .append(EnumChatFormatting.GRAY)
                            .append("]");

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }

                case DASH: {
                    StringBuilder text = new StringBuilder()
                            .append("UID ")
                            .append(EnumChatFormatting.GRAY)
                            .append("- ")
                            .append(Wrapper.getMonsoonAccount().getUid());

                    Wrapper.getFontUtil().productSans.drawStringWithShadow(text.toString(), event.getSr().getScaledWidth() - Wrapper.getFontUtil().productSans.getStringWidth(text.toString()) - 2f, bottomRightOffset, new Color(getColor()));

                    bottomRightOffset -= Wrapper.getFontUtil().productSans.getHeight();

                    break;
                }
            }
        }
    };

    public int getColor() {
        return ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme()[0].getRGB(), ColorUtil.getClientAccentTheme()[1].getRGB(), System.currentTimeMillis() % 1500 / (1500 / 2.0f));
    }

    public int getColorForArray(int yOffset, int yTotal) {
        return ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme(yOffset, yTotal)[0].getRGB(), ColorUtil.getClientAccentTheme(yOffset, yTotal)[1].getRGB(), (System.currentTimeMillis() + yTotal * 3) % 1500 / (1500 / 2.0f));
    }

    public int getColorForArray2(int yOffset, int yTotal) {
        return ColorUtil.fadeBetween(ColorUtil.getClientAccentTheme()[0].getRGB(), ColorUtil.getClientAccentTheme()[1].getRGB(), (System.currentTimeMillis() + yTotal * 3) % 1500 / (1500 / 2.0f));
    }

    public enum Watermark {
        CSGO,
        SIMPLE,
        LOGO,
        LOGO2,
        NONE
    }

    public enum BasicTextElement {
        SIMPLE,
        SQUARE,
        DASH,
        NONE
    }

}