package wtf.monsoon.impl.module.visual;

import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Bind;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.module.annotation.DefaultBind;
import wtf.monsoon.impl.ui.panel.PanelGUI;
import wtf.monsoon.impl.ui.recode.panel.PanelScreen;

import java.awt.*;

@DefaultBind(code = Keyboard.KEY_RSHIFT, device = Bind.Device.KEYBOARD)
public class ClickGUI extends Module {

    private final Setting<Style> style = new Setting<>("Style", Style.PANEL)
            .describedBy("The style of the GUI.");

    public final Setting<Double> scrollSpeed = new Setting<>("Scroll Speed", 0.2)
            .minimum(0.1)
            .maximum(1.0)
            .incrementation(0.05)
            .describedBy("How fast scrolling is")
            .visibleWhen(() -> style.getValue().equals(Style.PANEL));

    public final Setting<Double> scrollDivider = new Setting<>("Scroll Divider", 5.0)
            .minimum(1.0)
            .maximum(10.0)
            .incrementation(0.1)
            .describedBy("How much to divide the scroll difference by")
            .visibleWhen(() -> style.getValue().equals(Style.PANEL));

    public final Setting<Boolean> particles = new Setting<>("Particles", false)
            .visibleWhen(() -> style.getValue().equals(Style.RECODE));

    public final Setting<Boolean> british = new Setting<>("British", false)
            .describedBy("innit bruv");

    public ClickGUI() {
        super("Click GUI", "Configure the client.", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        switch (style.getValue()) {
            case PANEL:
                mc.displayGuiScreen(Wrapper.getMonsoon().getPanelGUI());
                break;

            case WINDOW:
                mc.displayGuiScreen(Wrapper.getMonsoon().getWindowGUI());
                break;

            case RECODE:
                mc.displayGuiScreen(Wrapper.getMonsoon().getRecodePanelGUI());
                break;
        }

        this.toggle();
    }

    public enum Style {
        PANEL,
        WINDOW,
        RECODE
    }
}
