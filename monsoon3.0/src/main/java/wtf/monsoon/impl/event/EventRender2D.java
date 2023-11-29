package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.NonNull;
import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.api.event.Event;

@Data
public class EventRender2D extends Event {

    @NonNull
    private ScaledResolution sr;

    @NonNull
    private float partialTicks;

    @NonNull
    private float width, height;
}
