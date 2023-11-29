package wtf.monsoon.backend.manager.script.link

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.gui.ScaledResolution
import spritz.api.CallData
import spritz.api.Coercion
import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier
import spritz.api.result.Failure
import spritz.api.result.Result
import spritz.api.result.Success
import spritz.interpreter.RuntimeResult
import spritz.value.Value
import spritz.value.`class`.DefinedInstanceValue
import spritz.value.`class`.JvmInstanceValue
import spritz.value.string.StringValue
import spritz.value.table.Table
import spritz.value.table.TableAccessor
import spritz.value.task.DefinedTaskValue
import spritz.value.task.TaskValue
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.manager.script.ScriptManager
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventClientTick
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventRender2D
import wtf.monsoon.client.event.EventUpdate
import wtf.monsoon.client.modules.hud.HUDModule
import wtf.monsoon.client.util.ui.NVGWrapper
import wtf.monsoon.temp.ITempUI
import wtf.monsoon.temp.TempRenderer
import java.util.stream.Collectors

/**
 * @author surge
 * @since 27/03/2023
 */
class MonsoonLink {

    @Excluded
    var registrationScope: ScriptManager.Script? = null

    @Excluded
    fun find(name: String, table: Table, parameterCount: Int = 0): Value? {
        return TableAccessor(table)
            .identifier(name)
            .top(true)
            .predicate { it is DefinedTaskValue && it.arguments.size == parameterCount }
            .find().value
    }

    @Identifier("register_module")
    fun registerModule(data: CallData, name: String, description: String, module: DefinedInstanceValue): Result {
        if (registrationScope == null) {
            return Failure(RegistrationError(
                "Registration was not in registration scope - make sure you are using `monsoon::register`!",
                data.start,
                data.end,
                data.context
            ))
        }

        val enable = find("enable", module.table)
        val disable = find("disable", module.table)

        val update = find("on_update", module.table)
        val render2d = find("on_render_2d", module.table, parameterCount = 1)

        val inbound = find("inbound", module.table, parameterCount = 1)
        val outbound = find("inbound", module.table, parameterCount = 1)

        val moduleInstance = object : Module(name, description, Category.SCRIPT), ITempUI {

            init {
                module.table.symbols.forEach {
                    if (it.value is JvmInstanceValue && (it.value as JvmInstanceValue).instance is Setting<*>) {
                        this.settings.add((it.value as JvmInstanceValue).instance as Setting<*>)
                    }
                }

                TempRenderer.register(this) { this.isEnabled() }
            }

            override fun enable() {
                super.enable()

                val result = enable?.execute(arrayListOf())

                if (result?.error != null) {
                    println("Disabling '${this.name}', error occurred in 'enable':\n ${result.error}")
                    disable()
                }
            }

            override fun disable() {
                super.disable()

                val result = disable?.execute(arrayListOf())

                if (result?.error != null) {
                    println("Disabling '${this.name}', error occurred in 'disable':\n ${result.error}")
                    disable()
                }
            }

            @EventListener
            val updateListener = fun(_: EventUpdate) {
                val result = update?.execute(arrayListOf())

                if (result?.error != null) {
                    println("Disabling '${this.name}', error occurred in 'on_update':\n ${result.error}")
                    disable()
                }
            }

            @EventListener
            val packetListener = fun(it: EventPacket) {
                if (it.direction == EventPacket.PacketDirection.SEND) {
                    val result = outbound?.execute(arrayListOf(Coercion.IntoSpritz.coerce(it)))

                    if (result?.error != null) {
                        println("Disabling '${this.name}', error occurred in 'outbound':\n ${result.error}")
                        disable()
                    }
                } else if (it.direction == EventPacket.PacketDirection.RECEIVE) {
                    val result = inbound?.execute(arrayListOf(Coercion.IntoSpritz.coerce(it)))

                    if (result?.error != null) {
                        println("Disabling '${this.name}', error occurred in 'inbound':\n ${result.error}")
                        disable()
                    }
                }
            }

            override fun render2D(nvg: NVGWrapper, width: Int, height: Int) {
                val result = render2d?.execute(arrayListOf(Coercion.IntoSpritz.coerce(ScaledResolution(mc))))

                if (result?.error != null) {
                    println("Disabling '${this.name}', error occurred in 'render2d':\n ${result.error}")
                    disable()
                }
            }

        } fromScript registrationScope!!

        try {
            registrationScope!!.modules.add(moduleInstance)
            moduleInstance.reflect()
        } catch (exception: Exception) {
            return Failure(RegistrationError(
                "Module with name '$name' already exists!",
                data.start,
                data.end,
                data.context
            ))
        }

        return Success()
    }

    @Identifier("setting")
    fun registerSetting(name: StringValue, description: StringValue, value: Value): Value {
        return Coercion.IntoSpritz.coerce(Setting(name.value, description.value, value.asJvmValue()))
    }

    @Identifier("register")
    fun register(data: CallData, name: String, author: String, description: String, initialisation: TaskValue): Result {
        val result = RuntimeResult()

        registrationScope = ScriptManager.Script(name, author, description)

        result.register(initialisation.execute(arrayListOf()))

        if (result.error != null) {
            return Failure(result.error!!)
        }

        if (Wrapper.monsoon.scriptManager.scripts.containsKey(name)) {
            return Failure(RegistrationError(
                "Script with name '$name' already exists!",
                data.start,
                data.end,
                data.context
            ))
        } else {
            Wrapper.monsoon.scriptManager.scripts[name] = registrationScope!!
        }

        registrationScope = null

        return Success()
    }

    @Identifier("get_version")
    fun getVersion(): String = Wrapper.monsoon.version

    @Identifier("get_sorted_modules")
    fun getSortedModules(): List<ModuleWrapper> {
        return Wrapper.monsoon.moduleManager.modules.stream()
            .sorted(Comparator.comparingDouble { module: Module ->
                HUDModule.generateModuleDataAndWidth(module).key()!!
            }.reversed())
            .filter { module -> module!!.visible.getValue() }
            .collect(Collectors.toList())
            .map { ModuleWrapper(it) }
    }

}