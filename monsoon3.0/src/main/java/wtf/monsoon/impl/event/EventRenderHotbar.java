package wtf.monsoon.impl.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.api.event.Event;

@Getter
@AllArgsConstructor
public class EventRenderHotbar extends Event {
    ScaledResolution sr;

    @Getter
    public static class Pre extends EventRenderHotbar {
        ScaledResolution sr;

        public Pre(ScaledResolution sr) {
            super(sr);
        }
    }
}
