package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.player.Scaffold;

public class Sprint extends Module {

    public final Setting<Boolean> omni = new Setting<>("Omni", false)
            .describedBy("Whether to sprint in all directions.");

    public Sprint() {
        super("Sprint", "Always sprint", Category.MOVEMENT);
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
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (player.isMoving() && !Wrapper.getModule(Scaffold.class).isEnabled()) {
            if (omni.getValue()) {
                mc.thePlayer.setSprinting(true);
            } else {
                if (player.isMoving() && (mc.thePlayer.moveForward >= Math.abs(mc.thePlayer.moveStrafing)))
                    mc.thePlayer.setSprinting(true);
            }
        }
    };
}
