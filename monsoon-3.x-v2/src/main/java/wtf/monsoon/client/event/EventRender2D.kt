package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.client.gui.ScaledResolution
import spritz.api.annotations.Identifier
import wtf.monsoon.Wrapper
import wtf.monsoon.client.util.ui.NVGWrapper

data class EventRender2D(@Identifier("scaled_resolution") val scaledResolution: ScaledResolution, val displayWidth: Int, val displayHeight: Int) : Event() {

    override val cancellable: Boolean = true
    val nvg: NVGWrapper = Wrapper.monsoon.nvg

}