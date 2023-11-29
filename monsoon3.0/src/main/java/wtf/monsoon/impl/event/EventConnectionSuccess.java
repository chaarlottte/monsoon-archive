package wtf.monsoon.impl.event;

import com.mojang.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.IChatComponent;
import wtf.monsoon.api.event.Event;

@EqualsAndHashCode(callSuper = true)
@Data @AllArgsConstructor
public class EventConnectionSuccess extends Event {
    private ServerData serverData;
    private GameProfile gameProfile;
}
