package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.AxisAlignedBB

data class EventBlockCollide(var collisionBoundingBox: AxisAlignedBB?, var block: Block?, var x: Double?, var y: Double?, var z: Double?) : Event() {
    override val cancellable: Boolean = true
}