package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventRenderHotbar;

public class Hotbar extends Module {
    public Hotbar() {
        super("Hotbar", "Renders a custom hotbar instead of the vanilla one", Category.HUD);
    }

    @EventLink
    private final Listener<EventRenderHotbar> erhListener = e -> {

    };
}
