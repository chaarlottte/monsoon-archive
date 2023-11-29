package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.module.player.AutoHypixel;

public class BlacklistMap extends Command {

    public BlacklistMap() {
        super("BlacklistMap");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .blacklistmap map name>");
            return;
        }

        String map = args[0];

        Wrapper.getModule(AutoHypixel.class).addBlacklistedMap(map);
        PlayerUtil.sendClientMessage("Blacklisted map " + map + ".");
    }

}
