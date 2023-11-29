package wtf.monsoon.client.ui.kpanel.element

import me.surge.animation.ColourAnimation
import me.surge.animation.Easing
import wtf.monsoon.Wrapper
import java.awt.Color

/**
 * @author surge
 * @since 21/02/2023
 */
abstract class Element(var x: Float, var y: Float, var width: Float, var height: Float) {

    protected val ui = Wrapper.monsoon.nvg
    val hover = ColourAnimation(Color(16, 16, 16), Color(16, 16, 16).brighter(), 100f, false, Easing.LINEAR)

    abstract fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float)

    open fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return false
    }

    open fun release(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return false
    }

    open fun key(code: Int, char: Char): Boolean {
        return false
    }

    open fun offset(): Float {
        return 0f
    }

    open fun trueHeight(): Float {
        return height + offset()
    }

    fun hovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

    fun position(x: Float, y: Float): Element {
        this.x = x
        this.y = y

        return this
    }

}
