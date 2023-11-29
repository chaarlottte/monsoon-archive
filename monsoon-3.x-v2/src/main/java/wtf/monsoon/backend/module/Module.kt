package wtf.monsoon.backend.module

import me.surge.animation.Animation
import me.surge.animation.Easing
import org.json.JSONObject
import org.lwjgl.input.Keyboard
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.Feature
import wtf.monsoon.backend.file.Serializable
import wtf.monsoon.backend.manager.script.ScriptManager
import wtf.monsoon.backend.module.mode.ModuleMode
import wtf.monsoon.backend.setting.Bind
import wtf.monsoon.backend.setting.Setting
import java.awt.Color

open class Module(name: String, description: String, val category: Category) : Feature(name, description), Serializable {

    private var enabled: Boolean = false
    val animation: Animation = Animation(300f, false, Easing.CUBIC_IN_OUT)
    var script: ScriptManager.Script? = null

    open var metadata: () -> String = { "" }

    var settings: MutableList<Setting<*>> = ArrayList()

    open val visible = Setting("Visible", "The module's visibility in the arraylist", true)

    val key = Setting(
        "Key",
        "The key used to toggle the module",
        Bind(
            run {
                if (this::class.java.getAnnotation(DefaultKey::class.java) != null) {
                    this::class.java.getAnnotation(DefaultKey::class.java).key
                } else {
                    Keyboard.KEY_NONE
                }
            },
            Bind.Device.KEYBOARD
        )
    )

    open fun enable() {
        Wrapper.monsoon.bus.subscribe(this)
    }

    open fun disable() {
        Wrapper.monsoon.bus.unsubscribe(this)
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled

        this.animation.state = this.enabled

        if (this.enabled) {
            enable()
        } else {
            disable()
        }
    }

    fun toggle() {
        setEnabled(!this.enabled)
    }

    open fun reflect() {
        javaClass.declaredFields.filter { Setting::class.java.isAssignableFrom(it.type) }.forEach {
            it.isAccessible = true

            try {
                val setting = it[this] as Setting<*>

                if (setting.parent == null) {
                    settings.add(setting)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        settings.add(visible)
        settings.add(key)
    }

    infix fun metadata(metadata: () -> String): Module {
        this.metadata = metadata
        return this
    }

    infix fun fromScript(script: ScriptManager.Script): Module {
        this.script = script
        return this
    }

    fun saveName(): String = if (this.script != null) this.script!!.name + "." + this.name else this.name

    override fun save(): JSONObject {
        val obj: JSONObject = JSONObject()

        obj.put("enabled", this.enabled)

        val settingObj: JSONObject = JSONObject()

        this.settings.forEach { setting ->
            when (setting.getValue()) {
                is Boolean -> settingObj.put(setting.name, setting.getValue() as Boolean)
                is Enum<*> -> settingObj.put(setting.name, (setting.getValue() as Enum<*>).name)
                is ModuleMode<*> -> settingObj.put(setting.name, (setting.getValue() as ModuleMode<*>).name)
                is Number -> settingObj.put(setting.name, setting.getValue() as Number)
                is Color -> settingObj.put(
                    setting.name,
                    (setting.getValue() as Color).red.toString() + ":" + (setting.getValue() as Color).green + ":" + (setting.getValue() as Color).blue
                )
            }
        }

        obj.put("settings", settingObj)

        return obj
    }

    override fun load(obj: JSONObject) {
        this.setEnabled(obj.getBoolean("enabled"))
        val settingObj: JSONObject = obj.getJSONObject("settings")
        for (setting in this.settings) {
            try {
                if (setting.getValue() is Boolean) {
                    (setting as Setting<Boolean>).setValue(settingObj.getBoolean(setting.name))
                } else if (setting.getValue() is Double) {
                    (setting as Setting<Double>).setValue(settingObj.getDouble(setting.name))
                } else if (setting.getValue() is ModuleMode<*>) {
                    val value: String = settingObj.getString(setting.name)
                    setting.modes?.forEach {
                        if(it.name == value) {
                            (setting as Setting<ModuleMode<*>>).setValue(it)
                        }
                    }
                } else if (setting.getValue() is Enum<*>) {
                    try {
                        val enumuration = setting.getValue() as Enum<*>
                        val value = java.lang.Enum.valueOf(enumuration::class.java, settingObj.getString(setting.name))
                        (setting as Setting<Enum<*>>).setValue(value)
                    } catch (iae: IllegalAccessException) {
                        iae.printStackTrace()
                        // PlayerUtil.sendClientMessage("A setting for " + this.name + " couldn't be loaded.")
                        continue
                    }
                } else if (setting.getValue() is Color) {
                    val values: Array<String> =
                        settingObj.getString(setting.name).split(":".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    (setting as Setting<Color>).setValue(Color(values[0].toInt(), values[1].toInt(), values[2].toInt()))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}