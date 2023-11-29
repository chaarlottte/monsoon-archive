package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import wtf.monsoon.api.event.Event;

@Data @AllArgsConstructor
public class EventStep extends Event {

    private AxisAlignedBB axisAlignedBB;
    private Entity entity;
    private float height;

}
