package wtf.monsoon.impl.ui.recode.panel.elements;

import lombok.NonNull;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.util.render.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Panel extends Comp {
    @Setter
    float veloX = 0;

    Category category;
    public Animation expanded = new Animation(() -> 300F, false, () -> Easing.CIRC_IN_OUT);
    ArrayList<ModuleComp> moduleComps = new ArrayList<>();
    public Panel(Category category, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(x, y, w, h);
        this.category = category;
        AtomicInteger o = new AtomicInteger();
        Wrapper.getMonsoon().getModuleManager().getModulesByCategory(category).forEach(m -> {
            moduleComps.add(new ModuleComp(m,x,y+h+h* o.get(),w,h));
            o.getAndIncrement();
        });
    }

    @Override
    public void render(float mx, float my) {
//        ui.dropShadow(x,y,w,2,1,5,ColorUtil.interpolate(ColorUtil.getAccent()[0], ColorUtil.TRANSPARENT,0.5), ColorUtil.TRANSPARENT,true);
//        ui.dropShadow(x,y,w,2,1,10,ColorUtil.interpolate(ColorUtil.getAccent()[0], ColorUtil.TRANSPARENT,0.7), ColorUtil.TRANSPARENT,true);
//
//        ui.dropShadow(x,y+h+h*moduleComps.size()*(float)expanded.getAnimationFactor(),w,2,1,5,ColorUtil.interpolate(ColorUtil.getAccent()[0], ColorUtil.TRANSPARENT,0.5), ColorUtil.TRANSPARENT,true);
//        ui.dropShadow(x,y+h+h*moduleComps.size()*(float)expanded.getAnimationFactor(),w,2,1,10,ColorUtil.interpolate(ColorUtil.getAccent()[0], ColorUtil.TRANSPARENT,0.7), ColorUtil.TRANSPARENT,true);

        float maxHeight = 0;
        for (ModuleComp moduleComp : moduleComps) {
            maxHeight += moduleComp.getAbsoluteHeight();
        }

        ui.finishFrame();
        ui.initFrame();
        NanoVG.nvgTranslate(ui.vg,mx,my);
        NanoVG.nvgRotate(ui.vg,veloX);
        NanoVG.nvgTranslate(ui.vg,-(mx),-(my));

//        ui.dropShadow(x+8,y+8,w-16,h-16+maxHeight*(float) expanded.getAnimationFactor(),0,10,ColorUtil.getAccent()[0], ColorUtil.TRANSPARENT,false);

        ui.rect(x,y,w,h+h*moduleComps.size()*(float) expanded.getAnimationFactor(), new Color(0x161616));
        ui.rect(x,y,w,h, new Color(0x101010));
        ui.text(category.getIcon(), "category2", 20, x+8, y+h/2f,Color.WHITE, 16);
        ui.roundedLinearGradient(x,y,w,2, 0,ColorUtil.getAccent()[0],ColorUtil.getAccent()[1]);
        ui.text(category.name(), "sbold", 16, x+w/2f, y+h/2f+1, Color.WHITE, 2 | 16);


        ui.rect(x, y + h, w, maxHeight * (float) expanded.getAnimationFactor(), new Color(0x161616));

        if(expanded.getAnimationFactor() > 0.001) {
            ui.scissor(x, y + h, w, maxHeight * (float) expanded.getAnimationFactor(), () -> {
                float[] i = {0};
                moduleComps.forEach(m -> {
                    m.setX(x);
                    m.setY(y + h + i[0]);
                    m.render(mx, my);
                    i[0]+=m.getAbsoluteHeight();
                });
            });
        }
        ui.roundedLinearGradient(x,y+h+maxHeight*(float)expanded.getAnimationFactor(),w,2, 0,ColorUtil.getAccent()[0],ColorUtil.getAccent()[1]);
    }

    @Override
    public void click(float mx, float my, int button) {
        if(expanded.getState())
            moduleComps.forEach(m -> m.click(mx, my, button));
    }

    @Override
    public void key(int code, char c) {
        if(expanded.getState())
            moduleComps.forEach(m -> m.key(code,c));
    }
}
