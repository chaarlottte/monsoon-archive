package wtf.monsoon.impl.event;

import lombok.Data;
import wtf.monsoon.api.event.Event;

@Data
public class EventFOVUpdate extends Event {

    private final float fov;
    private float newFOV;

    public EventFOVUpdate(float fov) {
        this.fov = fov;
        this.newFOV = fov;
    }
}
