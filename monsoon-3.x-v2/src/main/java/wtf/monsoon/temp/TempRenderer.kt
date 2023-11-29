package wtf.monsoon.temp

import org.lwjgl.opengl.Display
import wtf.monsoon.Wrapper

object TempRenderer {

    private var interfaceArray: ArrayList<Pair<ITempUI, () -> Boolean>> = ArrayList()

    fun register(ui: ITempUI, condition: () -> Boolean = { true }) {
        interfaceArray.add(Pair(ui, condition))
    }

    fun render() {
        with (Wrapper.monsoon.nvg) {
            beginFrame()

            interfaceArray.forEach { (ui, condition) ->
                if (condition()) {
                    ui.render2D(Wrapper.monsoon.nvg, Display.getWidth(), Display.getHeight())
                }
            }

            endFrame()
        }
    }

}