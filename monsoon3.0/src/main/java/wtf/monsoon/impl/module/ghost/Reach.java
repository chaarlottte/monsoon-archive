package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventUpdate;

public class Reach extends Module {

    Setting<Boolean> randomized = new Setting<>("Randomized", true)
            .describedBy("Whether the reach is randomized [max-min]");

    Setting<Double> minReach = new Setting<>("MinReach", 3.0D)
            .minimum(0.1D)
            .maximum(8.0D)
            .incrementation(0.1D)
            .describedBy("Maximum hit reach");

    Setting<Double> maxReach = new Setting<>("MaxReach", 3.0D)
            .minimum(0.1D)
            .maximum(8.0D)
            .incrementation(0.1D)
            .describedBy("Minimum hit reach");

    Setting<Double> vBlockReach = new Setting<>("BlockReach", 3.0D)
            .minimum(0.1D)
            .maximum(8.0D)
            .incrementation(0.1D)
            .describedBy("Total block reach");

    public double hitReach, blockReach;

    public Reach() {
        super("Reach", "Extends your hit and block reach", Category.GHOST);
    }

    @EventLink
    private final Listener<EventUpdate> updateListener = e -> {
        hitReach = randomized.getValue() ? ((Math.random() * (maxReach.getValue() - minReach.getValue())) + minReach.getValue()) : maxReach.getValue();
        blockReach = vBlockReach.getValue();
    };
}
