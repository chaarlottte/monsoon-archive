package wtf.monsoon.impl.command;

import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;

/**
 * @author Surge
 * @since 28/07/2022
 */
public class SayCommand extends Command {

    public SayCommand() {
        super("Say");
    }

    @Override
    public void execute(String[] args) {
        // Concatenate the arguments into a single string.
        StringBuilder concatenated = new StringBuilder();

        // Add arguments to the string.
        for (String arg : args) {
            concatenated.append(arg).append(" ");
        }

        // Comedy fix to remove additional space at the end of the string.
        if (concatenated.charAt(concatenated.length() - 1) == ' ') {
            concatenated.deleteCharAt(concatenated.length() - 1);
        }

        PlayerUtil.sendClientMessage("said '" + concatenated + "'");

        // Send a raw C01PacketChatMessage to the server, in order to bypass the firing of the EventChatMessageSent event, that could cause us to cancel sending the message.
        Wrapper.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(concatenated.toString()));
    }

}
