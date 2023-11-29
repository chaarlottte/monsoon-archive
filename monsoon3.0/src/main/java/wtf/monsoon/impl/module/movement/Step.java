package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventStep;
import wtf.monsoon.impl.event.EventUpdate;

import java.util.HashMap;
import java.util.Map;

public class Step extends Module {

    public final Setting<Mode> mode = new Setting<>("Mode", Mode.VANILLA)
            .describedBy("Da mode");

    private final Setting<Float> stepHeight = new Setting<Float>("Step Height", 1.5f)
            .minimum(0.5f)
            .maximum(2.5f)
            .incrementation(0.5f)
            .describedBy("How high to step up.");

    public final Setting<Boolean> useTimer = new Setting<>("Use Timer", true)
            .describedBy("Da mode")
            .visibleWhen(() -> mode.getValue() == Mode.NCP);

    private Map<Double, double[]> ncpOffsets = new HashMap<>();

    private boolean timer = false;

    public Step() {
        super("Step", "Step up blox", Category.MOVEMENT);
        ncpOffsets.put(0.875, new double[] { 0.39, 0.7, 0.875 });
        ncpOffsets.put(1.0, new double[] { 0.42, 0.75, 1.0 });
        ncpOffsets.put(1.5, new double[] { 0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43 });
        ncpOffsets.put(2.0, new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.919 });
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.stepHeight = 0.6f;
        mc.getTimer().timerSpeed = 1.0f;
        timer = false;
    }

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        mc.thePlayer.stepHeight = stepHeight.getValue();

        if(timer && player.isOnGround()) {
            timer = false;
            mc.getTimer().timerSpeed = 1.0f;
        }
    };

    @EventLink
    public final Listener<EventStep> eventStepListener = e -> {
        if(e.getEntity() == mc.thePlayer) {
            switch (mode.getValue()) {
                case NCP:
                    double height = e.getAxisAlignedBB().minY - mc.thePlayer.posY;
                    if (height > stepHeight.getValue() || !player.isOnGround() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) return;

                    double[] offsets = ncpOffsets.getOrDefault(height, null);

                    if (offsets == null || offsets.length == 0) return;
                    if (useTimer.getValue()) {
                        mc.getTimer().timerSpeed = 1.0f / (1.0f / (offsets.length + 1.0f));
                        timer = true;
                    }

                    for (double offset : offsets) {
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY + offset,
                                mc.thePlayer.posZ,
                                false));
                    }
                    break;
            }
        }
    };

    enum Mode {
        VANILLA, NCP
    }
}
