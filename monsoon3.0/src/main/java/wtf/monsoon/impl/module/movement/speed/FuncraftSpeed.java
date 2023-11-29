package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Speed;

import java.util.List;

public class FuncraftSpeed extends ModeProcessor {

    private double speed, lastDist;
    private boolean prevOnGround;

    private final Setting<Boolean> funcraftTimerOption = new Setting<>("Funcraft Timer", true)
            .describedBy("Whether to use the Funcraft timer");

    public FuncraftSpeed(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (player.isMoving()) {
            speed = player.getBaseMoveSpeed();

            if (mc.thePlayer.onGround && !prevOnGround) {
                mc.getTimer().timerSpeed = 1.0f;
                prevOnGround = true;
                e.setY(0.41999998688698);
                mc.thePlayer.motionY = 0.42;
                speed *= 2.1;
            } else if (prevOnGround) {
                double difference = (0.66D * (lastDist - player.getBaseMoveSpeed()));
                speed = lastDist - difference;
                prevOnGround = false;
            } else {
                speed = lastDist - lastDist / 159D;
            }

            if (funcraftTimerOption.getValue()) {
                mc.getTimer().timerSpeed = 1.15f;
            }

            player.setSpeed(e, Math.max(player.getBaseMoveSpeed(), speed));
        }
    };

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { funcraftTimerOption };
    }
}
