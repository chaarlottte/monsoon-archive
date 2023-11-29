package wtf.monsoon.impl.ui.recode.panel;

import org.lwjgl.input.Mouse;
import org.lwjgl.nanovg.NanoVG;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.ui.Screen;
import wtf.monsoon.impl.module.visual.ClickGUI;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.recode.ParticleEngine;
import wtf.monsoon.impl.ui.recode.panel.elements.Panel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class PanelScreen extends Screen {

    private final ArrayList<Panel> panels = new ArrayList<>();
    ParticleEngine pe;

    private Panel dragging;
    private float dragX, dragY;
    private boolean drag;

    @Override
    public void init() {
        pe = new ParticleEngine(5);
        super.init();
        panels.clear();
        int o = 0;
        for (Category c : Category.values()) {
            panels.add(new Panel(c,10 + o * 210, 10, 200, 34));
            o ++;
        }
    }

    @Override
    public void render(float mx, float my) {
        ui.rect(0,0,dw,dh,new Color(0x93000000, true));

        if(Wrapper.getModule(ClickGUI.class).particles.getValue())
            pe.render(dw,dh,(int)mx,(int)my);

        if(!Mouse.isButtonDown(0)) {
            drag = false;
            if(dragging != null)
                dragging.setVeloX(0);
            dragging = null;
        }


        if(dragging != null && drag) {
            float maxVel = 100;

            float vmx = mx - dragging.getX() - dragX;
            if(vmx > 0) vmx = Math.min(mx - dragging.getX() - dragX, maxVel);
            if(vmx < 0) vmx = Math.max(mx - dragging.getX() - dragX, -maxVel);

            dragging.setVeloX(vmx/360f);

            dragging.setX(mx - dragX);
            dragging.setY(my - dragY);
        }

        panels.forEach(p -> p.render(mx, my));
    }

    @Override
    public void click(float mx, float my, int button) {
        panels.forEach(p -> {
            if(p.hovered() && button == 0) {
                drag = true;
                dragX = mx-p.getX();
                dragY = my-p.getY();
                dragging = p;
            }

            if(p.hovered() && button == 1) {
                p.expanded.setState(!p.expanded.getState());
            }
            p.click(mx, my, button);
        });
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        panels.forEach(p -> p.key(keyCode,typedChar));
        super.keyTyped(typedChar, keyCode);
    }
}
