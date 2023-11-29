package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

data class EventStrafing(var yaw: Float, var pitch: Float) : Event() {
    override val cancellable: Boolean = true
}