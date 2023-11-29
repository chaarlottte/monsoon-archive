package wtf.monsoon.impl.module.visual;

import lombok.Getter;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;

import java.awt.*;

public class Accent extends Module {
    public Setting<EnumAccents> accents = new Setting<EnumAccents>("Accent", EnumAccents.MONSOON_NEW).describedBy(getDescription());

    public Setting<Color> customColor1 = new Setting<Color>("Color", new Color(-1)).describedBy("Custom color either for static or fade accent")
            .visibleWhen(() -> accents.getValue().equals(EnumAccents.STATIC) | accents.getValue().equals(EnumAccents.FADE) | accents.getValue().equals(EnumAccents.FADE_STATIC));

    public Setting<Color> customColor2 = new Setting<Color>("Color 2", new Color(-1)).describedBy("Custom color either for static or fade accent")
            .visibleWhen(() -> accents.getValue().equals(EnumAccents.FADE));

    public Accent() {
        super("Accent", "Client accent color", Category.HUD);
    }

    public enum EnumAccents {
        MONSOON_NEW(new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
        MONSOON_OLD(new Color(0, 140, 255), new Color(0, 255, 255)),
        ASTOLFO(ColorUtil.astolfoColorsC(0, 100), ColorUtil.astolfoColorsC(0, 100)),
        COTTON_CANDY(new Color(91, 206, 250), new Color(245, 169, 184)),
        RAINBOW(ColorUtil.rainbow(0L), ColorUtil.rainbow(500L), ColorUtil.rainbow(1000L), ColorUtil.rainbow(1500L)),
        EXHIBITION(ColorUtil.exhibition(0L), ColorUtil.exhibition(500L), ColorUtil.exhibition(1000L), ColorUtil.exhibition(1500L)),
        FADE(new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
        FADE_STATIC(new Color(0, 238, 255, 255), new Color(0, 238, 255, 255).darker().darker().darker().darker()),
        STATIC(new Color(0, 238, 255, 255), new Color(0, 238, 255, 255));

        @Getter
        Color[] clrs;

        EnumAccents(Color... clrs) {
            this.clrs = clrs;
        }
    }
}
