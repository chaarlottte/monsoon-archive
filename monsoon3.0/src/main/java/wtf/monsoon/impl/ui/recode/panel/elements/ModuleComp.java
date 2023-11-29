package wtf.monsoon.impl.ui.recode.panel.elements;

import lombok.NonNull;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.SettingComp;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.impl.*;

import java.awt.*;
import java.util.ArrayList;

public class ModuleComp extends Comp {
    Module module;
    public Animation expanded = new Animation(() -> 200F, false, () -> Easing.CIRC_IN_OUT);
    Animation toggle = new Animation(() -> 200F, false, () -> Easing.LINEAR);
    ArrayList<SettingComp> settingComps = new ArrayList<>();
    public ModuleComp(Module module, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(x, y, w, h);
        this.module = module;
        for (Setting<?> s : module.getSettings()) {
            if(s.getValue() instanceof Boolean)
                settingComps.add(new BooleanSettingComp((Setting<Boolean>) s,0,0,w,h));
            else if(s.getValue() instanceof Bind)
                settingComps.add(new BindSettingComp((Setting<Bind>) s,0,0,w,h));
            else if(s.getValue() instanceof Number)
                settingComps.add(new NumberSettingComp((Setting<Number>) s,0,0,w,h));
            else if(s.getValue() instanceof Enum)
                settingComps.add(new EnumSettingComp((Setting<Enum>) s,0,0,w,h));
            else
                settingComps.add(new SettingComp(s, 0, 0, w, h));
        }
    }

    @Override
    public void render(float mx, float my) {
        toggle.setState(module.isEnabled());

        float maxH = 0;
        for (SettingComp settingComp : settingComps) {
            if(settingComp.getSetting().isVisible()) {
                maxH += settingComp.getH();
            }
        }

        if(expanded.getAnimationFactor() > 0.001) {
            float i = y+h;
            for (SettingComp settingComp : settingComps) {
                if(settingComp.getSetting().isVisible()) {
                    settingComp.setX(x).setY(i).render(mx, my);
                    i += settingComp.getH();
                }
            }
        }

        Color text = ColorUtil.interpolate(new Color(0x9A9A9A), Color.WHITE, toggle.getAnimationFactor());
        Color[] rect = {
                ColorUtil.interpolate(new Color(0x212121), ColorUtil.getAccent()[0], toggle.getAnimationFactor()),
                ColorUtil.interpolate(new Color(0x212121), ColorUtil.getAccent()[1], toggle.getAnimationFactor())
        };

        ui.rect(x,y,w,h, new Color(0x161616));
        ui.roundedLinearGradient(x+w-4,y+4,2,h-8, 2,rect[0],rect[1]);
        ui.text(module.getName(), "regular", 15, x+w/2f, y+h/2f+1, text, 2 | 16);
    }

    @Override
    public void click(float mx, float my, int button) {
        if(hovered()) {
            if(button == 0) module.toggle();
            if(button == 1) expanded.setState(!expanded.getState());
        }
        if(expanded.getAnimationFactor() > 0.001) {
            for (SettingComp settingComp : settingComps) {
                if(settingComp.getSetting().isVisible())
                    settingComp.click(mx, my, button);
            }
        }
    }

    @Override
    public void key(int code, char c) {
        if(expanded.getAnimationFactor() > 0.001)
            settingComps.forEach(cc -> cc.key(code, c));
        super.key(code, c);
    }

    public float getAbsoluteHeight() {
        float i = 0;
        for (SettingComp settingComp : settingComps) {
            if(settingComp.getSetting().isVisible())
                i+=settingComp.getH();
        }

        return h + (i * (float) expanded.getAnimationFactor());
    }
}
