package wtf.monsoon.client.ui.scripting

import net.minecraft.util.MathHelper
import org.lwjgl.input.Mouse
import wtf.monsoon.Wrapper
import wtf.monsoon.client.ui.components.Button
import wtf.monsoon.client.ui.kpanel.element.Element
import java.awt.Color
import java.awt.Rectangle
import java.lang.Float.max

/**
 * @author surge
 * @since 02/04/2023
 */
class ScriptingPanel(x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val elements = mutableListOf<ScriptElement>()
    private var scriptScroll = 0f
    private var logScroll = 0f

    private var selected: ScriptElement? = null

    private val scriptBounds = Rectangle((x + 10f).toInt(), (y + 70f).toInt(), 150, (height - 110f).toInt())
    private var consoleBounds = Rectangle((x + width - 930f).toInt(), (y + height - 205f).toInt(), 925, 200)

    private val reload = Button(0, (x + 30).toInt(), (y + height - 35).toInt(), 100, 25, "Reload All") {
        selected = null
        elements.clear()
        Wrapper.monsoon.scriptManager.initialise()
    }

    override fun draw(mouseX: Int, mouseY: Int, mouseDelta: Float) {
        Wrapper.monsoon.scriptManager.scripts.forEach { (_, script) ->
            if (!elements.any { it.script == script }) {
                elements.add(ScriptElement(this, script, 0f, 0f, 150f, 50f))
            }
        }

        consoleBounds = if (selected == null) {
            Rectangle((x + width - 930f).toInt(), (y + 70).toInt(), 925, height.toInt() - 70)
        } else {
            Rectangle((x + width - 930f).toInt(), (y + height - 200f).toInt(), 925, 200)
        }

        elements.removeIf { !Wrapper.monsoon.scriptManager.scripts.containsValue(it.script) }

        if (scriptBounds.contains(mouseX, mouseY)) {
            scriptScroll += mouseDelta * 0.05f
        }

        if (consoleBounds.contains(mouseX, mouseY)) {
            logScroll += mouseDelta * 0.05f
        }

        var logHeight = 0f

        Wrapper.monsoon.scriptManager.compilations.forEach { (scriptName, log) ->
            logHeight += ui.textHeight("regular", 15f) + 2f

            val logSplit = log.split('\n')

            for (arrow in logSplit.indices) {
                if (logSplit[arrow].trim().isNotEmpty()) {
                    logHeight += (ui.textHeight("regular", 12f) + 2) * log.split("\n").size
                }
            }
        }

        scriptScroll = MathHelper.clamp_float(scriptScroll, -max(0f, elements.sumOf { it.trueHeight() + 2.0 }.toFloat() - scriptBounds.height), 0f)
        logScroll = MathHelper.clamp_float(logScroll, -max(0f, (logHeight + 10) - MathHelper.clamp_float(logHeight, 0f, consoleBounds.height.toFloat())), 0f)

        ui.rect(x, y, width, height, Color(10, 10, 10))

        ui.text("Monsoon Scripting", x + 10, y + 10, "regular", 28f, Color(177, 182, 166))
        ui.text("Powered by Spritz", x + 10, y + 40f, "regular", 18f, Color(54, 57, 70))

        // scripts
        ui.rect(x + 5f, y + 65f, 160f, height - 70f, Color(15, 15, 15))

        ui.rect(x + 5f, y + 65f, 160f, 38f, Color(5, 5, 5))
        ui.text("Scripts", x + 15f, y + 74f, "regular", 20f, Color.WHITE)

        ui.scissor(x + 10f, y + 103f, 150f, height - 110f) {
            var offset = y + 108f + scriptScroll

            elements.forEach {
                it.position(x + 10f, offset).draw(mouseX, mouseY, mouseDelta)

                offset += it.trueHeight() + 5
            }
        }

        // console
        ui.rect(x + width - 930f, consoleBounds.y.toFloat() - 5f, 925f, consoleBounds.height.toFloat(), Color(15, 15, 15))

        ui.rect(x + width - 930f, consoleBounds.y.toFloat() - 5f, 925f, 38f, Color(5, 5, 5))

        ui.text("Console", consoleBounds.x.toFloat() + 10f, consoleBounds.y.toFloat() + 4f, "regular", 20f, Color.WHITE)

        ui.scissor(consoleBounds.x.toFloat(), consoleBounds.y.toFloat() + 38f, consoleBounds.width.toFloat(), consoleBounds.height.toFloat() - 44f) {
            var offset = consoleBounds.y.toFloat() + 4f + ui.textHeight("regular", 20f) + 15f + logScroll

            Wrapper.monsoon.scriptManager.compilations.forEach { (scriptName, log) ->
                ui.text(scriptName, x + width - 920f, offset, "regular", 15f, if (log.split("\n").last().contains("Failed")) {
                    Color.RED
                } else {
                    Color.WHITE
                })

                offset += ui.textHeight("regular", 15f) + 2

                ui.text(log, x + width - 880f, offset, "regular", 12f, if (log.split("\n").last().contains("Failed")) {
                    Color(150, 50, 50)
                } else {
                    Color(177, 150, 166)
                })

                offset += (ui.textHeight("regular", 12f) + 2) * log.split("\n").size
            }
        }

        // selected
        if (selected != null) {
            ui.rect(consoleBounds.x.toFloat(), y + 65f, consoleBounds.width.toFloat(), height - consoleBounds.height.toFloat() - 75f, Color(15, 15, 15))
            ui.rect(consoleBounds.x.toFloat(), y + 65f, consoleBounds.width.toFloat(), 38f, Color(5, 5, 5))

            ui.text(selected!!.script.name, consoleBounds.x + 10f, y + 74f, "regular", 20f, Color.WHITE)
            ui.text("Made by ${selected!!.script.author}", consoleBounds.x + 10f, y + 110f, "regular", 15f, Color(54, 57, 70))
            ui.text(selected!!.script.description, consoleBounds.x + 10f, y + 127f, "regular", 15f, Color(54, 57, 70))

            val totalArea = consoleBounds.width - 20f
            val moduleWidth = totalArea / 5f

            var offsetX = consoleBounds.x + 10f
            var offset = (y + height - consoleBounds.height.toFloat() - 50f) - ((selected!!.script.modules.size / 5) * 35)

            ui.text("Introduces: ", offsetX, offset - ui.textHeight("regular", 17f) - 10f, "regular", 17f, Color.WHITE)

            selected!!.script.modules.forEach { module ->
                ui.rect(offsetX, offset, moduleWidth - 5f, 30f, Color(20, 20, 20))
                ui.text(module.name, offsetX + 5, offset + 8, "regular", 14f, Color.WHITE)

                offsetX += moduleWidth

                if (offsetX + 150f > consoleBounds.x + consoleBounds.width) {
                    offsetX = consoleBounds.x + 10f
                    offset += 35f
                }
            }
        }

        reload.drawButton(null, Mouse.getX(), Mouse.getY())
    }

    override fun click(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (reload.isHovered()) {
            reload.click()
            return true
        }

        if (scriptBounds.contains(mouseX, mouseY)) {
            elements.forEach {
                if (it.hovered(mouseX, mouseY)) {
                    selected = if (selected == it) null else it
                    return true
                }
            }
        }

        return super.click(mouseX, mouseY, button)
    }

}