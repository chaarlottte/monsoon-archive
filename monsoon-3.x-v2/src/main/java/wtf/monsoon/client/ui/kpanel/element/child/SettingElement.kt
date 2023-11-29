package wtf.monsoon.client.ui.kpanel.element.child

import me.surge.animation.Animation
import me.surge.animation.Easing
import wtf.monsoon.backend.setting.Bind
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.ui.kpanel.element.Element
import wtf.monsoon.client.util.ui.NVGWrapper
import java.awt.Color

/**
 * @author surge
 * @since 21/02/2023
 */
open class SettingElement<T>(val setting: Setting<T>, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val expanded = Animation(150f, false, Easing.CIRC_IN_OUT)
    private val children = arrayListOf<SettingElement<*>>()

    init {
        setting.children.forEach {
            when (it.getValue()) {
                is Boolean -> children.add(BooleanElement(it as Setting<Boolean>, x, y, width, height))
                is Enum<*> -> children.add(EnumElement(it as Setting<Enum<*>>, x, y, width, height))
                is Number -> children.add(NumberElement(it as Setting<Number>, x, y, width, height))
                is Bind -> children.add(BindElement(it as Setting<Bind>, x, y, width, height))
                is String -> children.add(SettingElement(it, x, y, width, height))
            }
        }
    }

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        if (expanded.getAnimationFactor() > 0) {
            ui.scissor(x, y + height, width, offset()) {
                var offset = 0f

                children.forEach { child ->
                    if(child.setting.visibility.invoke()) {
                        child.x = x
                        child.y = y + height + offset

                        child.draw(mouseX, mouseY, mouseDelta)

                        offset += child.trueHeight()
                    }
                }
            }
        }

        if(this.setting.getValue() is String && (this.setting.getValue() as Any) == "container") {
            hover.state = hovered(mouseX, mouseY)
            ui.rect(x, y, width, height, hover.getColour())
            ui.text(setting.name, x + 10, y + height / 2f + 1, "regular", 14f, Color.WHITE, NVGWrapper.Alignment.LEFT_MIDDLE)
            ui.text(if (expanded.state) "v" else "w", x + width - ((height - 16) / 2) - 4, y + height / 2f + 1, "entypo", 20f, Color.WHITE, NVGWrapper.Alignment.CENTER_MIDDLE)
        }
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state && (button == 1 || button == 0)) {
            this.expanded.state = !this.expanded.state
            return true
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
        return if (setting.visibility.invoke()) (children.sumOf { it.trueHeight().toDouble() } * expanded.getAnimationFactor()).toFloat() else 0.0f
    }

}