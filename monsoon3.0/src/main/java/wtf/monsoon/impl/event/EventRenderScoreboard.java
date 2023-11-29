package wtf.monsoon.impl.event;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Scoreboard;
import wtf.monsoon.api.event.Event;

@Getter @Setter
public class EventRenderScoreboard extends Event {

    @NonNull private Scoreboard scoreboard;
    @NonNull private ScaledResolution sr;
    @NonNull private int y;

    public EventRenderScoreboard(Scoreboard scoreboard, ScaledResolution sr, int y) {
        this.scoreboard = scoreboard;
        this.sr = sr;
        this.y = y;
    }

}
