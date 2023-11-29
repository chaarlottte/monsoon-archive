package wtf.monsoon.misc.script.wrapper;

import me.surge.api.Coercer;
import me.surge.api.result.Result;
import me.surge.api.result.Success;
import me.surge.lexer.value.ListValue;
import me.surge.lexer.value.Value;
import me.surge.lexer.value.link.JvmClassInstanceValue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;

import java.util.ArrayList;
import java.util.List;

public class ScriptPlayer {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendMessage(String message) {
        PlayerUtil.sendClientMessage(message);
    }

    public static void setSpeed(double speed) {
        Wrapper.getMonsoon().getPlayer().setSpeed(speed);
    }

    public static void jump() {
        mc.thePlayer.jump();
    }

    public static boolean isOnGround() {
        return mc.thePlayer.onGround;
    }

    public static boolean isCollidedHorizontally() {
        return mc.thePlayer.isCollidedHorizontally;
    }

    public static boolean isCollidedVertically() {
        return mc.thePlayer.isCollidedVertically;
    }

    public static float getYaw() {
        return mc.thePlayer.rotationYaw;
    }

    public static float getPitch() {
        return mc.thePlayer.rotationPitch;
    }

    public static double getMotionX() {
        return mc.thePlayer.motionX;
    }

    public static double getMotionY() {
        return mc.thePlayer.motionY;
    }

    public static double getMotionZ() {
        return mc.thePlayer.motionZ;
    }

    public static void setMotionX(double motion) {
        mc.thePlayer.motionX = motion;
    }

    public static void setMotionY(double motion) {
        mc.thePlayer.motionY = motion;
    }

    public static void setMotionZ(double motion) {
        mc.thePlayer.motionZ = motion;
    }

    public static double getPosX() {
        return mc.thePlayer.posX;
    }

    public static double getPosY() {
        return mc.thePlayer.posY;
    }

    public static double getPosZ() {
        return mc.thePlayer.posZ;
    }

    public static void setPosition(double x, double y, double z) {
        mc.thePlayer.setPosition(x, y, z);
    }

    public static boolean isFlying() {
        return mc.thePlayer.capabilities.isFlying;
    }

    public static void setFlying(boolean flying) {
        mc.thePlayer.capabilities.isFlying = flying;
    }

    /*public static void attack(EntityLivingBase entityLivingBase) {
        mc.thePlayer.attackTargetEntityWithCurrentItem(entityLivingBase);
    }

    public static double getDistanceToEntity(EntityLivingBase entityLivingBase) {
        return mc.thePlayer.getDistanceToEntity(entityLivingBase);
    }

    public static void sendPacket(Packet packet) {
        PacketUtil.sendPacket(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        PacketUtil.sendPacketNoEvent(packet);
    }*/

    public static Result getEntitiesInWorld() {
        List<Value> entities = new ArrayList<>();

        mc.theWorld.loadedEntityList.forEach(entity -> {
            entities.add(Coercer.coerce(entity));
        });

        return new Success(new ListValue("entities", entities));
    }

    public static void sendPacket(JvmClassInstanceValue<? extends Packet<?>> packet) {
        PacketUtil.sendPacket(packet.getInstance());
    }

    public static double getFallDistance() {
        return mc.thePlayer.fallDistance;
    }

    public static void setFallDistance(float fallDisatcne) {
        mc.thePlayer.fallDistance = fallDisatcne;
    }

    public static boolean isMoving() {
        return Wrapper.getMonsoon().getPlayer().isMoving();
    }

    public static double getSpeed() {
        return Wrapper.getMonsoon().getPlayer().getSpeed();
    }

    public static double getBaseSpeed() {
        return Wrapper.getMonsoon().getPlayer().getBaseMoveSpeed();
    }

}
