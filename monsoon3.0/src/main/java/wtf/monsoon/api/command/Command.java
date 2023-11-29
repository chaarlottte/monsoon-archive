package wtf.monsoon.api.command;

import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;

/**
 * @author Surge
 * @since 28/07/2022
 */
public abstract class Command {

    // Name of the command
    private final String name;

    // Minecraft instance
    public Minecraft mc = Wrapper.getMinecraft();

    public Command(String name) {
        this.name = name;
    }

    /**
     * Executes the command.
     *
     * @param args The arguments we have supplied.
     */
    public abstract void execute(String[] args);

    /**
     * Gets the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

}
