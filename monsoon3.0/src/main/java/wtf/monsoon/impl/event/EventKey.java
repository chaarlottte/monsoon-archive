package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

@Data
public class EventKey extends Event {

    @NonNull
    private int key;

}
