package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPreMotion;

import java.util.Random;

public class AutoClicker extends Module {

    private final Timer clickTimer = new Timer();

    @Getter
    private final Setting<Double> cps = new Setting<>("CPS", 10D)
            .minimum(1D)
            .maximum(20D)
            .incrementation(1D)
            .describedBy("The amount of clicks per second.");

    @Getter
    private final Setting<Boolean> block = new Setting<>("Auto Block", false)
            .describedBy("Whether to block automatically.");

    @Getter
    private final Setting<Double> blockFrequency = new Setting<>("Block Frequency", 4D)
            .minimum(0D)
            .maximum(10D)
            .incrementation(1D)
            .describedBy("The block frequency.")
            .childOf(block);

    @Getter
    private final Setting<Boolean> enableRandomization = new Setting<>("Enable Randomization", false)
            .describedBy("Whether to enable randomization.");

    @Getter
    private final Setting<Double> randomization = new Setting<>("Randomization", 3D)
            .minimum(0.5D)
            .maximum(5D)
            .incrementation(0.5D)
            .describedBy("The amount of randomization.")
            .childOf(enableRandomization);

    @Getter
    private final Setting<Boolean> enableJitter = new Setting<>("Enable Jitter", false)
            .describedBy("Whether to enable jittering.");

    @Getter
    private final Setting<Double> jitterAmount = new Setting<>("Jitter Amount", 0.2D)
            .minimum(0.05D)
            .maximum(0.5D)
            .incrementation(0.05D)
            .describedBy("The amount of jitter.")
            .childOf(enableJitter);

    public AutoClicker() {
        super("Auto Clicker", "Automatically clicks for you.", Category.GHOST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        clickTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    private final Listener<EventPreMotion> eventUpdateEventListener = e -> {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && clickTimer.hasTimeElapsed((long) ((long) (1000 / cps.getValue())), true)) {
            if (block.getValue()) mc.gameSettings.keyBindUseItem.pressed = false;
            mc.clickMouse();
            if (mc.thePlayer.ticksExisted % (10 - blockFrequency.getValue()) == 0 && block.getValue()) {
                mc.gameSettings.keyBindUseItem.pressed = true;
            }
        } else if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            if (enableJitter.getValue()) {
                mc.thePlayer.rotationYaw += (jitterAmount.getValue() + new Random().nextDouble()) * (new Random().nextBoolean() ? -1 : 1);
                mc.thePlayer.rotationPitch += (jitterAmount.getValue() + new Random().nextDouble()) * (new Random().nextBoolean() ? -1 : 1);
            }
        }
    };
}
