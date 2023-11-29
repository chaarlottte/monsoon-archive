package wtf.monsoon.api.util.entity;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Monsoon;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.misc.PacketUtil;

/**
 * @author Surge
 * @since 30/07/2022
 */
public class PlayerUtil extends Util {

    public static final String chatPrefix = EnumChatFormatting.WHITE + "<" + EnumChatFormatting.AQUA + "monsoon" + EnumChatFormatting.WHITE + "> " + EnumChatFormatting.RESET;
    public static final String debugPrefix = EnumChatFormatting.WHITE + "<" + EnumChatFormatting.YELLOW + "debug" + EnumChatFormatting.WHITE + "> " + EnumChatFormatting.RESET;

    /**
     * Adds a message to the player's chat, without sending it to the server.
     * <p/>
     * Includes a prefix before the message.
     * <p/>
     *
     * @param message The message content to send.
     */
    public static void sendClientMessage(String message) {
        Wrapper.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(chatPrefix + message));
    }

    public static void debug(String message) {
        if (Wrapper.isDebugModeEnabled()) {
            if (Monsoon.DEBUG_MODE)
                Wrapper.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(debugPrefix + message));
        }
    }

    public static boolean isBlockAbovePlayer() {
        return mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, 2, 0)).getBlock() != null &&
               // mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, 2, 0)).getBlock().isFullBlock() &&
               mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, 2, 0)).getBlock().isCollidable() &&
               !(mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, 2, 0)).getBlock() instanceof BlockAir);
    }

    public static void oldNCPDamage() {
        for (int i = 0; i < 50; ++i) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625D, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static void damageVerus() {
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.1001, mc.thePlayer.posZ, false));
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        mc.thePlayer.jump();
    }

    public static void damageHypixel() {
        for (int i = 0; i < 50; ++i) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625D, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    public static void damageSpartan() {
        for (int i = 0; i < 64; i++) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.049, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }

        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, true));
        PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
    }

    public static void fakeDamage() {
        mc.thePlayer.performHurtAnimation();
        mc.thePlayer.playSound(mc.thePlayer.getHurtSound(), mc.thePlayer.getSoundVolume(), mc.thePlayer.getSoundPitch());
    }

    public static void damageCustomAmount(double damage) {

        Minecraft mc = Minecraft.getMinecraft();

        if (damage > floor_double(mc.thePlayer.getMaxHealth()))
            damage = floor_double(mc.thePlayer.getMaxHealth());

        double offset = 0.0625;
        //offset = 0.015625;
        if (mc.thePlayer != null) {
            for (short i = 0; i <= ((3 + damage) / offset); i++) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY + ((offset / 2) * 1), mc.thePlayer.posZ, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY + ((offset / 2) * 2), mc.thePlayer.posZ, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, (i == ((3 + damage) / offset))));
            }
        }
    }

    private boolean damageVulcan(boolean damaged, int jumps, boolean playedFakeDmg) {
        mc.getTimer().timerSpeed = 1.0f;
        if (damaged) {
            jumps = 999;
            if(!playedFakeDmg) {
                PlayerUtil.fakeDamage();
                playedFakeDmg = true;
            }
            return false;
        }
        mc.thePlayer.jumpMovementFactor = 0.00f;
        if (mc.thePlayer.onGround) {
            if (jumps >= 4) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                damaged = true;
                jumps = 999;
                if(!playedFakeDmg) {
                    PlayerUtil.fakeDamage();
                    playedFakeDmg = true;
                }
                return false;
            }
            jumps++;
            mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
            mc.thePlayer.jump();
        }
        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        return true;
    }

    public static int floor_double(double p_76128_0_) {
        int var2 = (int) p_76128_0_;
        return p_76128_0_ < (double) var2 ? var2 - 1 : var2;
    }

    public static double getPlayerSpeed() {
        return mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ) * (Minecraft.getMinecraft().getTimer().ticksPerSecond * Minecraft.getMinecraft().getTimer().timerSpeed);
    }

}
