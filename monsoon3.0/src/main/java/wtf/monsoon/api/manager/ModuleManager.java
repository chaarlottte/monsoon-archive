package wtf.monsoon.api.manager;

import lombok.Getter;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.module.movement.HighJump;
import wtf.monsoon.impl.module.movement.LongJump;
import wtf.monsoon.impl.module.movement.Speed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    // Map of modules
    private final LinkedHashMap<Class<? extends Module>, Module> modules = new LinkedHashMap<>();

    @Getter
    private final List<Module> retardModules = new ArrayList<>();

    /**
     * Adds a module to the map
     *
     * @param clazz  The class of the module
     * @param module The module's instance
     */
    public void putModule(Class<? extends Module> clazz, Module module) {
        modules.put(clazz, module);
        retardModules.add(module);
    }

    /**
     * Gets a list of the modules
     *
     * @return The modules as a list
     */
    public List<Module> getModules() {
        //return new ArrayList<>(this.modules.values());
        return retardModules;
    }

    /**
     * Gets the sorted modules
     *
     * @return The modules which have been sorted by their name's width
     */
    public List<Module> getSortedModules() {
        return this.getModules().stream().sorted(Comparator.comparing(module -> Wrapper.getFontUtil().productSans.getStringWidth(((Module) module).getDisplayName())).reversed()).collect(Collectors.toList());
    }

    /**
     * Gets a module by its class
     *
     * @param clazz The class of the module
     * @param <T>   The class of the module
     * @return The module
     */
    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    /**
     * Gets a module by its name
     *
     * @param name The name of the module
     * @return The module if it is found, else null
     */
    public Module getModuleByName(String name) {
        for (Module module : modules.values()) {
            if (module.getName().replace(" ", "").equalsIgnoreCase(name)) {
                return module;
            }
        }

        return null;
    }

    public List<Module> getModulesByName(String name) {
        List<Module> shit = new ArrayList<>();
        for (Module module : getModules()) {
            if (module.getName().replace(" ", "").equalsIgnoreCase(name)) {
                shit.add(module);
            }
        }

        return shit;
    }

    /**
     * Gets the modules in a given category
     *
     * @param category The category to get the modules in
     * @return The filtered modules
     */
    public List<Module> getModulesByCategory(Category category) {
        return this.getModules().stream().filter(module -> module.getCategory() == category).collect(Collectors.toList());
    }

    public List<Module> getModulesToDisableOnFlag() {
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(getModule(Speed.class));
        moduleList.add(getModule(LongJump.class));
        moduleList.add(getModule(HighJump.class));
        return moduleList;
    }

}
