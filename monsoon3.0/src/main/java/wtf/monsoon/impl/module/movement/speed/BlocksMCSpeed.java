package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventMove;

public class BlocksMCSpeed extends ModeProcessor {
    public BlocksMCSpeed(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (player.isMoving()) {
            if (player.isOnGround()) {
                e.setY(0.41999998688698);
                player.jump(0.42f);
            }

            player.setSpeed(e, player.getSpeed());
        }
    };
}
