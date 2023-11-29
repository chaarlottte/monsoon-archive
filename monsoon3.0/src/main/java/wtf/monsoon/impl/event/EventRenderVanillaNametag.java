package wtf.monsoon.impl.event;

import lombok.Getter;
import net.minecraft.entity.Entity;
import wtf.monsoon.api.event.Event;

/**
 * @author Surge
 * @since 24/08/2022
 */
public class EventRenderVanillaNametag extends Event {

    @Getter
    private final Entity entity;

    public EventRenderVanillaNametag(Entity entity) {
        this.entity = entity;
    }

}
