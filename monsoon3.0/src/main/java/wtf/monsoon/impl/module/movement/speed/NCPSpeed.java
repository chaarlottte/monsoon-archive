package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.potion.Potion;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPreMotion;

public class NCPSpeed extends ModeProcessor {

    private boolean reset;

    public NCPSpeed(Module parentModule) {
        super(parentModule);
    }


    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (mc.thePlayer.ticksExisted % 20 <= 9) {
            mc.getTimer().timerSpeed = 1.05f;
        } else {
            mc.getTimer().timerSpeed = 0.98f;
        }

        if (player.isMoving()) {
            if (player.isOnGround()) {
                reset = false;
                player.jump(e);
                player.setSpeed(e, player.getSpeed() * 1.01f);
                e.setY(0.41f);
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    player.setSpeed(e, player.getSpeed() * (1.0f + 0.1f * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1)));
                }
            }
            player.setSpeed(e, player.getSpeed() * 1.0035f);
            if (player.getSpeed() < 0.277)
                reset = true;
            if (reset)
                player.setSpeed(e, 0.277f);


        } else {
            e.setX(mc.thePlayer.motionX = 0.0);
            e.setZ(mc.thePlayer.motionZ = 0.0);
            reset = true;
        }
    };


}
