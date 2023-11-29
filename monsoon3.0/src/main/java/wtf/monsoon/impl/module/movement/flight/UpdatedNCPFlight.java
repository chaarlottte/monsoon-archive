package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Flight;

public class UpdatedNCPFlight extends ModeProcessor {

    private final Setting<String> ncpLatestSettings = new Setting<>("NCP Latest Settings", "NCP Latest Settings")
            .describedBy("Settings for mode NCP latest.");

    private final Setting<Float> motionYSetting = new Setting<>("Motion Y", 0.275f)
            .minimum(-0.0525f)
            .maximum(0.3f)
            .incrementation(0.0005f)
            .describedBy("The Motion Y during flight.")
            .childOf(ncpLatestSettings);

    private final Setting<Double> ncpLatestSpeed = new Setting<>("Speed", 9.75D)
            .minimum(1.0D)
            .maximum(10.0D)
            .incrementation(0.05D)
            .describedBy("The speed of the flight.")
            .childOf(ncpLatestSettings);

    private final Setting<Float> ncpLatestTimerSpeed = new Setting<>("Timer Speed", 0.8f)
            .minimum(0.1f)
            .maximum(1.5f)
            .incrementation(0.1f)
            .describedBy("The timer speed during flight.")
            .childOf(ncpLatestSettings);

    public UpdatedNCPFlight(Module parentModule) {
        super(parentModule);
    }

    private int ticks;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ticks = 0;
        mc.getTimer().timerSpeed = 1f;
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (!player.isOnGround()) {
            mc.getTimer().timerSpeed = ncpLatestTimerSpeed.getValue();
            if (ticks == 0) {
                mc.thePlayer.motionY = motionYSetting.getValue();
                player.setSpeed(ncpLatestSpeed.getValue());
            }
            ticks++;
        } else {
            player.jump();
            mc.getTimer().timerSpeed = 0.2f;
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { ncpLatestSettings };
    }

}
