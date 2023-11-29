package wtf.monsoon.impl.module.hud;

import com.viaversion.viaversion.util.Pair;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.font.IFontRenderer;
import wtf.monsoon.api.util.render.BloomUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventBloom;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventRenderScoreboard;
import wtf.monsoon.impl.module.visual.Accent;

import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Surge
 * @since 30/12/2022
 */
public class HUDArrayList extends Module {

    public static Setting<Background> arrayListBackground = new Setting<>("Background", Background.NONE)
            .describedBy("How to draw the ArrayList's background");

    public static Setting<HUD.BasicTextElement> arrayListMetaData = new Setting<>("Metadata", HUD.BasicTextElement.DASH)
            .describedBy("How to draw the module metadata");

    public static Setting<AnimationEnum> arrayListAnimation = new Setting<>("Animation", AnimationEnum.SLIDE)
            .describedBy("How to draw the module animations");

    public static Setting<Float> elementHeight = new Setting<>("Height", 12f)
            .minimum(11f)
            .maximum(15f)
            .incrementation(1f)
            .describedBy("The height of each element");

    public static Setting<Boolean> arrayListTextShadow = new Setting<>("Text Shadow", true)
            .describedBy("Whether to draw the text shadow");

    public static Setting<Boolean> arrayListIgnoreVisuals = new Setting<>("Ignore Visuals", true)
            .describedBy("Whether to ignore visual modules");

    public static Setting<Outline> arrayListOutline = new Setting<>("Outline", Outline.NONE)
            .describedBy("How to outline the arraylist");

    public static Setting<Float> arrayListOpacity = new Setting<>("Opacity", 0f)
            .minimum(1f)
            .maximum(255f)
            .incrementation(1f)
            .describedBy("The opacity of the background")
            .visibleWhen(() -> arrayListBackground.getValue() == Background.CHILL || arrayListBackground.getValue() == Background.AMBIENT);

    public static Setting<EnumFont> font = new Setting<>("Font", EnumFont.PRODUCT_SANS)
            .describedBy("What font to use");

    public static Setting<Boolean> lowercase = new Setting<>("Lowercase", false)
            .describedBy("Make the text forced lowercase");

    private float arrayListHeight = 0;

    public HUDArrayList() {
        super("Array List", "yea", Category.HUD);
    }

    @EventLink
    private final Listener<EventRender2D> render2DListener = event -> {
        if (!mc.gameSettings.showDebugInfo) {
            float topRightOffset = 4f;

            List<Module> sortedModules = Wrapper.getMonsoon().getModuleManager().getModules().stream()
                    .filter(module -> arrayListAnimation.getValue() == AnimationEnum.NONE ? module.isEnabled() : module.getAnimation().getAnimationFactor() > 0.0)
                    .filter(module -> !arrayListIgnoreVisuals.getValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.HUD))
                    .filter(module -> !module.isDuplicate())
                    .filter(module -> module.isVisible())
                    .sorted(Comparator.comparingDouble(module -> generateModuleDataAndWidth((Module) module).key()).reversed())
                    .collect(Collectors.toList());

            int index = 0;

            for (Module module : sortedModules) {
                glPushMatrix();

                Pair<Float, String> data = generateModuleDataAndWidth(module);

                float x = event.getSr().getScaledWidth() - data.key() - 8;
                float height = elementHeight.getValue();
                float animationFactor = (float) (arrayListAnimation.getValue() == AnimationEnum.NONE ? 1.0 : module.getAnimation().getAnimationFactor());

                Color colour = generateColour(index);
                Color nextColour = generateColour(index + 1);

                boolean scissored = false;

                switch (arrayListBackground.getValue()) {
                    case CHILL: {
                        RenderUtil.rect(x, topRightOffset, data.key() + 4, height * animationFactor, new Color(0, 0, 0, arrayListOpacity.getValue().intValue()));
                        break;
                    }

                    case AMBIENT: {
                        RenderUtil.verticalGradient(x, topRightOffset, data.key() + 4, height * animationFactor, ColorUtil.integrateAlpha(colour, arrayListOpacity.getValue().intValue()), ColorUtil.integrateAlpha(nextColour, arrayListOpacity.getValue().intValue()));
                        break;
                    }
                }

                switch (arrayListAnimation.getValue()) {
                    case SLIDE: {
                        if (module.getAnimation().getAnimationFactor() != 1.0) {
                            RenderUtil.pushScissor(x, topRightOffset, data.key() + 4f, height);
                            scissored = true;
                        }

                        glTranslated((data.key() + 4) * (1 - animationFactor), 0, 0);

                        break;
                    }

                    case SCALE: {
                        RenderUtil.scale(x, topRightOffset, new float[]{ 1f, animationFactor });

                        break;
                    }

                    case SCISSOR: {
                        if (module.getAnimation().getAnimationFactor() != 1.0) {
                            RenderUtil.pushScissor(x, topRightOffset, data.key() + 4f, height * animationFactor);
                            scissored = true;
                        }

                        break;
                    }
                }

                font.getValue().font.drawString(data.value(), x + 2f, topRightOffset + ((height / 2f) - (font.getValue().font.getHeight() / 2f)) - (font.getValue() == EnumFont.MINECRAFT ? 0f : 1f), arrayListBackground.getValue() == Background.AMBIENT ? Color.WHITE : colour, arrayListTextShadow.getValue());

                if (scissored) {
                    RenderUtil.popScissor();
                }

                glPopMatrix();

                switch (arrayListOutline.getValue()) {
                    case TOP: {
                        if (index == 0) {
                            RenderUtil.rect(x, topRightOffset, data.key() + 4f, 1f, colour);
                        }

                        break;
                    }

                    case RIGHT: {
                        RenderUtil.verticalGradient(x + data.key() + 4f, topRightOffset, 1f, height, colour, nextColour);

                        break;
                    }

                    case TOP_RIGHT: {
                        RenderUtil.verticalGradient(x + data.key() + 4f, topRightOffset, 1f, height, colour, nextColour);

                        if (index == 0) {
                            RenderUtil.rect(x, topRightOffset - 1f, data.key() + 5f, 1f, colour);
                        }

                        break;
                    }

                    case LEFT: {
                        RenderUtil.verticalGradient(x - 1f, topRightOffset, 1f, height * animationFactor, colour, nextColour);
                        break;
                    }

                    case FULL: {
                        RenderUtil.verticalGradient(x - 1f, topRightOffset, 1f, height * animationFactor, colour, nextColour);
                        RenderUtil.verticalGradient(x + data.key() + 4f, topRightOffset, 1f, height, colour, nextColour);

                        if (index == 0) {
                            RenderUtil.rect(x - 1f, topRightOffset - 1f, data.key() + 6f, 1f, colour);
                        } else if (index == sortedModules.size() - 1) {
                            RenderUtil.rect(x - 1f, topRightOffset + height, data.key() + 6f, 1f, nextColour);
                        }

                        if (index != sortedModules.size() - 1) {
                            RenderUtil.verticalGradient(x - 1f, (float) (topRightOffset + (height * module.getAnimation().getAnimationFactor())), data.key() - generateModuleDataAndWidth(sortedModules.get(index + 1)).key(), 1f, colour, nextColour);
                        }

                        break;
                    }
                }

                topRightOffset += elementHeight.getValue() * animationFactor;

                if(index >= sortedModules.size()) {
                    //arrayListHeight = topRightOffset + (height * index);
                }

                index++;
            }

            arrayListHeight = 4f + (elementHeight.getValue() * (index + 2));
        }
    };

    @EventLink
    private final Listener<EventRenderScoreboard> eventRenderScoreboardListener = e -> {
        while (e.getY() <= this.arrayListHeight) {
            return;
        }
    };

    private Pair<Float, String> generateModuleDataAndWidth(Module module) {
        StringBuilder text = new StringBuilder()
                        .append(module.getName());

        if (!module.getMetaData().equals("")) {
            switch (arrayListMetaData.getValue()) {
                case SIMPLE: {
                    text.append(" ")
                        .append(EnumChatFormatting.GRAY)
                        .append(module.getMetaData());

                    break;
                }

                case SQUARE: {
                    text.append(" ")
                        .append(EnumChatFormatting.GRAY)
                        .append("[")
                        .append(EnumChatFormatting.WHITE)
                        .append(module.getMetaData())
                        .append(EnumChatFormatting.GRAY)
                        .append("]");

                    break;
                }

                case DASH: {
                    text.append(" ")
                        .append(EnumChatFormatting.GRAY)
                        .append("- ")
                        .append(module.getMetaData());

                    break;
                }
            }
        }

        String finalText = text.toString();

        if (lowercase.getValue()) {
            finalText = finalText.toLowerCase(Locale.getDefault());
        }

        return new Pair<>(font.getValue().font.getStringWidthF(finalText), finalText);
    }

    private Color generateColour(int index) {
        Color combined = ColorUtil.fadeBetween(5, index * 15, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);

        Accent.EnumAccents enumeration = Wrapper.getModule(Accent.class).accents.getValue();

        if (enumeration.equals(Accent.EnumAccents.ASTOLFO)) {
            combined = ColorUtil.astolfoColorsC(index * 5, index * 20);
        } else if (enumeration.equals(Accent.EnumAccents.RAINBOW)) {
            combined = ColorUtil.rainbow(index * 300L);
        } else if (enumeration.equals(Accent.EnumAccents.EXHIBITION)) {
            combined = ColorUtil.exhibition(index * 300L);
        }

        return combined;
    }

    @AllArgsConstructor
    enum EnumFont {
        PRODUCT_SANS("Product Sans", Wrapper.getFontUtil().productSans, 2, 3),
        PRODUCT_SANS_MEDIUM("Product Sans Medium", Wrapper.getFontUtil().productSansMedium, 2, 3),
        PRODUCT_SANS_BOLD("Product Sans Bold", Wrapper.getFontUtil().productSansBold, 8, 2),
        PRODUCT_SANS_SMALL("Product Sans Small", Wrapper.getFontUtil().productSansSmall, 2, 1),
        PRODUCT_SANS_SMALL_BOLD("Product Sans Small Bold", Wrapper.getFontUtil().productSansSmallBold, 2, 1),

        COMIC_SANS("Comic Sans", Wrapper.getFontUtil().comicSans, 2, 3),
        COMIC_SANS_MEDIUM("Comic Sans Medium", Wrapper.getFontUtil().comicSansMedium, 2, 3),
        COMIC_SANS_SMALL("Comic Sans Small", Wrapper.getFontUtil().comicSansSmall, 2, 1),
        COMIC_SANS_BOLD("Comic Sans Bold", Wrapper.getFontUtil().comicSansBold, 8, 2),
        COMIC_SANS_MEDIUM_BOLD("Comic Sans Mediun Bold", Wrapper.getFontUtil().comicSansMediumBold, 8, 2),
        COMIC_SANS_SMALL_BOLD("Comic Sans Small Bold", Wrapper.getFontUtil().comicSansSmallBold, 2, 1),

        GREYCLIFF("Greycliff", Wrapper.getFontUtil().greycliff19, 2, 1),
        MINECRAFT("Minecraftia", Minecraft.getMinecraft().fontRendererObj, 4, 2),

        UW_U("uwu", Wrapper.getFontUtil().ubuntuwu, 2, 3),
        UW_U_MEDIUM("uwu medium", Wrapper.getFontUtil().ubuntuwuMedium, 2, 3),
        UW_U_SMALL("uwu smawl", Wrapper.getFontUtil().ubuntuwuSmall, 2, 1),;

        String fontName;
        IFontRenderer font;
        int xOffset, yOffset;

        @Override
        public String toString() {
            return fontName;
        }
    }

    enum Outline {
        TOP, RIGHT, TOP_RIGHT, LEFT, FULL, NONE
    }

    public enum Background {
        CHILL, AMBIENT, NONE
    }

    public enum AnimationEnum {
        SCISSOR, SLIDE, SCALE, NONE
    }

}
