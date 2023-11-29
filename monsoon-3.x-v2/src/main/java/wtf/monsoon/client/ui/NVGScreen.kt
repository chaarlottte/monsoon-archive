package wtf.monsoon.client.ui

import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.GL11.*
import wtf.monsoon.Wrapper

/**
 * @author surge
 * @since 21/02/2023
 */
open class NVGScreen : GuiScreen() {

    final override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)

        glPushAttrib(0)
        glPushMatrix()
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        Wrapper.monsoon.nvg.beginFrame()

        draw(mouseX * mc.gameSettings.guiScale, mouseY * mc.gameSettings.guiScale)

        Wrapper.monsoon.nvg.endFrame()

        glDisable(GL_BLEND)
        glPopMatrix()
        glPopAttrib()

        glEnable(GL_DEPTH_TEST)
    }

    final override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        click(mouseX * mc.gameSettings.guiScale, mouseY * mc.gameSettings.guiScale, mouseButton)
    }

    final override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)

        release(mouseX * mc.gameSettings.guiScale, mouseY * mc.gameSettings.guiScale, state)
    }

    final override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)

        key(keyCode, typedChar)
    }

    final override fun onGuiClosed() {
        super.onGuiClosed()

        close()
    }

    open fun draw(mouseX: Int, mouseY: Int) {}
    open fun click(mouseX: Int, mouseY: Int, mouseButton: Int) {}
    open fun release(mouseX: Int, mouseY: Int, mouseButton: Int) {}
    open fun key(code: Int, char: Char) {}
    open fun close() {}

}