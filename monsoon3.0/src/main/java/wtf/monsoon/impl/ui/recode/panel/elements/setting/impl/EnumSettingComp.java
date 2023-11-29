package wtf.monsoon.impl.ui.recode.panel.elements.setting.impl;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.SettingComp;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class EnumSettingComp extends SettingComp<Enum> {
    Animation expanded = new Animation(() -> 200F, false, () -> Easing.CIRC_IN_OUT);
    ArrayList<Button> buttons = new ArrayList<>();
    String[] values;
    float dH;
    public EnumSettingComp(Setting<Enum> setting, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(setting, x, y, w, h);
        dH = h;
        Enum<?> enumeration = setting.getValue();
        values = Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);
        for (int i = 0; i < values.length; i++) {
            buttons.add(new Button(i,0, 0, w-8, h-12));
        }
    }

    @Override
    public void render(float mx, float my) {
        ui.rect(x,y,w,getH(), new Color(26,26,26));
        ui.round(x+4,y+4,w-7,getH()-8, 7, new Color(38, 38, 38));
        ui.round(x+4+1,y+4+1,w-7-2,getH()-8-2, 7-1, new Color(30, 30, 30));
        ui.text(getSetting().getName(), "regular", 15, x+w/2f+1, y+h/2f+1, Color.WHITE, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);

        int i = 0;
        for (Button button : buttons) {
            button.setX(x+4).setY(y+4+(h-8)+(h-12)*i*(float)expanded.getAnimationFactor()).render(mx, my);
            i++;
        }

        ui.rect(x+8, y+4+(h-8)-2, w-16, 1, ColorUtil.interpolate(new Color(30, 30, 30),new Color(38, 38, 38),expanded.getAnimationFactor()));
    }

    @Override
    public void click(float mx, float my, int button) {
        super.click(mx, my, button);
        if(hovered(x+4,y+4,w-7,h-8) && button == 0)
            expanded.setState(!expanded.getState());


        if(expanded.getState()) {
            for (Button button1 : buttons) {
                button1.click(mx, my, button);
            }
        }
    }

    @Override
    public @NonNull float getH() {
        return (float) (dH + (buttons.size()*(h-12)+3)*expanded.getAnimationFactor());
    }

    class Button extends Comp {
        Animation selected = new Animation(() -> 200F, false, () -> Easing.CIRC_IN_OUT);
        int ordinal;

        public Button(int ordinal, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
            super(x, y, w, h);
            this.ordinal = ordinal;
        }

        @Override
        public void render(float mx, float my) {
            selected.setState(ordinal == getSetting().getValue().ordinal());
            float anim = (float) selected.getAnimationFactor();
            Color bg1 = ColorUtil.interpolate(new Color(30, 30, 30),new Color(38, 38, 38),selected.getAnimationFactor());
            Color text = ColorUtil.interpolate(new Color(164, 164, 164),Color.WHITE,selected.getAnimationFactor());

            bg1 = ColorUtil.interpolate(new Color(26,26,26),bg1,expanded.getAnimationFactor());
            text = ColorUtil.interpolate(new Color(26,26,26),text,expanded.getAnimationFactor());
            ui.round(x+w/2f-((w-6)/2f-2)*anim,y+h/2f-((h-4)/2f)*anim,(w-4-4)*anim, (h-4)*anim, 4, bg1);
            ui.text(values[ordinal],"regular",14,x+w/2f,y+h/2f,text, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE);
        }

        @Override
        public void click(float mx, float my, int button) {
            if (hovered()) {
                Enum<?> enumeration = getSetting().getValue();
                getSetting().setValue(Enum.valueOf(enumeration.getClass(), values[ordinal]));
            }
        }
    }
}
