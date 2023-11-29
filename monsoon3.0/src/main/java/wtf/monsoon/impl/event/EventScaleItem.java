package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import wtf.monsoon.api.event.Event;

@Data
@AllArgsConstructor
public class EventScaleItem extends Event {
    private float scaleX, scaleY, scaleZ;
}
