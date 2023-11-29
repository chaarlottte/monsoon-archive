package wtf.monsoon.backend.manager

import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module

class ModuleManager : LinkedHashMap<Class<out Module>, Module>() {

    val modules: List<Module>
        get() {
            val scriptModules = Wrapper.monsoon.scriptManager.scripts.map { it.value.modules }

            val allModules = mutableListOf<Module>()

            scriptModules.forEach { list ->
                list.forEach { module ->
                    allModules.add(module)
                }
            }

            return ArrayList(this.values + allModules)
        }

    fun getModuleByCategory(cat: Category) : MutableList<Module> {
        return modules.filter { it.category == cat }.toMutableList()
    }

    fun <T : Module> getModule(clazz: Class<T>): T {
        return this[clazz] as T
    }

}