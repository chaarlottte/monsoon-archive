package wtf.monsoon.api.event;

import lombok.Getter;
import lombok.Setter;

public class Event {

    @Getter
    @Setter
    private boolean cancelled;

    public void cancel() {
        this.cancelled = true;
    }

}
