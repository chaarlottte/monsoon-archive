package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

class EventMouseOver(var reach: Double, var expand: Float) : Event() {
    override val cancellable: Boolean = true
}