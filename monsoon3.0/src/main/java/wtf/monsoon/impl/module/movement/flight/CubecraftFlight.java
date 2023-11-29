package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class CubecraftFlight extends ModeProcessor {

    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5)
            .minimum(0.0D)
            .maximum(2.0D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.");

    public CubecraftFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setVelocity(0, 0, 0);
        player.setSpeed(0);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        e.setOnGround(true);
        mc.thePlayer.cameraYaw = 0.071f;
    };

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        player.setSpeed(e, speedValue.getValue());
        e.setY(mc.thePlayer.motionY = (Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null ? 0 : (mc.gameSettings.keyBindJump.isKeyDown() ? speedValue.getValue() : mc.gameSettings.keyBindSneak.isKeyDown() ? -speedValue.getValue() : 0)));
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { speedValue };
    }

}
