package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

data class EventMove(var x: Double, var y: Double, var z: Double) : Event() {
    override val cancellable: Boolean = true
}