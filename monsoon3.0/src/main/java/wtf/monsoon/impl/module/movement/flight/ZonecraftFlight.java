package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.processor.player.BlinkProcessor;

public class ZonecraftFlight extends ModeProcessor {

    private float y = 0;

    public ZonecraftFlight(Module parentModule) {
        super(parentModule);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Wrapper.getMonsoon().getProcessorManager().getProcessor(BlinkProcessor.class).setBlinking(true);
        Wrapper.getMonsoon().getProcessorManager().getProcessor(BlinkProcessor.class).setDispatch(false);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Wrapper.getMonsoon().getProcessorManager().getProcessor(BlinkProcessor.class).setBlinking(false);
        Wrapper.getMonsoon().getProcessorManager().getProcessor(BlinkProcessor.class).setDispatch(true);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (mc.thePlayer.posY < y) {
            e.setY(y);
            mc.thePlayer.setPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ);
            player.jump();
            player.setOnGround(true);
            e.setOnGround(true);
        } else if (player.isOnGround()) {
            y = (float) mc.thePlayer.posY;
            player.jump();
        }
    };

}
