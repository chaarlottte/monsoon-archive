package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import net.minecraft.client.Minecraft;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.util.entity.MovementUtil;

@Data
public class EventMove extends Event {

    @NonNull
    private double x, y, z;

}