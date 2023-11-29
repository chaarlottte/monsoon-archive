package wtf.monsoon.client.ui.kpanel.element.child

import wtf.monsoon.backend.format
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.util.ui.NVGWrapper.Alignment
import java.awt.Color

/**
 * Shoroa - don't make this like the last enum elements (dropdown), because it will not be immediately intuitive for the users
 * when I add child settings
 * @author surge
 * @since 21/02/2023
 */
class EnumElement(setting: Setting<Enum<*>>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Enum<*>>(setting, x, y, width, height) {

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hover.state = hovered(mouseX, mouseY)

        ui.rect(x, y, width, height, hover.getColour())

        ui.text(setting.name, x + 10, y + height / 2f + 1, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)
        ui.text(setting.getValue().name.format(), x + width - ui.textWidth(setting.getValue().name.format(), "regular", 14f) - 5, y + height / 2f + 1, "regular", 14f, Color.WHITE, Alignment.LEFT_MIDDLE)

        super.draw(mouseX, mouseY, mouseDelta)
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state && button == 0) {
            this.setting.setValue(this.setting.nextMode)
            return true
        }

        return super.click(mouseX, mouseY, button)
    }

}