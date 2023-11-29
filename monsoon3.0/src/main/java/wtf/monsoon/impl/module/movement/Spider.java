package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPreMotion;

public class Spider extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.MOTION)
            .describedBy("Da mode");

    public final Setting<Double> motion = new Setting<>("Motion", 0.25)
            .minimum(0.0)
            .maximum(2.0)
            .incrementation(0.05)
            .describedBy("The speed at which you ascend")
            .visibleWhen(() -> mode.getValue() == Mode.MOTION);

    public Spider() {
        super("Spider", "Climb up walls in style", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    @EventLink
    public final Listener<EventPreMotion> onPreMotion = e -> {
        if(mc.thePlayer.isCollidedHorizontally) {
            switch (mode.getValue()) {
                case MOTION:
                    mc.thePlayer.motionY = motion.getValue();
                    break;

                case TELEPORT:
                    mc.thePlayer.motionY = -0.0784f;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1f, mc.thePlayer.posZ);
                    break;
                case VULCAN:
                    if(mc.thePlayer.ticksExisted % 2 == 0) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1f, mc.thePlayer.posZ);
                    } else {
                        mc.thePlayer.motionY = -0.0784f;
                    }
                    break;
            }
        }
    };

    public enum Mode {
        MOTION,
        TELEPORT,
        VULCAN
    }
}
