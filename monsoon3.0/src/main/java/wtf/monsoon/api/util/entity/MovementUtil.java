package wtf.monsoon.api.util.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.module.combat.TargetStrafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MovementUtil extends Util {

    public static final double WALK_SPEED = 0.221;
    private static final List<Double> frictionValues = new ArrayList<>();
    private static final double MIN_DIF = 1.0E-2;
    public static final double BUNNY_DIV_FRICTION = 160.0D - MIN_DIF;
    private static final double AIR_FRICTION = 0.98;
    private static final double WATER_FRICTION = 0.89;
    private static final double LAVA_FRICTION = 0.535;


    public static float getDirection() {
        float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

        double moveForward = mc.thePlayer.moveForward;
        double moveStrafing = mc.thePlayer.moveStrafing;

        if(moveForward < 0) {
            yaw += 180;
        }

        if(moveStrafing > 0) {
            yaw += (moveForward == 0 ? -90 : moveForward > 0 ? -45 : 45);
        }
        if(moveStrafing < 0) {
            yaw += (moveForward == 0 ? 90 : moveForward > 0 ? 45 : -45);
        }

        return yaw;
    }

    public static boolean isGoingDiagonally() {
        return Math.abs(mc.thePlayer.motionX) > 0.08 && Math.abs(mc.thePlayer.motionZ) > 0.08;
    }

    public static double calculateFriction(double moveSpeed, double lastDist, double baseMoveSpeedRef) {
        frictionValues.clear();
        frictionValues.add(lastDist - (lastDist / BUNNY_DIV_FRICTION));
        frictionValues.add(lastDist - ((moveSpeed - lastDist) / 33.3));
        double materialFriction =
                mc.thePlayer.isInWater() ?
                        WATER_FRICTION :
                        mc.thePlayer.isInLava() ?
                                LAVA_FRICTION :
                                AIR_FRICTION;
        frictionValues.add(lastDist - (baseMoveSpeedRef * (1.0 - materialFriction)));
        return Collections.min(frictionValues);
    }

}