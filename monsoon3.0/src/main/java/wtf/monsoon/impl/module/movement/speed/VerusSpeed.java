package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventPreMotion;

public class VerusSpeed extends ModeProcessor {
    public VerusSpeed(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if(player.isMoving()) {
            if(player.isOnGround()) {
                if(!mc.gameSettings.keyBindJump.isKeyDown()) {
                    player.setSpeed(player.getBaseMoveSpeed());
                    player.jump();
                }
            } else {
                player.setSpeed(player.getSpeed() * 1.03);
            }
        }
    };


}
