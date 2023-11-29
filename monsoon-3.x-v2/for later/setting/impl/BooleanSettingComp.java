package wtf.monsoon.client.ui.panel.elements.setting.impl;

import lombok.NonNull;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.SettingComp;

import java.awt.*;

public class BooleanSettingComp extends SettingComp<Boolean> {
    Animation toggle = new Animation(() -> 200F, false, () -> Easing.LINEAR);
    public BooleanSettingComp(Setting<Boolean> setting, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(setting, x, y, w, h);
    }

    @Override
    public void render(float mx, float my) {
        toggle.setState(getSetting().getValue());
        super.render(mx, my);
        ui.round(x+w-24-2, y+h/2f-8-2, 16+4, 16+4, 7, new Color(38, 38, 38));
        ui.round(x+w-24+1-2, y+h/2f-8+1-2, 16-2+4, 16-2+4, 7-1, new Color(30, 30, 30));
        if(getSetting().getChildren().isEmpty())
            ui.text(getSetting().getName(), "regular", 14, x+10,y+h/2f, Color.WHITE, NanoVG.NVG_ALIGN_MIDDLE);
        ui.text("k", "entypo", (int) (13*toggle.getAnimationFactor()), x+w-24+8+1, y+h/2f+1, ColorUtil.interpolate(new Color(26,26,26), ColorUtil.getAccent()[0], toggle.getAnimationFactor()), 2 | 16);
    }

    @Override
    public void click(float mx, float my, int button) {
        super.click(mx, my, button);
        if(hovered() && button == 0) {
            getSetting().setValue(!getSetting().getValue());
        }
    }
}
