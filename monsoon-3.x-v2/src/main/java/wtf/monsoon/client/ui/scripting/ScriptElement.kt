package wtf.monsoon.client.ui.scripting

import wtf.monsoon.backend.manager.script.ScriptManager
import wtf.monsoon.client.ui.kpanel.element.Element
import java.awt.Color

/**
 * @author surge
 * @since 02/04/2023
 */
class ScriptElement(val panel: ScriptingPanel, val script: ScriptManager.Script, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        ui.rect(x, y, width, height, if (hovered(mouseX, mouseY)) Color(30, 30, 30) else Color(20, 20, 20))
        ui.text(script.name, x + 5, y + 6, "regular", 14f, Color(177, 182, 166))
        ui.text("Made by ${script.author}", x + 5, y + 30, "regular", 12f, Color(54, 57, 70))
    }

}