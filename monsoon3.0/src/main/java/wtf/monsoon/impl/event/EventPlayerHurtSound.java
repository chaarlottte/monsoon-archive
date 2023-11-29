package wtf.monsoon.impl.event;

import lombok.Getter;
import net.minecraft.entity.EntityLivingBase;
import wtf.monsoon.api.event.Event;

@Getter
public class EventPlayerHurtSound extends Event {

    private final EntityLivingBase entity;

    public EventPlayerHurtSound(EntityLivingBase entityLivingBase) {
        this.entity = entityLivingBase;
    }

}
