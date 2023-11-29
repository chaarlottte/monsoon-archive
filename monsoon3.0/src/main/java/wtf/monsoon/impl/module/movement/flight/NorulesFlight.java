package wtf.monsoon.impl.module.movement.flight;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Flight;

public class NorulesFlight extends ModeProcessor {

    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5)
            .minimum(0.0D)
            .maximum(2.0D)
            .incrementation(0.05D)
            .describedBy("The speed you will go.");

    private final Setting<Float> timerSpeed = new Setting<>("Timer Speed", 1.0f)
            .minimum(1.0f)
            .maximum(3.0f)
            .incrementation(0.05f)
            .describedBy("The timer speed during flight.");

    public NorulesFlight(Module parentModule) {
        super(parentModule);
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        player.setOnGround(true);
        e.setOnGround(mc.thePlayer.ticksExisted % 2 == 0);
        mc.thePlayer.motionY = 0;
        e.setY(Math.round(mc.thePlayer.posY));
        if(mc.thePlayer.ticksExisted % 2 == 0) mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.prevPosX, mc.thePlayer.posY - 1, mc.thePlayer.prevPosZ), 1, new ItemStack(Blocks.stone), 1, 1, 1));
        if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) player.setSpeed(speedValue.getValue());
        mc.getTimer().timerSpeed = timerSpeed.getValue();
        mc.thePlayer.cameraYaw = 0.07f;
    };

    @Override
    public Setting[] getModeSettings() {
        return new Setting[] { speedValue, timerSpeed };
    }

}
