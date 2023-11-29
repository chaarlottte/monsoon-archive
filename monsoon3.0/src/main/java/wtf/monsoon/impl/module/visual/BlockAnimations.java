package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventUpdate;

public class BlockAnimations extends Module {

    public final Setting<Animation> animation = new Setting<>("Animation", Animation.OLD)
            .describedBy("The block animation");

    public final Setting<SwingAnimation> swing = new Setting<>("Swing", SwingAnimation.NORMAL)
            .describedBy("The swing animation");

    public final Setting<Double> speed = new Setting<>("Slowdown", 1.2)
            .minimum(0.1)
            .maximum(3.5)
            .incrementation(0.01)
            .describedBy("Change the speed of the animation.");

    public final Setting<Boolean> hideModule = new Setting<>("HideModule", false)
            .describedBy("Whether to hide the module in the Array List.");

    public BlockAnimations() {
        super("Block Animations", "Change the block animations", Category.VISUAL);
        this.setMetadata(() -> StringUtil.formatEnum(animation.getValue()));
    }

    @EventLink
    private final Listener<EventUpdate> er2d = e -> {
        this.setVisible(hideModule.getValue());
    };

    public enum Animation {
        OLD,
        ASTOLFO,
        CHILL,
        EXHI,
        EXHI_TAP,
        EXHIBOBO,
        SLIDE,
        SWING,
        OH_THE_MISERY
    }

    public enum SwingAnimation {
        SMOOTH,
        NORMAL
    }
}
