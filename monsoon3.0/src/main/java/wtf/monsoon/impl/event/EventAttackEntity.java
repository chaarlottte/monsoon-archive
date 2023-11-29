package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.entity.Entity;
import wtf.monsoon.api.event.Event;

/**
 * @author Surge
 * @since 28/07/2022
 */

@AllArgsConstructor
public class EventAttackEntity extends Event {

    @Getter
    private Entity target;

}
