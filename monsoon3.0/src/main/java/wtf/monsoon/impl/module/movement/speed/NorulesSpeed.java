package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;

public class NorulesSpeed extends ModeProcessor {

    private final Setting<Float> timerSpeed = new Setting<>("Timer Speed", 1.0f)
            .minimum(1f)
            .maximum(2f)
            .incrementation(0.05f)
            .describedBy("The timer speed while using the module Speed.");

    public NorulesSpeed(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (player.isMoving()) {
            if (player.isOnGround()) {
                mc.getTimer().timerSpeed = 0.85f;
                player.setSpeed(e, player.getSpeed());
                e.setY(mc.thePlayer.motionY = 0.41999998688698f);
            } else {
                mc.getTimer().timerSpeed = timerSpeed.getValue();

                if (mc.thePlayer.motionY > 0) {
                    mc.getTimer().timerSpeed += 0.2f;
                } else {
                    mc.getTimer().timerSpeed -= 0.1f;
                }

                player.setSpeed(e, player.getBaseMoveSpeed() * 1.4f);
            }
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { timerSpeed };
    }

}
