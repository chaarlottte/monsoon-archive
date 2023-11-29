package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.module.player.ChatSpammer;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class SpamCommand extends Command {

    public SpamCommand() {
        super("Spam");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .spam <username>");
            return;
        }

        String username = args[0];
        Wrapper.getModule(ChatSpammer.class).setUsername(username);
        Wrapper.getModule(ChatSpammer.class).getMode().setValue(ChatSpammer.Mode.HYPIXEL);
        Wrapper.getModule(ChatSpammer.class).setEnabled(true);
        PlayerUtil.sendClientMessage("Now spamming user " + username + "...");
        Wrapper.getNotifManager().notify(NotificationType.YES, "Spammer", "Now spamming user " + username + "...");
    }

}
