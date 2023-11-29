package wtf.monsoon.client.modules.movement.flight

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.block.BlockAir
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventBlockCollide
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight
import wtf.monsoon.client.util.misc.Stopwatch
import kotlin.math.roundToInt

class VerusFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    private val verusMode: Setting<Mode> = Setting("Verus Mode", Mode.AIRWALK)

    private val fastVerus: Setting<Boolean> = Setting("Fast", false)
        .visibleWhen { verusMode.getValue() === Mode.BRUE }

    private var ticks = 0

    private val timer: Stopwatch = Stopwatch()

    init {
       this.registerSettings(verusMode, fastVerus)
    }


    override fun enable() {
        super.enable()
        if (verusMode.getValue() === Mode.BRUE) {
            ticks = 0
            player.setSpeed(0.0)
            mc.thePlayer.setVelocity(0.0, 0.0, 0.0)
        }
    }

    override fun disable() {
        super.disable()
        mc.thePlayer.setVelocity(0.0, 0.0, 0.0)
        player.setSpeed(0.0)
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        when (verusMode.getValue()) {
            Mode.AIRWALK -> {
                mc.thePlayer.onGround = true
                e.onGround = mc.thePlayer.ticksExisted % 2 == 0
                mc.thePlayer.motionY = 0.0
                e.y = mc.thePlayer.posY.roundToInt().toDouble()
                mc.thePlayer.sendQueue.addToSendQueue(
                    C08PacketPlayerBlockPlacement(
                        BlockPos(
                            mc.thePlayer.prevPosX,
                            mc.thePlayer.posY - 1,
                            mc.thePlayer.prevPosZ
                        ), 1, ItemStack(Blocks.stone), 1f, 1f, 1f
                    )
                )
            }

            Mode.BRUE -> {}
        }
    }

    @EventListener
    val eventMove = fun(e: EventMove) {
        when (verusMode.getValue()) {
            Mode.BRUE -> {
                if (ticks % 14 == 0 && player.isOnGround) {
                    player.setSpeed(e, 1f)
                    e.y = 0.42
                    mc.thePlayer.motionY = -(mc.thePlayer.posY - (mc.thePlayer.posY - mc.thePlayer.posY % 0.015625))
                } else {
                    if (fastVerus.getValue()) {
                        if (player.isOnGround) {
                            player.setSpeed(e, player.baseMoveSpeed * 2.65f)
                        } else player.setSpeed(e, 0.41)
                    } else {
                        if (player.isOnGround) {
                            if (mc.thePlayer.moveStrafing == 0f) {
                                val multiplier =
                                    (1.0f + if (mc.thePlayer.motionY < 0f) mc.thePlayer.motionY * -6f else mc.thePlayer.motionY * 6f).toFloat()
                                if (mc.thePlayer.hurtTime > 0) player.setSpeed(
                                    e,
                                    player.baseMoveSpeed * 1.7f
                                ) else player.setSpeed(e, player.baseMoveSpeed * multiplier)
                            } else {
                                player.setSpeed(e, player.speed * 0.9f)
                            }
                        } else player.setSpeed(e, 0.41)
                    }
                }
                ticks++
            }

            Mode.AIRWALK -> {}
        }
    }

    @EventListener
    val eventBlockCollide = fun(e: EventBlockCollide) {
        when (verusMode.getValue()) {
            Mode.BRUE ->
                if (e.block is BlockAir && !mc.thePlayer.isSneaking) {
                    val x: Double = e.x!!
                    val y: Double = e.y!!
                    val z: Double = e.z!!
                    if (y < mc.thePlayer.posY)
                        e.collisionBoundingBox = AxisAlignedBB.fromBounds(-15.0, -1.0, -15.0, 15.0, 1.0, 15.0).offset(x, y, z)
                }

            Mode.AIRWALK -> {}
        }
    }

    private enum class Mode {
        BRUE, AIRWALK
    }

}