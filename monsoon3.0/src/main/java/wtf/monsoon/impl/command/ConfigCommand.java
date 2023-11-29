package wtf.monsoon.impl.command;

import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.config.ConfigSystem;
import wtf.monsoon.api.util.entity.PlayerUtil;

import java.io.File;

/**
 * @author Surge
 * @since 28/07/2022
 */
public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("Config");
    }

    private int configLoadRetries = 0;

    @Override
    public void execute(String[] args) {
        // Check that we have at least two arguments.
        if (args.length < 2) {
            PlayerUtil.sendClientMessage("Usage: .config <action> <config name>");
            return;
        }

        // The action we want to perform
        String action = args[0];

        // The config name
        String name = args[1];

        // Perform action
        switch (action) {
            case "save":
                // Save the config
                Wrapper.getMonsoon().getConfigSystem().save(name);

                // Notify
                PlayerUtil.sendClientMessage("Saved config " + name + ".");
                break;

            case "load":
                // Check config exists beforehand
                if (Wrapper.getMonsoon().getConfigSystem().configExists(name)) {
                    // Load the config
                    try {
                        if(configLoadRetries >= 1) Wrapper.getMonsoon().getConfigSystem().load(name, true);
                        else Wrapper.getMonsoon().getConfigSystem().loadNoCatch(name, false);
                    } catch (ConfigSystem.ConfigForOldVersionException e) {
                        PlayerUtil.sendClientMessage(e.getMessage());
                        PlayerUtil.sendClientMessage("Support will not be provided by Monsoon for using this config.");
                        PlayerUtil.sendClientMessage("Run the command again to confirm you want to load this config.");
                        break;
                    }

                    PlayerUtil.sendClientMessage("Loaded config " + name + ".");
                }

                // Otherwise notify the user that the config doesn't exist.
                else {
                    PlayerUtil.sendClientMessage("A config with the name " + name + " does not exist!");
                }
                break;

            case "delete":
                // Check config exists beforehand
                if (Wrapper.getMonsoon().getConfigSystem().configExists(name)) {
                    // Delete the config
                    boolean deleted = new File("monsoon" + File.separator + "configs" + File.separator + name + ".json").delete();

                    if (deleted) {
                        PlayerUtil.sendClientMessage("Deleted config " + name + ".");
                    }

                    // rare case of no write/delete access
                    else {
                        PlayerUtil.sendClientMessage("Failed to delete config " + name + "!");
                    }
                }

                // Otherwise notify the user that the config doesn't exist.
                else {
                    PlayerUtil.sendClientMessage("A config with the name " + name + " does not exist!");
                }
                break;

            default:
                // The action the user tried to perform was not recognised.
                PlayerUtil.sendClientMessage("Unknown action: " + action);
                PlayerUtil.sendClientMessage("Valid actions: 'save', 'load', 'delete'");
                PlayerUtil.sendClientMessage("Syntax: .config <action> <configname>");
                break;
        }
    }

}
