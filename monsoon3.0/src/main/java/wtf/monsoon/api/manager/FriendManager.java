package wtf.monsoon.api.manager;

import lombok.Getter;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.friend.Friend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FriendManager {

    @Getter
    private final List<Friend> friends = new ArrayList<>();

    /**
     * Adds a friend with the given username to our friends list
     *
     * @param username The username of the friend
     */
    public void addFriend(String username) {
        Friend friend = new Friend(username);

        if (Wrapper.getMinecraft().theWorld.playerEntities.stream().anyMatch(entityPlayer -> entityPlayer.getDisplayName().getUnformattedText().equalsIgnoreCase(username))) {
            friend.setPlayer(Wrapper.getMinecraft().theWorld.playerEntities.stream().filter(entityPlayer -> entityPlayer.getDisplayName().getUnformattedText().equalsIgnoreCase(username)).collect(Collectors.toList()).get(0));
        }

        friends.add(friend);
    }

    /**
     * Removes a friend from our friends list
     *
     * @param username The username to remove
     * @return Whether we could successfully remove the friend from the friends list
     */
    public boolean removeFriend(String username) {
        if (friends.stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(username))) {
            friends.removeIf(friend -> friend.getName().equalsIgnoreCase(username));
            return true;
        }

        return false;
    }

    /**
     * Checks if a player is a friend, through their username
     *
     * @param username The username of the player to check
     * @return Whether the name given has an object in our friends list that is case insensitively equal to it.
     */
    public boolean isFriend(String username) {
        return friends.stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(username));
    }

}
