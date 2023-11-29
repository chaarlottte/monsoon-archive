package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import net.minecraft.network.Packet;
import wtf.monsoon.api.event.Event;

@Data
public class EventPacket extends Event {

    @NonNull
    public Packet packet;
    public boolean cancelled;
    @NonNull
    public Direction direction;


    public enum Direction {
        SEND, RECEIVE
    }

}
