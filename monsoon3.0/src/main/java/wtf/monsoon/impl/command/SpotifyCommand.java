package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.module.hud.Spotify;

public class SpotifyCommand extends Command {
    public SpotifyCommand() {
        super("Spotify");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            PlayerUtil.sendClientMessage("Usage: .spotify <spotify oauth token>");
            return;
        }
        String token = args[0];

        Wrapper.getModule(Spotify.class).setToken(token);
    }
}
