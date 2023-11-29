package wtf.monsoon.client.ui.kpanel.element.child

import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.util.ui.NVGWrapper.Alignment
import java.awt.Color
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

/**
 * @author surge
 * @since 21/02/2023
 */
class NumberElement(setting: Setting<Number>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Number>(setting, x, y, width, height) {

    private var dragging = false

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hover.state = hovered(mouseX, mouseY)

        ui.rect(x, y, width, height, hover.getColour())

        val difference = width.coerceAtMost(0f.coerceAtLeast(mouseX - (x + 4))).toDouble()

        val minimum = setting.minimum!!.toDouble()
        val maximum = setting.maximum!!.toDouble()

        val renderWidth = ((width - 8) * (setting.getValue().toDouble() - minimum) / (maximum - minimum)).toFloat()

        val value: Double

        if (dragging) {
            value = if (difference == 0.0) {
                minimum
            } else {
                val newValue = BigDecimal(difference / (width - 8) * (maximum - minimum) + minimum).setScale(2, RoundingMode.HALF_DOWN).toDouble()
                val precision = 1 / setting.incrementation!!.toDouble()
                (minimum.coerceAtLeast(maximum.coerceAtMost(newValue)) * precision).roundToInt() / precision
            }

            when (setting.getValue()) {
                is Int -> setting.setValue(value.toInt())
                is Float -> setting.setValue(value.toFloat())
                is Double -> setting.setValue(value)
                is Long -> setting.setValue(value)
            }
        }

        ui.roundedLinearGradient(x + 4, y + height - 10, width - 8, 4f, 2f, Color(0, 0, 255, 50), Color(161, 0, 255, 50))

        ui.scissor(x + 4, y + height - 10, renderWidth, 4f) {
            ui.roundedLinearGradient(x + 4, y + height - 10, width - 8, 4f, 2f, Color.BLUE, Color(161, 0, 255))
        }

        ui.text(setting.name, x + 10, y + height / 2f - 4, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)
        ui.text(setting.getValue().toString(), x + width - ui.textWidth(setting.getValue().toString(), "regular", 14f) - 5, y + height / 2f - 4, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)

        super.draw(mouseX, mouseY, mouseDelta)
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state && button == 0) {
            this.dragging = true
            return true
        }

        return super.click(mouseX, mouseY, button)
    }

    override fun release(mouseX: Int, mouseY: Int, button: Int): Boolean {
        dragging = false

        return super.release(mouseX, mouseY, button)
    }

}