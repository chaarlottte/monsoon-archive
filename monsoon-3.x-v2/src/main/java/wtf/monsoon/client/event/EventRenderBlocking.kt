package wtf.monsoon.client.event

import me.bush.eventbuskotlin.Event
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper

data class EventRenderBlocking(var equipProgress: Float, var swingProgress: Float) : Event() {
    override val cancellable: Boolean = true

    fun transformFirstPersonItem(equipProgress: Float = this.equipProgress, swingProgress: Float = this.swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f)
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f)
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f)
        val f = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * Math.PI.toFloat())
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(f1 * -20.0f, 0.0f, 0.0f, 1.0f)
        GlStateManager.rotate(f1 * -80.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.scale(0.4f, 0.4f, 0.4f)
    }

    fun doBlockTransformations() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f)
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f)
    }
}