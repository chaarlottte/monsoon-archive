package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPreMotion;

public class VanillaFlight extends ModeProcessor {

    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5)
            .minimum(0.0D)
            .maximum(2.0D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.");

    public VanillaFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.capabilities.isFlying = false;
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        player.setSpeed(speedValue.getValue());
        mc.thePlayer.capabilities.isFlying = true;
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { speedValue };
    }

}
