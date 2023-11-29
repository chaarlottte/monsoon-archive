package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import wtf.monsoon.api.event.Event;

@Getter
@Setter
@AllArgsConstructor
public final class EventBlockCollide extends Event {
    private AxisAlignedBB collisionBoundingBox;
    private Block block;
    private int x, y, z;
}