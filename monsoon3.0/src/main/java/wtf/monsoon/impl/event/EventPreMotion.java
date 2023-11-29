package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

@Data
public class EventPreMotion extends Event {

    @NonNull
    private double x, y, z;
    @NonNull
    private float yaw, pitch;
    @NonNull
    private boolean onGround;

}
