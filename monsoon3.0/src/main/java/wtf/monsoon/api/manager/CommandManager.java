package wtf.monsoon.api.manager;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.event.EventChatMessageSent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Surge
 * @since 28/07/2022
 */
public class CommandManager {

    // List of commands
    private final List<Command> commands = new ArrayList<>();

    // The command prefix to start command execution messages with
    private final String prefix = ".";

    public CommandManager() {
        Wrapper.getEventBus().subscribe(this);
    }

    @EventLink
    public final Listener<EventChatMessageSent> eventChatMessageSentListener = event -> {
        // Get message content
        String message = event.getContent();

        // Check message starts with our prefix
        if (message.startsWith(prefix)) {
            // Cancel sending the message to the server.
            event.cancel();

            // Get all arguments, separated by whitespaces
            String[] args = message.split(" ");

            // Get command name
            String commandName = args[0].substring(prefix.length());

            // Get command arguments
            String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

            // Get the executed command
            Command executedCommand = commands.stream().filter(command -> command.getName().equalsIgnoreCase(commandName)).findFirst().orElse(null);

            // Execute command if it exists
            if (executedCommand != null) {
                executedCommand.execute(commandArgs);
            }

            // Else notify the user
            else {
                PlayerUtil.sendClientMessage("Command does not exist!");
            }
        }
    };

    /**
     * Gets the commands list
     *
     * @return The commands list
     */
    public List<Command> getCommands() {
        return commands;
    }

}
