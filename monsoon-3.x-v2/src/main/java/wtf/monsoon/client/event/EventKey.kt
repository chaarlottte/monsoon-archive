package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

data class EventKey(val key: Int) : Event() {
    override val cancellable: Boolean = true
}