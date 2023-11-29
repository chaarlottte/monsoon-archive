package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPreMotion;

public class MotionFlight extends ModeProcessor {

    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5)
            .minimum(0.0D)
            .maximum(2.0D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.");

    public MotionFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setVelocity(0, 0, 0);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        player.setSpeed(speedValue.getValue());
        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? speedValue.getValue() : mc.gameSettings.keyBindSneak.isKeyDown() ? -speedValue.getValue() : 0;
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { speedValue };
    }

}
