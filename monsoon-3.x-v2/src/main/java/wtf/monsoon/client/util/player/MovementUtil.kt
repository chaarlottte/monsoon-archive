package wtf.monsoon.client.util.player

import net.minecraft.util.MathHelper
import wtf.monsoon.client.util.Util
import java.util.*
import kotlin.math.*


object MovementUtil : Util() {

    val direction: Float
        get() {
            var yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw)
            val moveForward: Double = mc.thePlayer.moveForward.toDouble()
            val moveStrafing: Double = mc.thePlayer.moveStrafing.toDouble()
            if (moveForward < 0) {
                yaw += 180f
            }
            if (moveStrafing > 0) {
                yaw += (if (moveForward == 0.0) -90 else if (moveForward > 0) -45 else 45).toFloat()
            }
            if (moveStrafing < 0) {
                yaw += (if (moveForward == 0.0) 90 else if (moveForward > 0) 45 else -45).toFloat()
            }
            return yaw
        }

    val isGoingDiagonally: Boolean
        get() = abs(mc.thePlayer.motionX) > 0.08 && abs(mc.thePlayer.motionZ) > 0.08
}