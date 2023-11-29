package wtf.monsoon.client.util.network

import net.minecraft.client.Minecraft
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import wtf.monsoon.client.util.Util

object PacketUtil : Util() {
    fun sendPacket(p: Packet<*>?) {
        mc.netHandler.networkManager.sendPacket(p)
    }

    fun sendPacketNoEvent(p: Packet<*>?) {
        mc.netHandler.networkManager.sendPacketNoEvent(p)
    }

    fun sendFunnyPacket() {
        sendPacketNoEvent(
            C04PacketPlayerPosition(
                Minecraft.getMinecraft().thePlayer.posX,
                Math.PI / 100E-10,
                Minecraft.getMinecraft().thePlayer.posZ,
                false
            )
        )
    }

    fun fixRightClick(): Float {
        /*return if (ViaMCP.getInstance().getVersion() === ViaMCP.PROTOCOL_VERSION) {
            16.0f
        } else {
            1.0f
        }*/
        return 1.0f
    }

    fun sendBlocking(callEvent: Boolean, placement: Boolean) {
        if (mc.thePlayer == null) return
        if (placement) {
            val packet =
                C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.heldItem, 0f, 0f, 0f)
            if (callEvent) {
                sendPacket(packet)
            } else {
                sendPacketNoEvent(packet)
            }
        } else {
            val packet = C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem)
            if (callEvent) {
                sendPacket(packet)
            } else {
                sendPacketNoEvent(packet)
            }
        }
    }

    fun releaseUseItem(callEvent: Boolean) {
        if (mc.thePlayer == null) return
        val packet =
            C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN)
        if (callEvent) {
            sendPacket(packet)
        } else {
            sendPacketNoEvent(packet)
        }
    }
}