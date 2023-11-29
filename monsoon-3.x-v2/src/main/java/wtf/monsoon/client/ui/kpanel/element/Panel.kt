package wtf.monsoon.client.ui.kpanel.element

import me.surge.animation.Animation
import me.surge.animation.Easing
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.client.util.ui.NVGWrapper
import java.awt.Color

/**
 * @author surge
 * @since 21/02/2023
 */
class Panel(val category: Category, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val expanded = Animation(200f, true, Easing.CIRC_IN_OUT)
    private val children = arrayListOf<ModuleElement>()

    var dragging = false

    var velocity: Float = 0f

    private var lastX = 0f
    private var lastY = 0f

    private var lastRenderX = x
    private var lastRenderY = y

    init {
        Wrapper.monsoon.moduleManager.getModuleByCategory(category).forEach { module ->
            children.add(ModuleElement(module, x, y, width - 4, height))
        }
    }

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        if (dragging) {
            this.x = mouseX - lastX
            this.y = mouseY - lastY

            velocity = (mouseX - lastX - lastRenderX) / 360
        } else {
            velocity = 0f
        }

        ui.endFrame()
        ui.beginFrame()

        ui.save()

        ui.translate(mouseX.toFloat(), mouseY.toFloat())
        ui.rotate(velocity)
        ui.translate(-mouseX.toFloat(), -mouseY.toFloat())

        ui.rect(x, y, width, trueHeight(), Color(16, 16, 16))
        ui.rect(x, y, width, height, Color(10, 10, 10))
        ui.text(category.iconCode.toString(), x + 8, y + height / 2f, "category2", 20f, Color.WHITE, NVGWrapper.Alignment.LEFT_MIDDLE)
        ui.roundedLinearGradient(x, y, width, 2f, 0f, Color.BLUE, Color(161, 0, 255))
        ui.text(category.name, x + width / 2f, y + height / 2f + 1, "sbold", 16f, Color.WHITE, NVGWrapper.Alignment.CENTER_MIDDLE)

        ui.rect(x, y + height, width, (offset() * expanded.getAnimationFactor()).toFloat(), Color(16, 16, 16))

        if (expanded.getAnimationFactor() > 0) {
            ui.scissor(x, y + height, width, (offset() * expanded.getAnimationFactor()).toFloat()) {
                var offset = 0f

                children.forEach { child ->
                    child.x = x + 2
                    child.y = y + height + offset

                    child.draw(mouseX, mouseY, mouseDelta)

                    offset += child.trueHeight()
                }
            }
        }

        // ui.roundedLinearGradient(x, (y + height + offset() * expanded.getAnimationFactor()).toFloat(), width, 2f, 0f, Color.BLUE, Color(161, 0, 255))

        ui.restore()

        lastRenderX = x
        lastRenderY = y
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY)) {
            when (button) {
                0 -> {
                    lastX = mouseX - x
                    lastY = mouseY - y

                    dragging = true
                }

                1 -> {
                    expanded.state = !expanded.state
                }
            }
        }

        if (expanded.state) {
            children.forEach { child ->
                if (child.click(mouseX, mouseY, button)) {
                    return true
                }
            }
        }

        return super.click(mouseX, mouseY, button)
    }

    override fun release(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (button == 0) {
            this.dragging = false
        }

        if (expanded.state) {
            children.forEach { child ->
                if (child.release(mouseX, mouseY, button)) {
                    return true
                }
            }
        }

        return super.release(mouseX, mouseY, button)
    }

    override fun key(code: Int, char: Char): Boolean {
        if (expanded.state) {
            children.forEach { child ->
                if (child.key(code, char)) {
                    return true
                }
            }
        }

        return super.key(code, char)
    }

    override fun offset(): Float {
        return (children.sumOf { it.trueHeight().toDouble() } * expanded.getAnimationFactor()).toFloat()
    }

}