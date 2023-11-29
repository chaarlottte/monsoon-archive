package wtf.monsoon.backend.module.mode

import wtf.monsoon.Wrapper
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.backend.module.MulticlassModule
import wtf.monsoon.client.util.player.MonsoonPlayerObject
import wtf.monsoon.misc.InstanceAccess

open class ModuleMode<T : MulticlassModule>(var name: String, var parent: MulticlassModule) : InstanceAccess() {

    private val settings: MutableMap<Setting<*>, String> = LinkedHashMap()

    open var metadata: () -> String = { name }

    open fun enable() {}
    open fun disable() {}

    protected fun registerSettings(vararg settings: Setting<*>) {
        for (setting in settings) {
            this.settings[setting] = setting.name
        }
    }

    val informationSuffix: String?
        get() = null
}