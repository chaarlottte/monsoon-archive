package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.util.MovingObjectPosition.MovingObjectType

data class EventRightClick(val type: MovingObjectType) : Event() {
    override val cancellable: Boolean = true
}