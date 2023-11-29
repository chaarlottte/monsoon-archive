package wtf.monsoon.impl.ui.recode.panel.elements.setting;

import lombok.Getter;
import lombok.NonNull;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.panel.elements.setting.ElementSetting;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.impl.BindSettingComp;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.impl.BooleanSettingComp;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.impl.EnumSettingComp;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.impl.NumberSettingComp;

import java.awt.*;
import java.util.ArrayList;

public class SettingComp<T> extends Comp {
    @Getter
    Setting<T> setting;
    public Animation expanded = new Animation(() -> 200F, false, () -> Easing.CIRC_IN_OUT);
    ArrayList<SettingComp> settingComps = new ArrayList<>();
    public SettingComp(Setting<T> setting, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(x, y, w, h);
        this.setting = setting;
        for (Setting<?> s : setting.getChildren()) {
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
        int c = 26;
        ui.rect(x,y,w,getH(), new Color(c,c,c));

        if(!settingComps.isEmpty()) {
            ui.text(""+settingComps.size(), "regular", 14, x+10,y+h/2f, Color.WHITE, NanoVG.NVG_ALIGN_MIDDLE);
            ui.text(getSetting().getName(), "regular", 14, x+26,y+h/2f, Color.WHITE, NanoVG.NVG_ALIGN_MIDDLE);
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
    }

    @Override
    public void click(float mx, float my, int button) {
        if(hovered()) {
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

    @Override
    public float getH() {
        float i = 0;
        for (SettingComp settingComp : settingComps) {
            if(settingComp.getSetting().isVisible())
                i+=settingComp.getH();
        }

        return h + (i * (float) expanded.getAnimationFactor());
    }
}
