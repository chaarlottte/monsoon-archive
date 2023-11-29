package wtf.monsoon.backend.manager.script.link

import net.minecraft.network.Packet
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0CPacketInput
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C0EPacketClickWindow
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import spritz.api.annotations.Identifier

/**
 * @author surge
 * @since 03/04/2023
 */
class PacketLink : Link() {

    @Identifier("C0APacketAnimation") @JvmField val c0APacketAnimation = C0APacketAnimation::class.java
    @Identifier("C0BPacketEntityAction") @JvmField val c0BPacketEntityAction = C0BPacketEntityAction::class.java
    @Identifier("C0CPacketInput") @JvmField val c0CPacketInput = C0CPacketInput::class.java
    @Identifier("C0DPacketCloseWindow") @JvmField val c0DPacketCloseWindow = C0DPacketCloseWindow::class.java
    @Identifier("C0EPacketClickWindow") @JvmField val c0EPacketClickWindow = C0EPacketClickWindow::class.java
    @Identifier("C0FPacketConfirmTransaction") @JvmField val c0FPacketConfirmTransaction = C0FPacketConfirmTransaction::class.java

    @Identifier("C00PacketKeepAlive") @JvmField val c00PacketKeepAlive = C00PacketKeepAlive::class.java
    @Identifier("C01PacketChatMessage") @JvmField val c01PacketChatMessage = C01PacketChatMessage::class.java
    @Identifier("C02PacketUseEntity") @JvmField val c02PacketUseEntity = C02PacketUseEntity::class.java
    @Identifier("C03PacketPlayer") @JvmField val c03PacketPlayer = C03PacketPlayer::class.java

    @Identifier("send")
    fun send(packet: Packet<*>) {
        mc.thePlayer.sendQueue.addToSendQueue(packet)
    }

}