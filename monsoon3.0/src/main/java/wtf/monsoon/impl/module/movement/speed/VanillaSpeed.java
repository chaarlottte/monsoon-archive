package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;

public class VanillaSpeed extends ModeProcessor {

    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5)
            .minimum(0.0D)
            .maximum(2.0D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.");

    private final Setting<Float> timerSpeed = new Setting<>("Timer Speed", 1.0f)
            .minimum(1f)
            .maximum(2f)
            .incrementation(0.05f)
            .describedBy("The timer speed while using the module Speed.");

    public VanillaSpeed(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (player.isMoving()) {
            if (player.isOnGround()) {
                player.setSpeed(e, speedValue.getValue() * 1.5d);
                e.setY(0.41999998688698);
                mc.thePlayer.motionY = 0.42;
            } else {
                player.setSpeed(e, speedValue.getValue());
                mc.getTimer().timerSpeed = timerSpeed.getValue();
            }
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { speedValue, timerSpeed };
    }

}
