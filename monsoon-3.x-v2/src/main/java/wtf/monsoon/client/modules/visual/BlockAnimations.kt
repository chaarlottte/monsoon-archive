package wtf.monsoon.client.modules.visual
import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.MathHelper
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.DefaultKey
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.event.EventRender2D
import wtf.monsoon.client.event.EventRenderBlocking
import wtf.monsoon.client.util.misc.StringUtil

class BlockAnimations : Module("Block Animations", "Customize the client's blocking animations.", Category.VISUAL) {

    private val blockAnimation: Setting<Animation> = Setting("Animation", "The block animation", Animation.OLD)

    override var metadata = { StringUtil.formatEnum(this.blockAnimation.getValue()) }

    @EventListener
    val renderBlocking = fun(it: EventRenderBlocking) {
        val f = it.equipProgress
        val f1 = it.swingProgress
        val var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * Math.PI.toFloat())

        it.cancel()
        when(blockAnimation.getValue()) {
            Animation.OLD -> {
                it.transformFirstPersonItem(f, f1)
                it.doBlockTransformations()
            }
            Animation.CHILL -> {
                GlStateManager.translate(0.055f, 0.12f, 0.0f)
                it.transformFirstPersonItem(f, f1)
                it.doBlockTransformations()
            }
            Animation.EXHI -> {
                this.exhi(f, f1)
                it.doBlockTransformations()
            }
            Animation.EXHIBOBO -> {
                this.exhibobo(f, f1)
                it.doBlockTransformations()
            }
            Animation.EXHI_TAP -> {
                // GL11.glTranslated(-0.06D, 0.1D, 0.0D);
                GL11.glTranslated(-0.06, 0.17, -0.0)
                it.transformFirstPersonItem(f / 2.5f, 0.0f)
                GlStateManager.rotate(-var9 * 40.0f / 2.0f, var9 / 2.0f, 1.0f, 4.0f)
                GlStateManager.rotate(-var9 * 30.0f, 1.0f, var9 / 3.0f, -0.0f)
                it.doBlockTransformations()
            }
            Animation.SLIDE -> {
                GlStateManager.translate(-0.02, 0.05, 0.0)
                it.transformFirstPersonItem(f, 0.0f)
                it.doBlockTransformations()
                GlStateManager.translate(-0.05f, 0.2f, 0.2f)
                GlStateManager.rotate(-var9 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f)
                GlStateManager.rotate(-var9 * 70.0f, 1.0f, -0.4f, -0.0f)
            }
            Animation.ASTOLFO -> {
                GlStateManager.translate(0.05f, 0f, -0.35f)
                it.transformFirstPersonItem(f, f1)
                it.doBlockTransformations()
            }
            Animation.SWING -> {
                it.transformFirstPersonItem(f, f1)
                it.doBlockTransformations()
                GlStateManager.translate(-0.3f, 0.2f, 0.2f)
            }
            Animation.OH_THE_MISERY -> {
                GlStateManager.translate(0f, 0.125f, 0f)
                it.transformFirstPersonItem(f, f1)
                val var15 = MathHelper.sin((MathHelper.sqrt_float(f1).toDouble() * 3.141592653589793).toFloat())
                GlStateManager.rotate(var15 * 30.0f / 2.0f, -var15, -0.0f, 9.0f)
                GlStateManager.rotate(var15 * 40.0f, 1.0f, -var15 / 2.0f, -0.0f)
                it.doBlockTransformations()
            }
        }
    }


    private fun exhi(equipProgress: Float, swingProgress: Float) {
        GlStateManager.translate(0.56, -0.52, -0.71999997)
        GlStateManager.translate(0.0, 0.07, 0.0)
        val funny1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f)
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f)
        GlStateManager.translate(0.0f - funny1 / 100.0f, 0.0f + funny1 / 15.0f, 0.0f + funny1 / 15.0f)
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f)
        val f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f)
        val f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f)
        GlStateManager.rotate(f1 * -40.0f, 1.0f, 0.4f, 0.9f)
        GlStateManager.rotate(f1 * -20.0f, 1.0f, 0.0f, 0.0f)
        // val event = EventScaleItem(0.4f, 0.4f, 0.4f)
        // Wrapper.getEventBus().post(event)
        // GlStateManager.scale(event.getScaleX(), event.getScaleY(), event.getScaleZ())
        GlStateManager.scale(0.4f, 0.4f, 0.4f)
    }

    private fun exhibobo(equipProgress: Float, swingProgress: Float) {
        GlStateManager.translate(0.56, -0.52, -0.71999997)
        GlStateManager.translate(0.0, 0.07, 0.0)
        val funny2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * Math.PI.toFloat())
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f + funny2 / 10)
        GlStateManager.translate(0f, 0f + funny2 / 12, -0f)
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f)
        //GlStateManager.rotate(-10.0F, 0.0F, 1.0F, 0.0F);
        val var3 = MathHelper.sin(swingProgress * swingProgress * Math.PI.toFloat())
        val var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * Math.PI.toFloat())
        GlStateManager.rotate(var4 * -20.0f, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(var4 * -20.0f, 1.0f, 0.2f, 1.0f)
        GlStateManager.rotate(var4 * -15.0f, 1f, 0.4f, 0.9f)
        // val event2 = EventScaleItem(0.4f, 0.4f, 0.4f)
        // Wrapper.getEventBus().post(event2)
        // GlStateManager.scale(event2.getScaleX(), event2.getScaleY(), event2.getScaleZ())
        GlStateManager.scale(0.4f, 0.4f, 0.4f)
    }

    enum class Animation {
        OLD, ASTOLFO, CHILL, EXHI, EXHI_TAP, EXHIBOBO, SLIDE, SWING, OH_THE_MISERY
    }
}