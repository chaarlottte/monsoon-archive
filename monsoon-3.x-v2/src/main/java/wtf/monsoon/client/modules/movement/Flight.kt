package wtf.monsoon.client.modules.movement

import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.modules.movement.flight.*

class Flight : MulticlassModule("Flight", "Fly", Category.MOVEMENT) {
    override var modeSetting: Setting<ModuleMode<*>> = Setting("Mode", "The mode of the flight.", this.getModes())

    private fun getModes() : List<ModuleMode<Flight>> {
        val modes: MutableList<ModuleMode<Flight>> = ArrayList()
        modes.add(VanillaFlight("Vanilla", this))
        modes.add(MotionFlight("Motion", this))
        modes.add(UpdatedNCPFlight("Updated NCP", this))
        modes.add(VerusFlight("Verus", this))
        modes.add(VulcanFlight("Vulcan", this))
        modes.add(OldNCPFlight("Old NCP", this))
        modes.add(CollisionFlight("Collision", this))
        modes.add(UnpatchableAirjumpFlight("Air Jump", this))
        return modes
    }
}