package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.block.Block
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.AxisAlignedBB

data class EventMovementInput(var forward: Float, var strafe: Float, var jump: Boolean, var sneak: Boolean, var sneakSlowdown: Double) : Event() {
    override val cancellable: Boolean = true
}