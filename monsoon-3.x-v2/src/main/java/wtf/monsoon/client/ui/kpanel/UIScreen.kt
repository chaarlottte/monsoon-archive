package wtf.monsoon.client.ui.kpanel

import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.client.modules.client.ClickGUI
import wtf.monsoon.client.ui.NVGScreen
import wtf.monsoon.client.ui.ParticleEngine
import wtf.monsoon.client.ui.components.Button
import wtf.monsoon.client.ui.kpanel.element.Panel
import wtf.monsoon.client.ui.scripting.ScriptingScreen

/**
 * @author surge
 * @since 21/02/2023
 */
class UIScreen : NVGScreen() {

    private val panels = arrayListOf<Panel>()
    private lateinit var particleEngine: ParticleEngine

    override fun initGui() {
        particleEngine = ParticleEngine(5f)

        var x = 10f

        Category.values().forEach { category ->
            var canAdd = true

            panels.forEach { panel ->
                if (panel.category == category) {
                    canAdd = false
                }
            }

            if (canAdd) {
                panels.add(Panel(category, x, 10f, 200f, 34f))
            }

            x += 210
        }

        this.buttonList.add(Button(0, 5, Display.getHeight() - 30, 150, 25, "Scripting") {
            mc.displayGuiScreen(ScriptingScreen())
        })
    }

    override fun draw(mouseX: Int, mouseY: Int) {
        drawDefaultBackground()

        if (Wrapper.monsoon.moduleManager.getModule(ClickGUI::class.java).particles.getValue()) {
            particleEngine.maxVelocity = Wrapper.monsoon.moduleManager.getModule(ClickGUI::class.java).particleSpeed.getValue()
            particleEngine.render(Display.getWidth().toFloat(), Display.getHeight().toFloat(), mouseX, mouseY)
        }

        panels.forEach { it.draw(mouseX, mouseY, Mouse.getDWheel().toFloat()) }

        super.draw(mouseX, mouseY)
    }

    override fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {
        panels.forEach { panel ->
            if (panel.click(mouseX, mouseY, mouseButton)) {
                return@forEach
            }
        }

        buttonList.filterIsInstance<Button>().firstOrNull { it.isHovered() }?.click?.let { it() }

        super.click(mouseX, mouseY, mouseButton)
    }

    override fun release(mouseX: Int, mouseY: Int, state: Int) {
        panels.forEach { panel ->
            if (panel.release(mouseX, mouseY, state)) {
                return@forEach
            }
        }

        super.release(mouseX, mouseY, state)
    }

    override fun key(code: Int, char: Char) {
        panels.forEach { panel ->
            if (panel.key(code, char)) {
                return@forEach
            }
        }

        super.key(code, char)
    }

    override fun doesGuiPauseGame(): Boolean {
        return Wrapper.monsoon.moduleManager.getModule(ClickGUI::class.java).pause.getValue()
    }

}