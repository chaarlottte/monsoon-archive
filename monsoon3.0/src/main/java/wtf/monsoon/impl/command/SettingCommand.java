package wtf.monsoon.impl.command;

import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;

import java.util.Arrays;

public class SettingCommand extends Command {
    public SettingCommand() {
        super("setting");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            PlayerUtil.sendClientMessage("Usage: .setting <module> <setting> <value>");
            return;
        }
        String moduleName = args[0];
        String settingName = args[1];
        String valueString = args[2];

        Module module = null;
        for (Module mod : Wrapper.getMonsoon().getModuleManager().getModules()) {
            String str = mod.getName().toLowerCase().replaceAll(" ", "_");
            if (str.equalsIgnoreCase(moduleName)) module = mod;
        }
        if (module != null) {
            Setting setting = module.getSettings().stream().filter(set -> set.getName().replaceAll(" ", "_").equalsIgnoreCase(settingName)).findFirst().orElse(null);
            if (setting == null) {
                PlayerUtil.sendClientMessage(EnumChatFormatting.RED + "Error" + EnumChatFormatting.RESET + ": Unknown setting [" + settingName + "]");
                return;
            }
            String oldValueString = setting.getValue() != null ? setting.getValue() + "" : "NULL-PTR";
            Object value = null;
            try {
                if (setting.getValue() instanceof Boolean) {
                    value = Boolean.valueOf(valueString);
                    setting.setValue(value);
                } else if (setting.getValue() instanceof Integer) {
                    value = Integer.valueOf(valueString);
                    setting.setValue(value);
                } else if (setting.getValue() instanceof Double) {
                    value = Double.valueOf(valueString);
                    if ((Double) value <= (Double) setting.getMaximum() &&
                            (Double) value >= (Double) setting.getMinimum()) {
                        setting.setValue(value);
                    } else if ((Double) value > (Double) setting.getMaximum()) setting.setValue(setting.getMaximum());
                    else if ((Double) value < (Double) setting.getMinimum()) setting.setValue(setting.getMinimum());
                } else if (setting.getValue() instanceof Float) {
                    value = Float.valueOf(valueString);
                    if ((Float) value <= (Float) setting.getMaximum() &&
                            (Float) value >= (Float) setting.getMinimum()) {
                        setting.setValue(value);
                    } else if ((Float) value > (Float) setting.getMaximum()) setting.setValue(setting.getMaximum());
                    else if ((Float) value < (Float) setting.getMinimum()) setting.setValue(setting.getMinimum());
                } else {
                    Enum<?> enumeration = (Enum) setting.getValue();
                    String[] values = Arrays.stream(enumeration.getClass().getEnumConstants()).map(Enum::name).toArray(String[]::new);

                    boolean ok = false;

                    for (String val : values) {
                        if (val.equalsIgnoreCase(valueString)) {
                            ok = true;
                            break;
                        }
                    }

                    if (ok) {
                        while (!(setting.getValue().toString().equalsIgnoreCase(valueString))) {
                            setting.setValue(setting.getMode(false));
                        }
                    }

                    value = valueString;
                }
                System.out.println(setting.getValue().getClass().getSimpleName().toLowerCase());
            } catch (NumberFormatException e) {
                PlayerUtil.sendClientMessage(EnumChatFormatting.RED + "Error" + EnumChatFormatting.RESET + ": Incorrect value!");
            }

            if (value != null) {
                PlayerUtil.sendClientMessage(String.format("Set %s from %s to %s", settingName.toUpperCase(), oldValueString.toUpperCase(), valueString.toUpperCase()));
            }
        } else
            PlayerUtil.sendClientMessage(EnumChatFormatting.RED + "Error" + EnumChatFormatting.RESET + ": Unknown module [" + moduleName + "]");
    }
}
