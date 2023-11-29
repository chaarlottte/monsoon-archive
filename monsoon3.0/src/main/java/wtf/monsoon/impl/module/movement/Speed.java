package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.*;
import wtf.monsoon.impl.module.movement.speed.*;

import java.util.Arrays;

public class Speed extends Module {

    @Getter
    private final Setting<Mode> mode = new Setting<>("Mode", Mode.VANILLA)
            .describedBy("How to control speed");

    public Speed() {
        super("Speed", "Go faster", Category.MOVEMENT);
        this.setMetadata(() -> {
            if (mode.getValue() == Mode.WATCHDOG) {
                return "Watchdog (" + StringUtil.formatEnum(((WatchdogSpeed) Mode.WATCHDOG.getProcessor()).watchdogMode.getValue()) + ")";
            }

            return StringUtil.formatEnum(mode.getValue());
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mode.getValue().getProcessor().onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mode.getValue().getProcessor().onDisable();
    }

    @EventLink
    private Listener<EventPreMotion> eventPreMotionListener = e -> {
        // System.out.println("a");
    };

    @EventLink
    private Listener<EventUpdateEnumSetting> eventUpdateEnumSettingListener = e -> {
        if(e.getOldValue() instanceof Mode) {
            if (e.getSetting().equals(mode)) {
                ((Mode) e.getOldValue()).getProcessor().onDisable();
                ((Mode) e.getNewValue()).getProcessor().onEnable();
            }
        }
    };

    enum Mode {
        VANILLA(new VanillaSpeed(Wrapper.getModule(Speed.class))),
        WATCHDOG(new WatchdogSpeed(Wrapper.getModule(Speed.class))),
        FUNCRAFT(new FuncraftSpeed(Wrapper.getModule(Speed.class))),
        BLOCKSMC(new BlocksMCSpeed(Wrapper.getModule(Speed.class))),
        YPORT(new YPortSpeed(Wrapper.getModule(Speed.class))),
        VERUS(new VerusSpeed(Wrapper.getModule(Speed.class))),
        NORULES(new NorulesSpeed(Wrapper.getModule(Speed.class))),
        CUBECRAFT(new CubecraftSpeed(Wrapper.getModule(Speed.class))),
        NEGATIVITY(new NegativitySpeed(Wrapper.getModule(Speed.class))),
        VULCAN(new VulcanSpeed(Wrapper.getModule(Speed.class))),
        NCP(new NCPSpeed(Wrapper.getModule(Speed.class)));

        @Getter public ModeProcessor processor;

        Mode(ModeProcessor processor) {
            this.processor = processor;
        }
    }
}