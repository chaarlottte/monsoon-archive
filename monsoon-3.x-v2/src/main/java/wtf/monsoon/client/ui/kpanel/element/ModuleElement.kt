package wtf.monsoon.client.ui.kpanel.element

import me.surge.animation.Animation
import me.surge.animation.Easing
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Bind
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.ui.kpanel.element.child.*
import wtf.monsoon.client.util.ui.ColorUtil
import wtf.monsoon.client.util.ui.NVGWrapper
import java.awt.Color

/**
 * @author surge
 * @since 21/02/2023
 */
class ModuleElement(val module: Module, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val expanded = Animation(150f, false, Easing.CIRC_IN_OUT)

    private val children = arrayListOf<SettingElement<*>>()

    init {
        module.settings.forEach {
            when (it.getValue()) {
                is Boolean -> children.add(BooleanElement(it as Setting<Boolean>, x, y, width, height))
                is Enum<*> -> children.add(EnumElement(it as Setting<Enum<*>>, x, y, width, height))
                is ModuleMode<*> -> children.add(ClassModeElement(it as Setting<ModuleMode<*>>, x, y, width, height))
                is Number -> children.add(NumberElement(it as Setting<Number>, x, y, width, height))
                is Bind -> children.add(BindElement(it as Setting<Bind>, x, y, width, height))
                is String -> children.add(SettingElement(it as Setting<String>, x, y, width, height))
            }
        }
    }

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hover.state = hovered(mouseX, mouseY)

        val text = ColorUtil.interpolate(Color(0x9A9A9A), Color.WHITE, module.animation.getAnimationFactor())
        val rect = arrayOf(
            ColorUtil.interpolate(Color(21, 21, 21), Color.BLUE, module.animation.getAnimationFactor()),
            ColorUtil.interpolate(Color(21, 21, 21), Color(161, 0, 255), module.animation.getAnimationFactor())
        )

        ui.rect(x, y, width, height, hover.getColour())
        ui.roundedLinearGradient(x + width - 4, y + 4, 2f, height - 8, 2f, rect[0], rect[1])
        ui.text(module.name, x + width / 2f, y + height / 2f - (if (module.script != null) 3f else -1f), "regular", 15f, text, NVGWrapper.Alignment.CENTER_MIDDLE)

        if (module.script != null) {
            ui.text(module.script!!.name, x + width / 2f, y + height - 8f, "regular", 10f, text.darker().darker(), NVGWrapper.Alignment.CENTER_MIDDLE)
        }

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
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state) {
            when (button) {
                0 -> {
                    module.toggle()
                    return true
                }

                 1 -> {
                     expanded.state = !expanded.state
                     return true
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
        var amount = 0f

        this.children.forEach {
            if (it.setting.visibility()) {
                amount += it.trueHeight()
            }
        }

        return (amount * expanded.getAnimationFactor()).toFloat()
    }

}