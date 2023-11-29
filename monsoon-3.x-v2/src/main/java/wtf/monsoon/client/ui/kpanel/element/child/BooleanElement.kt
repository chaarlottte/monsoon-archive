package wtf.monsoon.client.ui.kpanel.element.child

import me.surge.animation.Animation
import me.surge.animation.Easing
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.util.ui.NVGWrapper.Alignment
import java.awt.Color

/**
 * @author surge
 * @since 21/02/2023
 */
class BooleanElement(setting: Setting<Boolean>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Boolean>(setting, x, y, width, height) {

    private val enabled = Animation(100f, false, Easing.LINEAR)

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hover.state = hovered(mouseX, mouseY)
        enabled.state = setting.getValue()

        val dimension = height - 16

        ui.rect(x, y, width, height, hover.getColour())

        ui.round(x + width - dimension - 4, y + height / 2f - (dimension / 2f), dimension, dimension, 7f, Color(38, 38, 38))
        ui.round(x + width - dimension - 3, y + height / 2f - (dimension / 2f) + 1, dimension - 2, dimension - 2, 6f, Color(30, 30, 30))

        ui.text("k", x + width - (dimension / 2) - 4, y + height / 2f + 1, "entypo", (14f * enabled.getAnimationFactor()).toFloat(), Color.WHITE, Alignment.CENTER_MIDDLE)

        ui.text(setting.name, x + 10, y + height / 2f + 1, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)

        super.draw(mouseX, mouseY, mouseDelta)
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state && button == 0) {
            this.setting.setValue(!this.setting.getValue())
            return true
        }

        return super.click(mouseX, mouseY, button)
    }

}