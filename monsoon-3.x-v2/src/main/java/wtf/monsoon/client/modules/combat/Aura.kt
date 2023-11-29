package wtf.monsoon.client.modules.combat

import me.bush.eventbuskotlin.Event
import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.MathHelper
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPostMotion
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.event.EventUpdate
import wtf.monsoon.client.modules.player.Scaffold
import wtf.monsoon.client.util.math.randomNumber
import wtf.monsoon.client.util.misc.Stopwatch
import wtf.monsoon.client.util.player.RotationUtil

class Aura : Module("Aura", "Automatically attack entities around you.", Category.COMBAT) {
    private val cps: Setting<Double> = Setting("CPS", "Maximum clicks per second", 10.0)
        .minimum(1.0)
        .maximum(20.0)
        .incrementation(0.5)

    private val range: Setting<Double> = Setting("Range", "The range to attack", 4.0)
        .minimum(2.0)
        .maximum(6.0)
        .incrementation(0.1)

    private val attackEvent = Setting("Attack Event", AttackEvent.PRE)

    var target: EntityLivingBase? = null

    private val attackTimer: Stopwatch = Stopwatch()
    private var lastDelay = 1000 / this.cps.getValue()

    private var finalYaw: Float = 0f;
    private var finalPitch: Float = 0f;

    private var blocking: Boolean = false
    private var isAttackTick: Boolean = false

    override fun enable() {
        super.enable()
        this.attackTimer.reset()
        if(mc.thePlayer != null) {
            this.finalYaw = mc.thePlayer.rotationYaw
            this.finalPitch = mc.thePlayer.rotationPitch
        }
        this.blocking = false
        this.isAttackTick = false
    }

    @EventListener
    val onUpdate = fun(_: EventUpdate) {
        this.isAttackTick = false
        if(this.attackTimer.hasTimeElapsed(this.lastDelay)) {
            this.isAttackTick = true
            this.lastDelay = (1000 / this.cps.getValue()) + randomNumber(10, -10)
        }
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        this.target = this.getSingleTarget()

        if(this.target == null)
            return

        RotationUtil.getRotations(target!!)
            .also { this.finalYaw = it[0] }
            .also { e.yaw = it[0] }

            .also { this.finalPitch = it[1] }
            .also { e.pitch = it[1] }


        mc.thePlayer.rotationYawHead = e.yaw.also { mc.thePlayer.renderYawOffset = RotationUtil.getRenderYawOffset(it) }
        mc.thePlayer.rotationPitchHead = e.pitch

        if(this.isAttackTick)
            this.target?.let { this.attack(it, e) }
    }

    @EventListener
    val postMotion = fun(e: EventPostMotion) {
        if(this.isAttackTick)
            this.target?.let { this.attack(it, e) }
    }

    private fun attack(e: EntityLivingBase, event: Event) {
        if(event.javaClass == this.attackEvent.getValue().clazz) {
            mc.thePlayer.swingItem()
            mc.playerController.attackEntity(mc.thePlayer, e)
        }
    }

    private fun getSingleTarget(): EntityLivingBase? {
        val targets: List<EntityLivingBase> = Wrapper.monsoon.targetsManager.getTargets(this.range.getValue())
            .asSequence()
            .filter { entity -> entity != Minecraft.getMinecraft().thePlayer }
            .filter { entity -> entity.ticksExisted > 0 }
            .filter { entity -> mc.theWorld.loadedEntityList.contains(entity) }
            .filter { entity -> !entity.isDead && entity.health > 0 }
            .sortedBy { entity -> mc.thePlayer.getDistanceSq(entity.posX, entity.posY, entity.posZ) }
            .toList()
        return targets.firstOrNull()
    }

    fun shouldRenderBlockAnim(): Boolean {
        return this.isEnabled()
                && this.target != null
                && !this.target!!.isDead
                && mc.thePlayer.getDistanceToEntity(this.target!!) <= this.range.getValue()
                && mc.thePlayer != null
                && mc.thePlayer.heldItem != null
                && mc.thePlayer.heldItem.item != null
    }

    enum class AttackEvent(var clazz: Class<out Event>) {
        PRE(EventPreMotion::class.java),
        POST(EventPostMotion::class.java)
    }
}