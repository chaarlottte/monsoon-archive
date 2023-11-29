package wtf.monsoon.client.modules.movement.flight

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventBlockCollide
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.modules.movement.Flight
import wtf.monsoon.client.util.misc.Stopwatch
import wtf.monsoon.client.util.network.PacketUtil
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

class VulcanFlight(name: String, parent: MulticlassModule) : ModuleMode<Flight>(name, parent) {

    private val mode: Setting<Mode> = Setting("Vulcan Mode", Mode.FAST)

    private val speed: Setting<Float> = Setting("Vulcan Speed", 0.77f)
        .minimum(0.1f)
        .maximum(1.0f)
        .incrementation(0.01f)

    private var ticks = 0
    private var vticks = 0
    private var stage = 0

    private val timer: Stopwatch = Stopwatch()

    private var startX = 0.0
    private var startY = 0.0
    private var startZ = 0.0

    private var lastTickX = 0.0
    private var lastTickY = 0.0
    private var lastTickZ = 0.0
    private var lastSentX = 0.0
    private var lastSentY = 0.0
    private var lastSentZ = 0.0
    private val lastMotionX = 0.0
    private var getLastMotionZ = 0.0
    private var startYaw = 0f
    private var startPitch = 0f
    private var waitFlag = false
    private var started = false
    private var damaged = false
    private var playedFakeDmg = false

    private var jumps = 0

    init {
       this.registerSettings(mode, speed)
    }

    override fun enable() {
        super.enable()
        playedFakeDmg = false
        when (mode.getValue()) {
            Mode.AIRWALK -> {
                startX = mc.thePlayer.posX
                startY = mc.thePlayer.posY
                startZ = mc.thePlayer.posZ
                ticks = 0
                mc.thePlayer.jump()
            }
            Mode.TEST -> if (mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42
            Mode.FAST -> {
                ticks = 0
                waitFlag = false
                started = false
                damaged = false
                jumps = 0
                startYaw = mc.thePlayer.rotationYaw
                startPitch = mc.thePlayer.rotationPitch
                mc.timer.timerSpeed = 1.0f
                damage()
            }

            else -> {}
        }
    }

    override fun disable() {
        super.disable()
        player.setSpeed(0.0)
        mc.thePlayer.setVelocity(0.0, 0.0, 0.0)
        mc.timer.timerSpeed = 1f
        when (mode.getValue()) {
            Mode.AIRWALK -> mc.thePlayer.motionY = -0.09800000190735147
            Mode.FAST -> PacketUtil.sendPacketNoEvent(C04PacketPlayerPosition(lastTickX, lastTickY, lastTickZ, false))
            else -> {}
        }
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        when (mode.getValue()) {
            Mode.AIRWALK -> {
                ticks++
                if (ticks >= 4) {
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.setPosition(
                        mc.thePlayer.posX,
                        (e.y / 0.5).roundToInt() * 0.5,
                        mc.thePlayer.posZ
                    )
                    player.setSpeed(0.2694 * (1 + speed.getValue()))
                }
                if (ticks in 0..20 && ticks >= 4 || mc.thePlayer.posY % 0.5 == 0.0) {
                    val mathGround2 = (e.y / 0.015625).roundToInt() * 0.015625
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mathGround2, mc.thePlayer.posZ)
                    e.y = mathGround2
                    e.onGround = true
                }
            }

            Mode.TEST -> {
                e.onGround = true
            }

            Mode.FAST -> {
                if (!this.damage()) {
                    e.yaw = this.startYaw
                    e.pitch = this.startPitch
                    mc.thePlayer.jumpMovementFactor = 0.00f
                    if (!this.started && !this.waitFlag) {
                        PacketUtil.sendPacketNoEvent(C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 0.0784, mc.thePlayer.posZ, 0f, 0f, false))
                        // e.setY(mc.thePlayer.posY - 0.0784);
                        this.waitFlag = true
                    }
                    if (this.started) {
                        mc.timer.timerSpeed = 1.0f
                        if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                            mc.thePlayer.motionZ = 0.0
                            mc.thePlayer.motionY = 0.0
                            mc.thePlayer.motionX = 0.0
                            if (mc.gameSettings.keyBindJump.isKeyDown) {
                                mc.thePlayer.motionY = 0.42
                            }
                        }
                        this.ticks++
                        if (this.ticks > 4)
                            this.ticks = 4

                        mc.timer.timerSpeed = 1.2f
                        player.setSpeed(this.speed.getValue() * 2)
                    }
                } else {
                    Wrapper.monsoon.log("not done dmg", Monsoon.Level.DEBUG)
                }
            }

            else -> {}
        }
    }

    @EventListener
    val eventMove = fun(e: EventMove) {
        when (mode.getValue()) {
            Mode.AIRWALK -> {}
            Mode.TEST -> {
                mc.timer.timerSpeed = 0.1f
                e.y = 0.also { mc.thePlayer.motionY = 0.toDouble() }.toDouble()
                player.setSpeed(e, player.baseMoveSpeed * 7)
            }

            else -> {}
        }
    }

    @EventListener
    val eventPacket = fun(e: EventPacket) {
        when (mode.getValue()) {
            Mode.AIRWALK -> {
                if (e.packet is S08PacketPlayerPosLook) {
                    val packet = e.packet as S08PacketPlayerPosLook
                    if (mc.thePlayer.ticksExisted > 20 && !mc.isSingleplayer) {
                        if (abs(packet.x - startX) + abs(packet.y - startY) + abs(packet.z - startZ) < 4.0) {
                            e.cancel()
                        }
                    }
                }
            }

            Mode.TEST -> {
                if (e.packet is C03PacketPlayer) {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        e.cancel()
                    }
                }
                if (e.packet is S08PacketPlayerPosLook) {
                    this.parent.toggle()
                }
            }

            Mode.FAST -> {
                if (e.packet is C03PacketPlayer && this.waitFlag)
                    e.cancel()

                if (this.started) {
                    if (e.packet is C03PacketPlayer && e.packet !is C05PacketPlayerLook) {
                        val packet = e.packet as C03PacketPlayer
                        val deltaX = packet.x - this.lastSentX
                        val deltaY = packet.y - this.lastSentY
                        val deltaZ = packet.z - this.lastSentZ
                        val sqrt = sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
                        if (sqrt > 10) {
                            // PlayerUtil.sendClientMessage("poop");
                            Wrapper.monsoon.log(sqrt.toString() + "", Monsoon.Level.DEBUG)
                            PacketUtil.sendPacketNoEvent(C04PacketPlayerPosition(this.lastTickX, this.lastTickY, this.lastTickZ, false))
                            // PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(this.lastTickX, this.lastTickY, this.lastTickZ, this.startYaw, this.startPitch, false));
                            this.lastSentX = this.lastTickX
                            this.lastSentY = this.lastTickY
                            this.lastSentZ = this.lastTickZ
                        }
                        this.lastTickX = packet.x
                        this.lastTickY = packet.y
                        this.lastTickZ = packet.z
                        e.cancel()
                    } else if (e.packet is C03PacketPlayer) {
                        e.cancel()
                    }
                }
                if (e.packet is S08PacketPlayerPosLook && waitFlag) {
                    val packet = e.packet as S08PacketPlayerPosLook
                    this.lastSentX = packet.x
                    this.lastSentY = packet.y
                    this.lastSentZ = packet.z
                    PacketUtil.sendPacketNoEvent(C06PacketPlayerPosLook(packet.x, packet.y, packet.z, packet.yaw, packet.pitch, false))
                    mc.thePlayer.setPosition(packet.x, packet.y, packet.z)
                    e.cancel()
                    this.started = true
                    this.waitFlag = false
                }
            }

            else -> {}
        }
    }

    private fun damage(): Boolean {
        mc.timer.timerSpeed = 1.0f
        if (this.damaged) {
            this.jumps = 999
            if (!this.playedFakeDmg) {
                player.fakeDamage()
                this.playedFakeDmg = true
            }
            return false
        }
        mc.thePlayer.jumpMovementFactor = 0.00f
        if (mc.thePlayer.onGround) {
            if (this.jumps >= 4) {
                PacketUtil.sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
                damaged = true
                jumps = 999
                if (!playedFakeDmg) {
                    player.fakeDamage()
                    playedFakeDmg = true
                }
                return false
            }
            jumps++
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            Wrapper.monsoon.log("jump", Monsoon.Level.DEBUG)
            mc.thePlayer.jump()
        }
        mc.thePlayer.motionZ = 0.0
        mc.thePlayer.motionX = 0.0
        return true
    }

    internal enum class Mode {
        GLIDE, FAST, AIRWALK, TEST
    }

}