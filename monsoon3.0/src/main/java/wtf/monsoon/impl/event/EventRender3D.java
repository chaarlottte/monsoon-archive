package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

@Data
public class EventRender3D extends Event {

    @NonNull
    private float partialTicks;

}
