package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.client.gui.ScaledResolution

data class EventPreMotion(var x: Double, var y: Double, var z: Double, var yaw: Float, var pitch: Float, var onGround: Boolean) : Event() {
    override val cancellable: Boolean = true
}