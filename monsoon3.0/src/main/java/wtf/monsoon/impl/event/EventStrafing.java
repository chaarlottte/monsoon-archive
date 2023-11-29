package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

@Data @AllArgsConstructor
public class EventStrafing extends Event {

    @NonNull
    private float forward, strafe, friction, attributeSpeed, yaw, pitch;
}
