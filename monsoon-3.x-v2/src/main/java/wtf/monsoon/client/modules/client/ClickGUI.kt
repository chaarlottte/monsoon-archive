package wtf.monsoon.client.modules.client

import org.lwjgl.input.Keyboard
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.DefaultKey
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.ui.kpanel.UIScreen

/**
 * @author surge
 * @since 09/02/2023
 */
@DefaultKey(key = Keyboard.KEY_RSHIFT)
class ClickGUI : Module("ClickGUI", "The main GUI of the client", Category.CLIENT) {

    val pause = Setting("Pause", "Pause the game when in the ClickGUI", false)

    val particles = Setting("Particles", "Display particles in the GUI", true)
    val particleSpeed = Setting("Speed", "The speed of the particles in the background", 5f) minimum 1f maximum 10f incrementation 0.1f childOf particles

    override fun enable() {
        super.enable()
        mc.displayGuiScreen(UIScreen())
        this.toggle()
    }

}