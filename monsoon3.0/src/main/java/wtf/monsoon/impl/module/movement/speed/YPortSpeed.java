package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPreMotion;

public class YPortSpeed extends ModeProcessor {

    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5)
            .minimum(0.0D)
            .maximum(2.0D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.");

    public YPortSpeed(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (player.isMoving()) {
            mc.getTimer().timerSpeed = 1.0f;
            if (player.isOnGround()) {
                player.jump();
                mc.thePlayer.motionY = 0.42f;
                player.setSpeed(player.getSpeed());
            } else {
                //player.setSpeed(player.getSpeed());
                player.setSpeed(speedValue.getValue());
                if(!mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY = -0.42f;
            }
        } else {
            player.setSpeed(0.0);
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { speedValue };
    }

}
