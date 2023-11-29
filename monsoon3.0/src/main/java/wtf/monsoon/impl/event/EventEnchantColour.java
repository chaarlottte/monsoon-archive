package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import wtf.monsoon.api.event.Event;

import java.awt.*;

/**
 * @author Surge
 * @since 02/01/2023
 */
@Data
public class EventEnchantColour extends Event {

    @NonNull
    private Color colour;

}
