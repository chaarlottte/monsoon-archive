package wtf.monsoon.client.modules.combat

import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting

class TargetStrafe : Module("Target Strafe", "Automatically strafe around opponents when attacking them.", Category.COMBAT) {
    val distance = Setting<Double>("Distance", "Distance", 3.0, 1.0, 6.0, 0.1)

    var direction = false
}