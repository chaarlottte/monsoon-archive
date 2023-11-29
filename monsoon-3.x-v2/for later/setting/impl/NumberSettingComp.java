package wtf.monsoon.client.ui.panel.elements.setting.impl;

import lombok.NonNull;
import org.lwjgl.input.Mouse;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.ui.recode.panel.elements.setting.SettingComp;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;

public class NumberSettingComp extends SettingComp<Number> {
    boolean sliding;
    float renderValue, dH;

    public NumberSettingComp(Setting<Number> setting, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(setting, x, y, w, h);
        renderValue = 0;
        dH = h;
    }

    @Override
    public void render(float mx, float my) {
        if(!Mouse.isButtonDown(0)) sliding = false;

        ui.rect(x,y,w,getH(), new Color(26,26,26));
        ui.round(x+8-1,y+8+16+4-1,w-16+2,4+2,3,new Color(38, 38, 38));
        ui.round(x+8,y+8+16+4,w-16,4,3,new Color(30, 30, 30));
        ui.circle(x+8+2+renderValue, y+8+16+4+2.5f, 5, ColorUtil.getAccent()[0]);
        ui.circle(x+8+2+renderValue, y+8+16+4+2.5f, 3, new Color(26,26,26));


        float diff = Math.max(0,Math.min(w-16-4,(mx-(x+8+2))));
        float min = getSetting().getMinimum().floatValue();
        float max = getSetting().getMaximum().floatValue();
        float step = getSetting().getIncrementation().floatValue();
        float current = getSetting().getValue().floatValue();

        renderValue = (w-16-4) * (current - min) / (max - min);

        ui.text(getSetting().getName(), "regular", 14, x+8, y+4, Color.WHITE);
        ui.text(current+"", "regular", 14, x+w-8, y+4, Color.WHITE, NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_TOP);


        if(sliding) {
            Float value = (float) MathUtils.round(((diff/(w-16-4)) * (max - min) + min), 2);
            value = Math.round(Math.max(min, Math.min(max, value)) * (1 / step)) / (1 / step);

            Float fin = (float) MathUtils.round(value, 2);

            if (getSetting().getValue() instanceof Double) {
                getSetting().setValue(fin.doubleValue());
            } else if (getSetting().getValue() instanceof Float) {
                getSetting().setValue(fin);
            } else if (getSetting().getValue() instanceof Integer) {
                getSetting().setValue(fin.intValue());
            } else if (getSetting().getValue() instanceof Long) {
                getSetting().setValue(fin.longValue());
            } else if (getSetting().getValue() instanceof Short) {
                getSetting().setValue(fin.shortValue());
            } else if (getSetting().getValue() instanceof Byte) {
                getSetting().setValue(fin.byteValue());
            }
        }
    }

    @Override
    public void click(float mx, float my, int button) {
        if(hovered(x+8-1,y+8+16+4-1,w-16+2,4+2) && button == 0) {
            sliding = true;
        }
        super.click(mx, my, button);
    }

    @Override
    public @NonNull float getH() {
        return dH + 10;
    }
}
