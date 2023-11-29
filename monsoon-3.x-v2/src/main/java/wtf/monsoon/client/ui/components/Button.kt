package wtf.monsoon.client.ui.components

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import wtf.monsoon.Wrapper
import java.awt.Color

/**
 * @author surge
 * @since 02/04/2023
 */
class Button(id: Int, x: Int, y: Int, width: Int, height: Int, text: String, val click: () -> Unit) : GuiButton(id, x, y, width, height, text) {

    override fun drawButton(mc: Minecraft?, mouseX: Int, mouseY: Int) {
        if (this.visible) {
            with (Wrapper.monsoon.nvg) {
                round(this@Button.xPosition.toFloat(), this@Button.yPosition.toFloat(), this@Button.width.toFloat(), this@Button.height.toFloat(), 5f, if (isHovered()) Color(20, 20, 20) else Color(10, 10, 10))
                text(this@Button.displayString, (this@Button.xPosition.toFloat() + this@Button.width.toFloat() / 2f) - (textWidth(this@Button.displayString, "regular", 18f) / 2f), (this@Button.yPosition.toFloat() + this@Button.height / 2f) - (this.textHeight("regular", 18f) / 2f) + 1, "regular", 18f, Color.WHITE)
            }
        }
    }

    fun isHovered(): Boolean {
        return Mouse.getX() >= xPosition && (Display.getHeight() - Mouse.getY()) >= yPosition && Mouse.getX() <= xPosition + width && (Display.getHeight() - Mouse.getY()) <= yPosition + height
    }

}