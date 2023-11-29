package wtf.monsoon.impl.module.visual;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

/**
 * @author Surge
 * @since 22/08/2022
 */
public class Blur extends Module {

    public final Setting<Boolean> chat = new Setting<>("Chat", false)
            .describedBy("Blurs the chat GUI screen");

    public final Setting<Float> guiBlurStrength = new Setting<>("Intensity", 6f)
            .minimum(2f)
            .maximum(10f)
            .incrementation(0.5f)
            .describedBy("Strength of  the blur");

    public Blur() {
        super("Blur", "Blurs GUIs", Category.VISUAL);
    }

}
