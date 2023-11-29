package wtf.monsoon.impl.module.movement.flight;

import com.github.javafaker.Bool;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovementInput;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPreMotion;

public class NegativityFlight extends ModeProcessor {

    private final Setting<Mode> mode = new Setting<>("Negativity Mode", Mode.NORMAL);
    private final Setting<Boolean> tickExploit = new Setting<>("Tick Exploit", true);
    private final Setting<Boolean> spoofGround = new Setting<>("Spoof Ground", true);

    private final Setting<Float> timerSpeed = new Setting<>("Timer Speed", 1.0f)
            .minimum(1.0f)
            .maximum(2.5f)
            .incrementation(0.05f);

    private final Setting<Boolean> randModifier = new Setting<>("Random Speed Modifier", true)
            .visibleWhen(() -> mode.getValue().equals(Mode.FAST));

    private final Setting<Double> speedModifier = new Setting<>("Speed Modifier", 1.01)
            .minimum(1.0)
            .maximum(1.1)
            .incrementation(0.01)
            .visibleWhen(() -> mode.getValue().equals(Mode.FAST) && !randModifier.getValue());

    private double speed, ogPosY;
    private boolean jumped;
    private int jumpTicks;

    private final Timer timer = new Timer();

    public NegativityFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        jumped = false;
        jumpTicks = 0;
        mc.thePlayer.isCollidedVertically = false;
        timer.reset();
        speed = player.getBaseMoveSpeed() * 2.5f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        /*if(jumped) {
            mc.thePlayer.setVelocity(0, 0, 0);
            player.setSpeed(0);
            for(int i = 0; i < 3; i++) {
                for(double d = 0; d < 1.25; d += 0.25) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY + d,
                            mc.thePlayer.posZ,
                            false
                    ));
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        true
                ));
            }
        }*/
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if(jumped && mode.getValue().equals(Mode.FAST)) mc.thePlayer.posY = this.ogPosY;
    };

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if(!mc.thePlayer.onGround) jumpTicks++;

        mc.getTimer().timerSpeed = timerSpeed.getValue();

        if (jumpTicks <= 0) {
            PlayerUtil.sendClientMessage("jump");
            player.setSpeed(e, 0);
            player.jump(e);
            jumpTicks++;
        } else if(!jumped) {
            if(mc.thePlayer.motionY < 0 && jumpTicks > 7) {
                mc.thePlayer.onGround = true;
                PlayerUtil.fakeDamage();
                ogPosY = mc.thePlayer.posY;
                jumped = true;
            }
        } else {
            switch (mode.getValue()) {
                case NORMAL:
                    speed = player.getBaseMoveSpeed() * 2.9f;
                    if(spoofGround.getValue()) mc.thePlayer.onGround = true;
                    e.setY(mc.thePlayer.motionY = 0);
                    player.setSpeed(e, speed);
                    break;
                case FAST:
                    float jumpHeight = 0.2f;
                    double maxSpeed = 10;
                    if (mc.thePlayer.ticksExisted % 10 == 0 && tickExploit.getValue())
                        player.setSpeed(e, 0);


                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        e.setY(mc.thePlayer.motionY = jumpHeight);
                        if (speed < maxSpeed) speed *= this.getSpeedModifier();
                    } else e.setY(mc.thePlayer.motionY = -jumpHeight);

                    if(spoofGround.getValue()) mc.thePlayer.onGround = true;
                    player.setSpeed(e, speed);
                    break;
            }

            if(tickExploit.getValue())
                if (mc.thePlayer.ticksExisted % 10 == 0)
                    for (int i = 0; i < 3; i++) PacketUtil.sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));


        }
    };

    private double getSpeedModifier() {
        if(randModifier.getValue()) return (1 + MathUtils.randomNumber(0.05, 0.01));
        return speedModifier.getValue();
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { mode, tickExploit, spoofGround, timerSpeed, randModifier, speedModifier };
    }

    enum Mode {
        NORMAL, FAST
    }

}
