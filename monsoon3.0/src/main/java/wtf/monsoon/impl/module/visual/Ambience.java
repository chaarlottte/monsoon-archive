package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventUpdate;

import java.util.function.Supplier;

public class Ambience extends Module {

    public final Setting<Time> time = new Setting<>("Time", Time.NIGHT)
            .describedBy("The time.");

    private static final Timer timer = new Timer();

    public Ambience() {
        super("Ambience", "Change the world time.", Category.VISUAL);
        this.setMetadata(() -> StringUtil.formatEnum(time.getValue()));
    }

    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        if (time.getValue() == Time.DYNAMIC) {
            if (timer.hasTimeElapsed(11500, false)) {
                timer.reset();
            }
        }
    };

    public enum Time {
        MORNING(() -> 23000),
        DAY(() -> 30000),
        NIGHT(() -> 15000),
        MIDNIGHT(() -> 18000),
        DYNAMIC(() -> (int) timer.getTime() * 2);

        @Getter
        private final Supplier<Integer> time;

        Time(Supplier<Integer> time) {
            this.time = time;
        }
    }
}
