package wtf.monsoon.client.modules.movement

import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.modules.movement.speed.*

class Speed : MulticlassModule("Speed", "Achieve faster speeds.", Category.MOVEMENT) {
    override var modeSetting: Setting<ModuleMode<*>> = Setting("Mode", "The mode of the speed.", this.getModes())

    private fun getModes() : List<ModuleMode<Speed>> {
        val modes: MutableList<ModuleMode<Speed>> = ArrayList()
        modes.add(VanillaSpeed("Vanilla", this))
        modes.add(NCPSpeed("NCP", this))
        modes.add(VerusSpeed("Verus", this))
        return modes
    }
}