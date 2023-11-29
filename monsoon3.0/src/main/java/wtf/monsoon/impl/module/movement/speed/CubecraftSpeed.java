package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventMove;

public class CubecraftSpeed extends ModeProcessor {
    public CubecraftSpeed(Module parentModule) {
        super(parentModule);
    }


    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if(player.isMoving()) {
            if(player.isOnGround()) {
                player.setSpeed(e, player.getBaseMoveSpeed() * 5f);
                e.setY(mc.thePlayer.motionY = 0.41999998688698f);
            } else {
                player.setSpeed(e, player.getBaseMoveSpeed() * 2.2f);

            }
        }
        if (mc.thePlayer.motionY == 0.08307781780646721) mc.thePlayer.motionY = -0.25;
    };
}
