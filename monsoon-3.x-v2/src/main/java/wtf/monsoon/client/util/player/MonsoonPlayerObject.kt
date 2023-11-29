package wtf.monsoon.client.util.player

import net.minecraft.block.BlockAir
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.potion.Potion
import net.minecraft.stats.StatList
import net.minecraft.util.MathHelper
import wtf.monsoon.Wrapper
import wtf.monsoon.client.event.EventMove
import wtf.monsoon.client.modules.combat.Aura
import wtf.monsoon.client.modules.combat.TargetStrafe
import wtf.monsoon.client.modules.movement.Sprint
import wtf.monsoon.client.util.network.PacketUtil
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MonsoonPlayerObject {
    private val mc = Minecraft.getMinecraft()
    private val walkSpeed = 0.221
    private val frictionValues: MutableList<Double> = ArrayList()
    private val minDif = 1.0E-2
    private val bunnyDivFriction = 160.0 - minDif
    private val airFriction = 0.98
    private val waterFriction = 0.89
    private val lavaFriction = 0.535

    val groundDivisor = 1.0 / 64.0

    val baseMoveSpeed: Double
        get() {
            var baseSpeed = 0.2873
            if (mc.thePlayer != null && mc.theWorld != null) if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(
                Potion.moveSpeed
            ).amplifier + 1)
            return baseSpeed
        }

    val speed: Float
        get() = sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ)
            .toFloat()

    val moving: Boolean
        get() = mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f

    fun isOnGround(height: Float): Boolean {
        return mc.theWorld.getCollidingBoundingBoxes(
            mc.thePlayer,
            mc.thePlayer.entityBoundingBox.offset(0.0, -height.toDouble(), 0.0)
        ).isNotEmpty()
    }

    fun setSpeed(event: EventMove, speed: Number) {
        val (targetStrafeEnabled, dir) = targetStrafeCheck()
        val direction = if(targetStrafeEnabled) dir else Math.toRadians(MovementUtil.direction.toDouble()).toFloat()
        if (moving) {
            event.x = (-sin(direction.toDouble()) * speed.toDouble()).also { mc.thePlayer.motionX = it }
            event.z = (cos(direction.toDouble()) * speed.toDouble()).also { mc.thePlayer.motionZ = it }
        } else {
            event.x = (0.also { mc.thePlayer.motionX = 0.0 }.toDouble())
            event.z = (0.also { mc.thePlayer.motionZ = 0.0 }.toDouble())
        }
    }

    fun setSpeed(speed: Number) {
        val (targetStrafeEnabled, dir) = targetStrafeCheck()
        val direction = if(targetStrafeEnabled) dir else Math.toRadians(MovementUtil.direction.toDouble()).toFloat()
        if (moving) {
            mc.thePlayer.motionX = (-sin(direction.toDouble()) * speed.toDouble())
            mc.thePlayer.motionZ = (cos(direction.toDouble()) * speed.toDouble())
        } else {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
    }

    fun setSpeed(event: EventMove, speed: Number, direction: Float) {
        val (targetStrafeEnabled, dire) = targetStrafeCheck()
        val dir = if(targetStrafeEnabled) dire else Math.toRadians(direction.toDouble()).toFloat()
        if (moving) {
            event.x = (-sin(dir.toDouble()) * speed.toDouble()).also { mc.thePlayer.motionX = it }
            event.z = (cos(dir.toDouble()) * speed.toDouble()).also { mc.thePlayer.motionZ = it }
        } else {
            event.x = 0.also { mc.thePlayer.motionX = 0.0 }.toDouble()
            event.z = 0.also { mc.thePlayer.motionZ = 0.0 }.toDouble()
        }
    }

    fun setSpeedWithCorrection(
        event: EventMove,
        speed: Number,
        lastMotionX: Double,
        lastMotionZ: Double,
        modifier: Double
    ) {

        val (targetStrafeEnabled, dir) = targetStrafeCheck()
        val direction = if(targetStrafeEnabled) dir else Math.toRadians(MovementUtil.direction.toDouble()).toFloat()
        if (moving) {
            event.x = (-sin(direction.toDouble()) * speed.toDouble()).also { mc.thePlayer.motionX = it }
            event.z = (cos(direction.toDouble()) * speed.toDouble()).also { mc.thePlayer.motionZ = it }
        } else {
            event.x = 0.also { mc.thePlayer.motionX = 0.0 }.toDouble()
            event.z = 0.also { mc.thePlayer.motionZ = 0.0 }.toDouble()
        }
        if (event.x > 0 && event.x > lastMotionX) {
            event.x = (lastMotionX + (event.x - lastMotionX) * modifier).also { mc.thePlayer.motionX = it }
        } else if (event.x < 0 && event.x < lastMotionX) {
            event.x = (lastMotionX - (lastMotionX - event.x) * modifier).also { mc.thePlayer.motionX = it }
        }
        if (event.z > 0 && event.z > lastMotionZ) {
            event.z = (lastMotionZ + (event.z - lastMotionZ) * modifier).also { mc.thePlayer.motionZ = it }
        } else if (event.z< 0 && event.z < lastMotionZ) {
            event.z = (lastMotionZ - (lastMotionZ - event.z) * modifier).also { mc.thePlayer.motionZ = it }
        }
    }

    private fun targetStrafeCheck(): Pair<Boolean, Float> {
        var direction = 0f

        val targetStrafe = Wrapper.monsoon.getModule(TargetStrafe::class.java)
        val aura = Wrapper.monsoon.getModule(Aura::class.java)

        val targetStrafeEnabled =
            targetStrafe.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown

        if(Wrapper.monsoon.getModule(Aura::class.java).isEnabled() && Wrapper.monsoon.getModule(Aura::class.java).target != null && targetStrafeEnabled) {
            if (mc.thePlayer.isCollidedHorizontally)
                targetStrafe.direction = !targetStrafe.direction

            direction = if (mc.thePlayer.getDistanceToEntity(aura.target) >= targetStrafe.distance.getValue()) {
                RotationUtil.getRotations(aura.target!!)[0]
            } else {
                RotationUtil.getRotations(aura.target!!)[0] + if (targetStrafe.direction) 91 - mc.thePlayer.getDistanceToEntity(aura.target) * 3 else -91 + mc.thePlayer.getDistanceToEntity(aura.target) * 3
            }

            direction = Math.toRadians(direction.toDouble()).toFloat()
            return Pair(true, direction)
        }
        return Pair(false, direction)
    }

    val jumpBoostModifier: Int
        get() {
            val effect = mc.thePlayer.getActivePotionEffect(Potion.jump)
            return if (effect != null) effect.amplifier + 1 else 0
        }

    fun getJumpHeight(baseJumpHeight: Double): Double {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava) {
            return 0.221 * (0.115 / 0.221) + 0.02f
        } else if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + (mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1.0f) * 0.1f
        }
        return baseJumpHeight
    }

    fun getJumpHeight(baseJumpHeight: Float): Float {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava) {
            return 0.221f * (0.115f / 0.221f) + 0.02f
        } else if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return baseJumpHeight + (mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1.0f) * 0.1f
        }
        return baseJumpHeight
    }

    fun strafe() {
        setSpeed(speed.toDouble())
    }

    fun strafe(event: EventMove) {
        setSpeed(event, speed.toDouble())
    }

    var isOnGround: Boolean
        get() = mc.thePlayer.onGround
        set(onGround) {
            mc.thePlayer.onGround = onGround
        }

    @JvmOverloads
    fun jump(motionY: Float = this.getJumpHeight(0.42f)) {
        mc.thePlayer.motionY = this.getJumpHeight(motionY).toDouble()
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            mc.thePlayer.motionY += ((mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1).toFloat() * 0.1f).toDouble()
        }
        if (mc.thePlayer.isSprinting) {
            val sprint: Sprint = Wrapper.monsoon.moduleManager.getModule(Sprint::class.java)
            val f =
                (if (sprint.isEnabled() && sprint.omni.getValue()) MovementUtil.direction else mc.thePlayer.rotationYaw) * 0.017453292f
            mc.thePlayer.motionX -= (MathHelper.sin(f) * 0.2f).toDouble()
            mc.thePlayer.motionZ += (MathHelper.cos(f) * 0.2f).toDouble()
        }
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
        if (mc.thePlayer.isSprinting) {
            mc.thePlayer.addExhaustion(0.8f)
        } else {
            mc.thePlayer.addExhaustion(0.2f)
        }
    }

    fun jump(eventMove: EventMove, motionY: Float) {
        eventMove.y = this.getJumpHeight(motionY).also { mc.thePlayer.motionY = it.toDouble() }.toDouble()
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            eventMove.y =
                ((mc.thePlayer.getActivePotionEffect(Potion.jump).amplifier + 1).toFloat() * 0.1f).toDouble()
                    .let { mc.thePlayer.motionY += it; mc.thePlayer.motionY }
        }
        if (mc.thePlayer.isSprinting) {
            val sprint: Sprint = Wrapper.monsoon.moduleManager.getModule(Sprint::class.java)
            val f =
                (if (sprint.isEnabled() && sprint.omni.getValue()) MovementUtil.direction else mc.thePlayer.rotationYaw) * 0.017453292f
            eventMove.x =
                (MathHelper.sin(f) * 0.2f).toDouble().let { mc.thePlayer.motionX -= it; mc.thePlayer.motionX }
            eventMove.z =
                (MathHelper.cos(f) * 0.2f).toDouble().let { mc.thePlayer.motionZ += it; mc.thePlayer.motionZ }
        }
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
        if (mc.thePlayer.isSprinting) {
            mc.thePlayer.addExhaustion(0.8f)
        } else {
            mc.thePlayer.addExhaustion(0.2f)
        }
    }

    fun jump(eventMove: EventMove) {
        jump(eventMove, this.getJumpHeight(0.42f))
    }

    fun friction(e: EventMove, moveSpeed: Double, lastDist: Double) {
        frictionValues.clear()
        frictionValues.add(lastDist - lastDist / bunnyDivFriction)
        frictionValues.add(lastDist - (moveSpeed - lastDist) / 33.3)
        val materialFriction =
            if (mc.thePlayer.isInWater) waterFriction else if (mc.thePlayer.isInLava) lavaFriction else airFriction
        frictionValues.add(lastDist - baseMoveSpeed * (1.0 - materialFriction))
        this.setSpeed(e, Collections.min(frictionValues))
    }

    fun isBlockAbovePlayer(): Boolean {
        return mc.theWorld.getBlockState(
            mc.thePlayer.position.add(
                0,
                2,
                0
            )
        ).block != null &&  // mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, 2, 0)).getBlock().isFullBlock() &&
                mc.theWorld.getBlockState(mc.thePlayer.position.add(0, 2, 0)).block.isCollidable &&
                mc.theWorld.getBlockState(mc.thePlayer.position.add(0, 2, 0)).block !is BlockAir
    }

    fun oldNCPDamage() {
        for (i in 0..49) {
            PacketUtil.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.0625,
                    mc.thePlayer.posZ,
                    false
                )
            )
            PacketUtil.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }
        PacketUtil.sendPacketNoEvent(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
    }

    fun damageVerus() {
        PacketUtil.sendPacketNoEvent(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY + 3.1001,
                mc.thePlayer.posZ,
                false
            )
        )
        PacketUtil.sendPacketNoEvent(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                false
            )
        )
        PacketUtil.sendPacketNoEvent(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
        mc.thePlayer.jump()
    }

    fun damageHypixel() {
        for (i in 0..49) {
            PacketUtil.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.0625,
                    mc.thePlayer.posZ,
                    false
                )
            )
            PacketUtil.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }
        PacketUtil.sendPacketNoEvent(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ,
                true
            )
        )
    }

    fun damageSpartan() {
        for (i in 0..63) {
            PacketUtil.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.049,
                    mc.thePlayer.posZ,
                    false
                )
            )
            PacketUtil.sendPacketNoEvent(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
        }
        PacketUtil.sendPacketNoEvent(
            C04PacketPlayerPosition(
                mc.thePlayer.posX,
                mc.thePlayer.posY + 0.1,
                mc.thePlayer.posZ,
                true
            )
        )
        PacketUtil.sendPacketNoEvent(C0APacketAnimation())
    }

    fun fakeDamage() {
        mc.thePlayer.performHurtAnimation()
        mc.thePlayer.playSound(mc.thePlayer.hurtSound, mc.thePlayer.soundVolume, mc.thePlayer.soundPitch)
    }

    fun damageCustomAmount(d: Double) {
        var damage = d
        val mc = Minecraft.getMinecraft()
        if (damage > floor_double(mc.thePlayer.maxHealth.toDouble())) damage = floor_double(mc.thePlayer.maxHealth.toDouble()).toDouble()
        val offset = 0.0625
        if (mc.thePlayer != null) {
            var i: Short = 0
            while (i <= (3 + damage) / offset) {
                PacketUtil.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + offset / 2 * 1, mc.thePlayer.posZ, false
                    )
                )
                PacketUtil.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY + offset / 2 * 2, mc.thePlayer.posZ, false
                    )
                )
                PacketUtil.sendPacketNoEvent(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY, mc.thePlayer.posZ, i.toDouble() == (3 + damage) / offset
                    )
                )
                PacketUtil.sendPacketNoEvent(
                    C06PacketPlayerPosLook(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        mc.thePlayer.rotationYaw,
                        mc.thePlayer.rotationPitch,
                        i.toDouble() == (3 + damage) / offset
                    )
                )
                i++
            }
        }
    }

    fun floor_double(p_76128_0_: Double): Int {
        val var2 = p_76128_0_.toInt()
        return if (p_76128_0_ < var2.toDouble()) var2 - 1 else var2
    }

    fun findItem(item: Item): Int {
        for (i in 0..8) {
            val itemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
            if (itemStack.item === item)
                return i
        }
        return -1
    }

    fun setSlot(slot: Int) {
        if (slot < 0 || slot > 8)
            return

        mc.thePlayer.inventory.currentItem = slot
    }

    fun getItemStack(): ItemStack? {
        return if (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null) null else mc.thePlayer.inventoryContainer.getSlot(
            getItemIndex() + 36
        ).stack
    }

    private fun getItemIndex(): Int {
        return mc.thePlayer.inventory.currentItem
    }

    fun hasItem(name: String): Boolean {
        for (i in mc.thePlayer.inventory.mainInventory.indices) {
            val stack = mc.thePlayer.inventory.mainInventory[i]
            if (stack != null && stack.item.unlocalizedName.contains(name)) {
                return true
            }
        }
        return false
    }

}