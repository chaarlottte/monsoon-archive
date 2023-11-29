package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

/**
 * @author Surge
 * @since 28/07/2022
 */

@Data
public class EventChatMessageSent extends Event {

    @NonNull
    private String content;
}
