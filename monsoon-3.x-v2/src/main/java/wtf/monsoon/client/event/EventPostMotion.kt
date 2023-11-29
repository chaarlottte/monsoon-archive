package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

data class EventPostMotion(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float, val onGround: Boolean) : Event() {
    override val cancellable: Boolean = true
}