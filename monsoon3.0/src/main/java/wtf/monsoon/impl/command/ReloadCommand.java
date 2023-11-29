package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.module.player.KillInsults;

// DOESN'T WORK LMAO
public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("Reload");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            PlayerUtil.sendClientMessage("Usage: .reload");
            return;
        }

        Wrapper.getMonsoon().getScriptLoader().reloadScripts();
        Wrapper.getModule(KillInsults.class).loadKillsults();
    }

}
