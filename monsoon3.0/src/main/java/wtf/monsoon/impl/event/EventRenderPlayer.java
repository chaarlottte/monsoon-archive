package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

@Data
public class EventRenderPlayer extends Event {

    @NonNull
    private float yaw;
    @NonNull
    private float pitch;
    @NonNull
    private float partialTicks;
}
