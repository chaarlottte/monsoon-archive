package wtf.monsoon.impl.ui.recode.panel.elements.setting.impl;

import lombok.NonNull;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.SettingComp;

import java.awt.*;

public class BindSettingComp extends SettingComp<Bind> {
    Animation bind = new Animation(200F, false, Easing.LINEAR);
    public BindSettingComp(Setting<Bind> setting, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(setting, x, y, w, h);
    }

    @Override
    public void render(float mx, float my) {
        ui.rect(x,y,w,h, new Color(26,26,26));

        String bindText = "Bind: " + (bind.getState() ? "..." : getSetting().getValue().getButtonName());
        float bindWidth = ui.textWidth(bindText, "regular", 12) + 10;

        ui.round(x+w/2f-bindWidth/2f,y+6,bindWidth,h-12, 7, new Color(38, 38, 38));
        ui.round(x+w/2f-bindWidth/2f+1,y+6+1,bindWidth-2,h-12-2, 7-1, ColorUtil.interpolate(new Color(30, 30, 30), new Color(30, 30, 30).brighter(), bind.getAnimationFactor()));
        ui.text(bindText, "regular", 12, x+w/2f+1, y+h/2f+1, Color.WHITE,NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);

        ui.text(FontUtil.UNICODES_UI.TRASH, "entypo", 16, x+w-16, y+h/2f, hovered(x+w-16-8, y+h/2f-8, 16, 16) ? Color.WHITE.darker() : new Color(30, 30, 30).brighter(), NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
    }

    @Override
    public void click(float mx, float my, int button) {
        super.click(mx, my, button);

        String bindText = "Bind: " + (bind.getState() ? "..." : getSetting().getValue().getButtonName());
        float bindWidth = ui.textWidth(bindText, "regular", 12) + 10;

        if(hovered(x+w/2f-bindWidth/2f,y+6,bindWidth,h-12) && button == 0) {
            bind.setState(true);
        }

        if(hovered(x+w-16-8, y+h/2f-8, 16, 16) && button == 0) {
            getSetting().getValue().setButtonCode(0);
        }

        if(!hovered()) bind.setState(false);
    }

    @Override
    public void key(int code, char c) {
        if(bind.getState()) {
            getSetting().setValue(new Bind(code, Bind.Device.KEYBOARD));
            bind.setState(false);
        }
        super.key(code, c);
    }
}
