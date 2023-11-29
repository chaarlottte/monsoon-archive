package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;

import java.lang.reflect.Field;

public class DuplicateCommand extends Command {

    public DuplicateCommand() {
        super("Duplicate");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .duplicate <module>");
            return;
        }
        String moduleName = args[0];

        try {
            Module module = Wrapper.getMonsoon().getModuleManager().getModuleByName(moduleName);
            Module newModule = cloneObject(module);
            //Module newModule = module.clone();
            newModule.setDuplicate(true);
            newModule.setName(module.getName() + " " + MathUtils.randomNumber(0, 999));
            Wrapper.getMonsoon().getModuleManager().getRetardModules().add(newModule);
            PlayerUtil.sendClientMessage("Successfully duplicated module " + module.getName() + ". It should appear in your ClickGUI as " + newModule.getName() + ".");
        } catch (Exception exception) {
            PlayerUtil.sendClientMessage("It didn't work.");
            exception.printStackTrace();
        }
    }

    private Module cloneObject(Module obj) {
        try {
            Module clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
