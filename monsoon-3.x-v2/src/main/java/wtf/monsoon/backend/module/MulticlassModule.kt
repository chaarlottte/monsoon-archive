package wtf.monsoon.backend.module

import com.github.javafaker.Cat
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.modules.combat.Velocity
import wtf.monsoon.client.util.misc.StringUtil
import java.util.*

open class MulticlassModule(name: String, description: String, category: Category) : Module(name, description, category) {

    private var lastMode: ModuleMode<*>? = null
    private var currentMode: ModuleMode<*>? = null
    open lateinit var modeSetting: Setting<ModuleMode<*>>

    override var metadata: () -> String = { "" }

    override fun enable() {
        super.enable()

        this.currentMode = this.modeSetting.getValue();

        if (this.currentMode != null) {
            this.lastMode = this.currentMode;
            Wrapper.monsoon.bus.subscribe(this.currentMode!!);

            if (mc.thePlayer != null)
                this.currentMode!!.enable();
        }

        this.metadata = this.modeSetting.getValue().metadata
    }

    override fun disable() {
        super.enable()
        if (this.currentMode != null) {
            Wrapper.monsoon.bus.unsubscribe(this.currentMode!!);

            if (mc.thePlayer != null)
                this.currentMode!!.disable();


            if (this.lastMode != null)
                if (Wrapper.monsoon.bus.unsubscribe(this.lastMode!!))
                    if (mc.thePlayer != null)
                        this.lastMode!!.disable();
        }
    }

    fun updateModes() {
        this.lastMode = this.currentMode;
        this.currentMode = this.modeSetting.getValue();

        if (this.isEnabled() && this.lastMode != null && this.lastMode != this.currentMode) {
            Wrapper.monsoon.bus.unsubscribe(this.lastMode!!);
            Wrapper.monsoon.bus.subscribe(this.currentMode!!);
            this.metadata = this.modeSetting.getValue().metadata
        }
    }

    override fun reflect() {
        settings.add(this.modeSetting)

        this.modeSetting.modes?.forEach { m ->
            m::class.java.declaredFields.filter { Setting::class.java.isAssignableFrom(it.type) }.forEach {
                it.isAccessible = true

                try {
                    val setting = it[m] as Setting<*>
                    setting.visibility = { this.modeSetting.getValue() == m }
                    if (setting.parent == null)
                        settings.add(setting)

                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        settings.add(visible)
        settings.add(key)
    }

}