package wtf.monsoon.backend.manager.script

import spritz.SpritzEnvironment
import spritz.api.Config
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.manager.script.link.*
import wtf.monsoon.backend.module.Module
import wtf.monsoon.client.ui.kpanel.UIScreen
import java.nio.charset.Charset

/**
 * @author surge
 * @since 27/03/2023
 */
class ScriptManager {

    val scripts = hashMapOf<String, Script>()
    val active = hashMapOf<String, Module>()

    val compilations = hashMapOf<String, String>()

    fun initialise() {
        scripts.forEach { (name, script) ->
            script.modules.forEach { module ->
                Wrapper.monsoon.bus.unsubscribe(module)
            }
        }

        scripts.clear()
        active.clear()
        compilations.clear()

        Wrapper.monsoon.fileManager.directories["scripts"]?.listFiles()?.forEach { file ->
            compilations[file.name] = ""

            // forced assignations - just no. why did i even add them?
            // natives - MIGHT be able to help deobfuscation idrk.
            val spritz = SpritzEnvironment(Config(forcedAssignations = false, natives = false))
                .putInstance("monsoon", MonsoonLink())
                .putInstance("player", PlayerLink())
                .putInstance("draw", DrawLink())
                .putInstance("world", WorldLink())
                .putInstance("packets", PacketLink())
                .putInstance("minecraft", MinecraftLink())
                .putClass("Colour", DrawLink.Colour::class.java)

            var status = "Compiled"

            spritz.setWarningHandler {
                compilations[file.name] += it.toString()
                println(it)
            }.setErrorHandler {
                compilations[file.name] += it.toString()
                status = "Failed"
                println(it)
            }

            val initial = System.currentTimeMillis()

            // everything should be handled inside the script
            spritz.evaluate(file.name, file.readText(Charset.defaultCharset()))

            compilations[file.name] += "\n$status in ${System.currentTimeMillis() - initial}ms"

            // the arrow thing doesn't work :(
            compilations[file.name] = compilations[file.name]!!.replace("^", "") + "\n"
        }

        // re-init ui
        Wrapper.monsoon.dropdownGui = UIScreen()
    }

    fun put(name: String, module: Module) {
        if (active.containsKey(name)) {
            throw Exception("Module already exists!")
        }

        active[name] = module
    }

    data class Script(val name: String, val author: String, val description: String) {

        val modules = mutableListOf<Module>()

    }

}