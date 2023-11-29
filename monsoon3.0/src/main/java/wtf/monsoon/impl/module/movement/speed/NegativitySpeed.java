package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;

import java.util.Set;

public class NegativitySpeed extends ModeProcessor {

    private final Setting<Double> originalSpeed = new Setting<>("Starting speed", 2.5)
            .minimum(1.0)
            .maximum(3.5)
            .incrementation(0.1);

    private final Setting<Double> modifier = new Setting<>("Speed Modifier", 1.01)
            .minimum(1.0)
            .maximum(1.1)
            .incrementation(0.01);

    private final Setting<Double> strafeModifier = new Setting<>("Strafe Modifier", 0.7)
            .minimum(0.1)
            .maximum(1.0)
            .incrementation(0.1);

    private final Setting<Double> groundModifier = new Setting<>("Ground Modifier", 1.7)
            .minimum(1.0)
            .maximum(2.0)
            .incrementation(0.1);

    private final Setting<Boolean> fastFall = new Setting<>("Fast Fall", true);

    private double speed, lastMotionX, lastMotionZ;
    private int ticks;

    public NegativitySpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        speed = player.getBaseMoveSpeed() * originalSpeed.getValue();
        ticks = 0;
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if(player.isMoving()) {
            if(player.isOnGround()) {
                speed *= modifier.getValue();
                e.setY(mc.thePlayer.motionY = 0.41999998688698f);
                ticks++;
            } else {
                if(mc.thePlayer.motionY < 0) {
                    if(ticks >= 2) {
                        if(fastFall.getValue() && mc.thePlayer.fallDistance <= 1.5) mc.thePlayer.motionY = -0.3f;
                    }
                }
                ticks++;
            }

            player.setSpeedWithCorrection(e, speed * (mc.thePlayer.onGround ? groundModifier.getValue() : 1.0), lastMotionX, lastMotionZ, strafeModifier.getValue());

            lastMotionX = e.getX();
            lastMotionZ = e.getZ();
        } else {
            speed = player.getBaseMoveSpeed() * 2f;
        }
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { originalSpeed, modifier, strafeModifier, groundModifier, fastFall };
    }
}
