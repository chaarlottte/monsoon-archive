package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event

class EventChatMessage(val message:String) : Event() {
    override val cancellable: Boolean = true
}