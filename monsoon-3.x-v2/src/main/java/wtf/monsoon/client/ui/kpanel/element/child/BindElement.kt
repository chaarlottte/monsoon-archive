package wtf.monsoon.client.ui.kpanel.element.child

import me.surge.animation.Animation
import me.surge.animation.Easing
import wtf.monsoon.backend.setting.Bind
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.util.ui.NVGWrapper.Alignment
import java.awt.Color

/**
 * @author surge
 * @since 21/02/2023
 */
class BindElement(setting: Setting<Bind>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Bind>(setting, x, y, width, height) {

    private val listening = Animation(100f, false, Easing.LINEAR)

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hover.state = hovered(mouseX, mouseY)

        ui.rect(x, y, width, height, hover.getColour())

        val buttonName = if (listening.state) "..." else this.setting.getValue().getButtonName()

        val bindWidth = ui.textWidth(buttonName, "regular", 14f)

        ui.round(x + width - bindWidth - 12, y + height / 2f - ((height - 16) / 2f), bindWidth + 8f, height - 16, 7f, Color(38, 38, 38))
        ui.round(x + width - bindWidth - 11, y + height / 2f - ((height - 16) / 2f) + 1, bindWidth + 6f, height - 18, 6f, Color(30, 30, 30))

        ui.text(buttonName, x + width - bindWidth - 8, y + height / 2f + 1, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)

        ui.text(setting.name, x + 10, y + height / 2f + 1, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)

        super.draw(mouseX, mouseY, mouseDelta)
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (listening.state) {
            this.setting.setValue(Bind(button, Bind.Device.MOUSE))
            this.listening.state = false
            return true
        }

        if (hover.state && button == 0) {
            this.listening.state = !this.listening.state
            return true
        }

        return super.click(mouseX, mouseY, button)
    }

    override fun key(code: Int, char: Char): Boolean {
        if (listening.state) {
            this.setting.setValue(Bind(code, Bind.Device.KEYBOARD))
            this.listening.state = false
            return true
        }

        return super.key(code, char)
    }

}