package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPreMotion;

public class FuncraftFlight extends ModeProcessor {


    private double funcraftSpeed;
    private float timerF;

    private Timer timer = new Timer();

    public FuncraftFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.thePlayer.fallDistance = 4f;
        PlayerUtil.fakeDamage();
        if (mc.thePlayer.isCollidedVertically) {
            //funcraftSpeed = 1.79f;
            funcraftSpeed = 1.5f;
        } else {
            funcraftSpeed = 0.3f;
        }
        if (player.isOnGround()) player.jump();
        mc.thePlayer.isCollidedVertically = false;
        //timerF = (float) funcraftTimer.getValue();
        timerF = 1.5f;
        timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.setVelocity(0, 0, 0);
        player.setSpeed(0);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.thePlayer.motionY = -0.2f;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.motionY = 0.2f;
        }

        if (timer.hasTimeElapsed(350, true)) {
            funcraftSpeed = +funcraftSpeed - 0.0046f;
            if (funcraftSpeed <= 0.15) {
                funcraftSpeed = 0.15f;
            }
        }

        //mc.getTimer().timerSpeed = timerF;
        mc.getTimer().timerSpeed = 1.0f;

        if (mc.thePlayer.isCollidedVertically) {
            player.jump();
        } else {

            if (!mc.gameSettings.keyBindJump.pressed || !mc.gameSettings.keyBindSneak.pressed) {
                //mc.thePlayer.motionY = 0;
                mc.thePlayer.motionY = 1E-4;
            }

            mc.thePlayer.jumpMovementFactor = 0;
            player.setSpeed(Math.max(player.getBaseMoveSpeed(), (funcraftSpeed -= funcraftSpeed / 159) + (1 / 2)));

        }

        double y1 = mc.thePlayer.posY - (player.isOnGround() ? 0 : 1E-10D);
        mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ);
    };

}
