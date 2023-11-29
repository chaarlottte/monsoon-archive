package wtf.monsoon.client.modules.player

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPostMotion
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight
import wtf.monsoon.client.util.network.PacketUtil
import wtf.monsoon.client.util.math.getClosestMultipleOfDivisor

class NoSlow : Module("No Slow", "Take no fall damage", Category.PLAYER) {
    val mode = Setting<Mode>("Mode", Mode.VANILLA)

    @EventListener
    val preMotion = fun(_: EventPreMotion) {
        when(mode.getValue()) {
            Mode.NCP ->
                if(mc.thePlayer.isUsingItem && player.moving)
                    if(mc.thePlayer.heldItem.item is ItemSword)
                        PacketUtil.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))

            Mode.ITEM_SWITCH_BUFFER ->
                if(mc.thePlayer.isUsingItem && player.moving)
                    PacketUtil.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))

            else -> {}
        }
    }

    @EventListener
    val postMotion = fun(e: EventPostMotion) {
        when(mode.getValue()) {
            Mode.NCP ->
                if(mc.thePlayer.isUsingItem && player.moving)
                    if(mc.thePlayer.heldItem.item is ItemSword)
                        PacketUtil.sendPacketNoEvent(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))

            else -> {}
        }
    }

    fun shouldUseNoSlow(): Boolean {
        return this.isEnabled()
    }

    enum class Mode {
        VANILLA, NCP, ITEM_SWITCH_BUFFER
    }
}