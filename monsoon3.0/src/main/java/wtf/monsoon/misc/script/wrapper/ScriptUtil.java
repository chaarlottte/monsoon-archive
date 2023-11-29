package wtf.monsoon.misc.script.wrapper;

import me.surge.api.Coercer;
import me.surge.api.result.Result;
import me.surge.api.result.Success;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.setting.Setting;

import java.util.HashMap;

public class ScriptUtil {
    public static HashMap<String, Setting<?>> settings = new HashMap<>();
    static Minecraft mc = Minecraft.getMinecraft();

    public static String currentScreen() {
        return mc.currentScreen != null ? mc.currentScreen.getClass().getSimpleName().toLowerCase() : "null";
    }

    public static int mouseX() {
        return Mouse.getX() / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    }

    public static int mouseY() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - (Mouse.getY() / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor());
    }

    public static boolean mouseDown(int button) {
        return Mouse.isButtonDown(button);
    }

    public static void addNumberSetting(String moduleName, String name, String description, double value, double min, double max, double incrementation) {
        Setting<Double> setting = new Setting<>(name, value)
                .minimum(min)
                .maximum(max)
                .incrementation(incrementation)
                .describedBy(description);

        settings.put(name + " - " + moduleName, setting);
    }

    public static void addBooleanSetting(String moduleName, String name, String description, boolean value) {
        Setting<Boolean> setting = new Setting<>(name, value)
                .describedBy(description);

        settings.put(name + " - " + moduleName, setting);
    }

    public static Result getSettingValue(String moduleName, String name) {
        Setting<?>[] set = new Setting[1];

        Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                module.getSettings().forEach(setting -> {
                    if (setting.getName().equalsIgnoreCase(name)) {
                        set[0] = setting;
                    }
                });
            }
        });

        //return set[0];

        return new Success(Coercer.coerceObject(set[0].getValue()));
    }

}
