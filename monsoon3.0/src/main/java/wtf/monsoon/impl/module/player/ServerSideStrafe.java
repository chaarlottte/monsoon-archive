package wtf.monsoon.impl.module.player;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.combat.TargetStrafe;

public class ServerSideStrafe extends Module {

    private float targetYaw, lastYaw;

    private final Setting<Boolean> strafeMotion = new Setting<>("Strafe Motion", false)
            .describedBy("Whether to set your motion to strafing.");

    private final Setting<Boolean> whileAura = new Setting<>("While Aura Enabled", false)
            .describedBy("Whether to set your rotation while Aura is attacking.");

    private final Setting<Boolean> whileScaffold = new Setting<>("While Scaffold Enabled", false)
            .describedBy("Whether to set your rotation while Scaffold is enabled.");

    public ServerSideStrafe() {
        super("Server Side Strafe", "Sets your rotation so you strafe server side.", Category.PLAYER);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (player.isMoving()) {
            if ((!Wrapper.getModule(Scaffold.class).isEnabled() || !whileScaffold.getValue()) && (!(Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null) || !whileAura.getValue())) {
                targetYaw = e.getYaw();
                if (mc.gameSettings.keyBindBack.pressed) {
                    targetYaw += 180;
                    if (mc.gameSettings.keyBindLeft.pressed) {
                        targetYaw += 45;
                    }
                    if (mc.gameSettings.keyBindRight.pressed) {
                        targetYaw -= 45;
                    }
                } else if (mc.gameSettings.keyBindForward.pressed) {
                    if (mc.gameSettings.keyBindLeft.pressed) {
                        targetYaw -= 45;
                    }
                    if (mc.gameSettings.keyBindRight.pressed) {
                        targetYaw += 45;
                    }
                } else {
                    if (mc.gameSettings.keyBindLeft.pressed) {
                        targetYaw -= 90;
                    }
                    if (mc.gameSettings.keyBindRight.pressed) {
                        targetYaw += 90;
                    }
                }
                if (Wrapper.getModule(TargetStrafe.class).isEnabled() && Wrapper.getModule(TargetStrafe.class).isStrafing()) {
                    targetYaw = MathHelper.wrapAngleTo180_float(Wrapper.getModule(TargetStrafe.class).currentYaw);
                }
                targetYaw = interpolateRotation(lastYaw, targetYaw, 45f);
                e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = targetYaw);
                lastYaw = targetYaw;

                if (strafeMotion.getValue()) {
                    double baseSpeed = MovementUtil.WALK_SPEED;
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
                        baseSpeed *= 1.0D + 0.2D * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
                    player.setSpeed(baseSpeed);
                }
            }
        }
    };

    private float interpolateRotation(final float prev, final float now, final float maxTurn) {
        float var4 = MathHelper.wrapAngleTo180_float(now - prev);
        if (var4 > maxTurn) {
            var4 = maxTurn;
        }
        if (var4 < -maxTurn) {
            var4 = -maxTurn;
        }
        return prev + var4;
    }

}
