package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventPreMotion;

public class ClickDelayRemover extends Module {

    public ClickDelayRemover() {
        super("Click Delay Remover", "Removes the 1.8 hit delay.", Category.GHOST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    private final Listener<EventPreMotion> eventUpdateEventListener = e -> {
        mc.leftClickCounter = 0;
    };
}
