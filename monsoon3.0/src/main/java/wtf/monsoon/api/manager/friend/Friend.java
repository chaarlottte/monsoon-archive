package wtf.monsoon.api.manager.friend;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;

public class Friend {

    // The name of the friend
    @Getter
    @Setter
    private String name;

    // The world's instance of the player
    @Getter
    private EntityPlayer player;

    public Friend(String name) {
        this.name = name;
    }

    /**
     * Sets the player object
     *
     * @param player The player object
     * @return The friend instance
     */
    public Friend setPlayer(EntityPlayer player) {
        this.player = player;
        return this;
    }

}
