package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

class EventGetMinimumMotion(var minimumMotion: Double) : Event() {
    override val cancellable: Boolean = true
}