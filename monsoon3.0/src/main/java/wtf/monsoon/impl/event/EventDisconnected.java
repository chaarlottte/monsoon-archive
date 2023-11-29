package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.IChatComponent;
import wtf.monsoon.api.event.Event;

@EqualsAndHashCode(callSuper = true)
@Data @AllArgsConstructor
public class EventDisconnected extends Event {
    private IChatComponent reason;
    private ServerData serverData;
}
