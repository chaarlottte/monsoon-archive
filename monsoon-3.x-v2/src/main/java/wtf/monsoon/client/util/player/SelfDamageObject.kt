package wtf.monsoon.client.util.player

import me.bush.eventbuskotlin.EventListener
import net.minecraft.init.Items
import net.minecraft.init.Items.arrow
import net.minecraft.item.Item
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.client.event.EventMovementInput
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.util.network.PacketUtil
import wtf.monsoon.misc.InstanceAccess


class SelfDamageObject(val type: DamageType, val timer: Float = 1.0f) : InstanceAccess() {

    private var done = false
    private var running = false

    // DamageType.JUMP
    private var jumps: Int = 0

    // DamageType.ITEM
    private var slot: Int = -1
    private var time: Int = 0
    private var itemType: ItemType = ItemType.BOW

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        if(!this.running) return
        when(this.type) {
            DamageType.JUMP -> {
                if(this.running) {
                    if(this.jumps < 3) {
                        mc.timer.timerSpeed = this.timer

                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.jump()
                            this.jumps++
                            Wrapper.monsoon.log("jumped!!! total jumps: ${this.jumps}", Monsoon.Level.DEBUG)
                        }

                        e.onGround = true
                    } else if(mc.thePlayer.onGround) {
                        mc.timer.timerSpeed = 1.0F;
                        this.running = false
                        this.done = true
                        this.end()
                    }
                }
            }
            DamageType.ITEM -> {
                if (this.running) {
                    val bowSlot: Int = player.findItem(Items.bow)
                    val rodSlot: Int = player.findItem(Items.fishing_rod)
                    val snowBall: Int = player.findItem(Items.snowball)
                    val egg: Int = player.findItem(Items.egg)
                    if (bowSlot != -1 && player.hasItem("arrow")) {
                        player.setSlot(bowSlot)
                        this.itemType = ItemType.BOW
                    } else if (rodSlot != -1) {
                        player.setSlot(rodSlot)
                        this.itemType = ItemType.ROD
                    } else if (snowBall != -1) {
                        player.setSlot(snowBall)
                        this.itemType = ItemType.PROJECTILES
                    } else if (egg != -1) {
                        player.setSlot(egg)
                        this.itemType = ItemType.PROJECTILES
                    }
                    time++
                    when (this.itemType) {
                        ItemType.BOW ->
                            when (time) {
                                3 ->
                                    PacketUtil.sendPacketNoEvent(C08PacketPlayerBlockPlacement(player.getItemStack()))
                                7 ->
                                    PacketUtil.sendPacketNoEvent(
                                        C07PacketPlayerDigging(
                                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                            BlockPos.ORIGIN,
                                            EnumFacing.DOWN
                                        )
                                    )

                                40 ->
                                    this.end()
                            }

                        ItemType.ROD -> {
                            when (time) {
                                3 ->
                                    PacketUtil.sendPacketNoEvent(C08PacketPlayerBlockPlacement(player.getItemStack()))
                                95 ->
                                    this.end()
                            }
                            if (mc.thePlayer.hurtTime == 9) {
                                PacketUtil.sendPacketNoEvent(C08PacketPlayerBlockPlacement(player.getItemStack()))
                            }
                        }

                        ItemType.PROJECTILES ->
                            when (time) {
                                3, 4, 5, 6 ->
                                    PacketUtil.sendPacketNoEvent(C08PacketPlayerBlockPlacement(player.getItemStack()))
                                100 ->
                                    this.end()
                            }
                    }

                    if (mc.thePlayer.hurtTime == 9)
                        this.end()

                    this.end()

                }
            }
        }
    }

    @EventListener
    val movementInput = fun(e: EventMovementInput) {
        if(!this.running) return
        if(this.running) {
            e.forward = 0f
            e.strafe = 0f
        }
    }

    @EventListener
    val packetEvent = fun(e: EventPacket) {
        if(this.type == DamageType.ITEM) {
            if(e.packet is S12PacketEntityVelocity)
                if((e.packet as S12PacketEntityVelocity).entityID == mc.thePlayer.entityId)
                    this.end()

            if(e.packet is S27PacketExplosion)
                this.end()
        }
    }

    fun isReady(): Boolean {
        return this.done
    }

    fun start() {
        Wrapper.monsoon.bus.subscribe(this)
        this.running = true
    }

    private fun end() {
        this.done = true
        this.running = false
        Wrapper.monsoon.bus.unsubscribe(this)
        Wrapper.monsoon.log("we end damage!!", Monsoon.Level.DEBUG)
    }

    enum class DamageType {
        ITEM, JUMP
    }

    private enum class ItemType {
        BOW, ROD, PROJECTILES
    }
}