package wtf.monsoon.client.ui.scripting

import org.lwjgl.input.Mouse
import wtf.monsoon.client.ui.NVGScreen

/**
 * @author surge
 * @since 02/04/2023
 */
class ScriptingScreen : NVGScreen() {

    var panel = ScriptingPanel(width - 550f, height - 400f, 1100f, 800f)

    override fun initGui() {
        panel = ScriptingPanel(width - 550f, height - 400f, 1100f, 800f)
    }

    override fun draw(mouseX: Int, mouseY: Int) {
        drawDefaultBackground()
        panel.draw(mouseX, mouseY, Mouse.getDWheel().toFloat())

        super.draw(mouseX, mouseY)
    }

    override fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {
        panel.click(mouseX, mouseY, mouseButton)

        super.click(mouseX, mouseY, mouseButton)
    }

    override fun release(mouseX: Int, mouseY: Int, mouseButton: Int) {
        panel.release(mouseX, mouseY, mouseButton)

        super.release(mouseX, mouseY, mouseButton)
    }

    override fun key(code: Int, char: Char) {
        panel.key(code, char)

        super.key(code, char)
    }

    override fun doesGuiPauseGame() = false

}