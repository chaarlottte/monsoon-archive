package wtf.monsoon.client.util.player

import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.MathHelper
import wtf.monsoon.client.modules.player.Scaffold
import wtf.monsoon.client.util.Util
import kotlin.math.atan2

object RotationUtil : Util() {

    fun getRotations(target: EntityLivingBase): FloatArray {
        val xDist = target.posX - mc.thePlayer.posX
        val zDist = target.posZ - mc.thePlayer.posZ
        val entityBB = target.entityBoundingBox.expand(0.10000000149011612, 0.10000000149011612, 0.10000000149011612)
        val playerEyePos = mc.thePlayer.posY + mc.thePlayer.eyeHeight
        val yDist =
            if (playerEyePos > entityBB.maxY) entityBB.maxY - playerEyePos else if (playerEyePos < entityBB.minY) entityBB.minY - playerEyePos else 0.0
        val fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist).toDouble()
        val yaw = (StrictMath.atan2(zDist, xDist) * 57.29577951308232).toFloat() - 90.0f
        var pitch = (-(StrictMath.atan2(yDist, fDist) * 57.29577951308232)).toFloat()
        pitch = pitch.coerceAtMost(90f)
        pitch = pitch.coerceAtLeast(-90f)
        return floatArrayOf(yaw, pitch)
    }

    fun getRotationsSmooth(target: EntityLivingBase, lastYaw: Float, lastPitch: Float): FloatArray {
        val xDist = target.posX - mc.thePlayer.posX
        val zDist = target.posZ - mc.thePlayer.posZ
        val entityBB = target.entityBoundingBox.expand(0.10000000149011612, 0.10000000149011612, 0.10000000149011612)
        val playerEyePos = mc.thePlayer.posY + mc.thePlayer.eyeHeight
        val yDist =
            if (playerEyePos > entityBB.maxY) entityBB.maxY - playerEyePos else if (playerEyePos < entityBB.minY) entityBB.minY - playerEyePos else 0.0
        val fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist).toDouble()
        val yaw = interpolateRotation(lastYaw, (StrictMath.atan2(zDist, xDist) * 57.29577951308232).toFloat() - 90.0f, 45f)
        var pitch = interpolateRotation(lastPitch, (-(StrictMath.atan2(yDist, fDist) * 57.29577951308232)).toFloat(), 45f)
        pitch = pitch.coerceAtMost(90f)
        pitch = pitch.coerceAtLeast(-90f)
        return floatArrayOf(yaw, pitch)
    }

    fun interpolateRotation(prev: Float, now: Float, maxTurn: Float): Float {
        var var4 = MathHelper.wrapAngleTo180_float(now - prev)
        if (var4 > maxTurn) {
            var4 = maxTurn
        }
        if (var4 < -maxTurn) {
            var4 = -maxTurn
        }
        return prev + var4
    }

    fun getRenderYawOffset(yaw: Float): Float {
        return interpolateRotation(mc.thePlayer.rotationYawHead, yaw, 120f)
    }

    fun getScaffoldRotations(info: Scaffold.BlockInfo): FloatArray {
        val xDiff = info.pos.x - mc.thePlayer.posX
        val yDiff = info.pos.y - mc.thePlayer.posY - 1.7
        val zDiff = info.pos.z - mc.thePlayer.posZ
        val dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff).toDouble()
        val yaw = (atan2(zDiff, xDiff) * 180.0 / Math.PI).toFloat() - 90.0f
        val pitch = -(atan2(yDiff, dist) * 180.0 / Math.PI).toFloat()
        return floatArrayOf(yaw, pitch)
    }

}