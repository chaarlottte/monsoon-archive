package wtf.monsoon.backend.manager.script.link

import org.lwjgl.opengl.Display
import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier
import wtf.monsoon.Wrapper
import java.awt.Color
import kotlin.math.ceil

/**
 * @author surge
 * @since 27/03/2023
 */
class DrawLink : Link() {

    @Identifier("rect")
    fun rect(x: Float, y: Float, width: Float, height: Float, colour: Colour) {
        Wrapper.monsoon.nvg.rect(x, y, width, height, colour.getJColour())
    }

    @Identifier("rounded_rect")
    fun roundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float, colour: Colour) {
        Wrapper.monsoon.nvg.round(x, y, width, height, radius, colour.getJColour())
    }

    @Identifier("draw_string")
    fun drawString(text: String, x: Float, y: Float, size: Float, colour: Colour) {
        Wrapper.monsoon.nvg.text(text, x, y, "regular", size, colour.getJColour())
    }

    @Identifier("draw_string_shadowed")
    fun drawStringShadowed(text: String, x: Float, y: Float, size: Float, colour: Colour) {
        Wrapper.monsoon.nvg.text(text, x + 1, y + 1, "regular", size, Colour(0, 0, 0, 100).setAlpha(100 * (colour.a / 255)).getJColour())
        Wrapper.monsoon.nvg.text(text, x, y, "regular", size, colour.getJColour())
    }

    @Identifier("draw_mc_string_shadowed")
    fun drawStringShadowedMC(text: String, x: Float, y: Float, colour: Colour) {
        mc.fontRendererObj.drawStringWithShadow(text, x, y, colour.getJColour().rgb)
    }

    @Identifier("get_string_width")
    fun getStringWidth(text: String, size: Float): Float {
        return Wrapper.monsoon.nvg.textWidth(text, "regular", size)
    }

    @Identifier("get_mc_string_width")
    fun getStringWidthMC(text: String) = mc.fontRendererObj.getStringWidth(text)

    @Identifier("get_window_width")
    fun getWindowWidth(): Float {
        return Display.getWidth().toFloat()
    }

    @Identifier("get_window_height")
    fun getWindowHeight(): Float {
        return Display.getHeight().toFloat()
    }

    @Identifier("get_rainbow")
    fun getRainbow(index: Int, multiplier: Int): Colour {
        var rainbowState = ceil((System.currentTimeMillis() + (index * multiplier)) / 20.0)
        rainbowState %= 360.0
        val color = Color.getHSBColor((rainbowState / 360.0f).toFloat(), 0.5f, 1.0f)

        return Colour(color.red, color.green, color.blue, color.alpha)
    }

    class Colour(val r: Int, val g: Int, val b: Int, var a: Int) {

        constructor(r: Int, g: Int, b: Int) : this(r, g, b, 255)

        fun setAlpha(a: Int): Colour {
            this.a = a
            return this
        }

        @Excluded
        fun getJColour(): Color {
            return Color(r, g, b, a)
        }

    }

}