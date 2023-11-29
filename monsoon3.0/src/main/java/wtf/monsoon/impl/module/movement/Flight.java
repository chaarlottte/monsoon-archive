package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.*;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.*;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.*;
import wtf.monsoon.impl.event.*;
import wtf.monsoon.impl.module.movement.flight.*;

public class Flight extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.MOTION)
            .describedBy("The mode of the flight.");

    public Flight() {
        super("Flight", "Fly", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mode.getValue().getProcessor().onEnable();
        mode.getValue().getProcessor().setParentModule(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mode.getValue().getProcessor().onDisable();
    }

    @EventLink
    private Listener<EventUpdateEnumSetting> eventUpdateEnumSettingListener = e -> {
        if(e.getOldValue() instanceof Mode) {
            if (e.getSetting().equals(mode)) {
                ((Mode) e.getOldValue()).getProcessor().onDisable();
                ((Mode) e.getNewValue()).getProcessor().onEnable();
            }
        }
    };

    private enum Mode {
        MOTION(new MotionFlight(Wrapper.getModule(Flight.class))),
        FUNCRAFT(new FuncraftFlight(Wrapper.getModule(Flight.class))),
        VERUS(new VerusFlight(Wrapper.getModule(Flight.class))),
        ZONECRAFT(new ZonecraftFlight(Wrapper.getModule(Flight.class))),
        NORULES(new NorulesFlight(Wrapper.getModule(Flight.class))),
        CUBECRAFT(new CubecraftFlight(Wrapper.getModule(Flight.class))),
        PACKET(new PacketFlight(Wrapper.getModule(Flight.class))),
        NEGATIVITY(new NegativityFlight(Wrapper.getModule(Flight.class))),
        VANILLA(new VanillaFlight(Wrapper.getModule(Flight.class))),
        VULCAN(new VulcanFlight(Wrapper.getModule(Flight.class))),
        UNPATCHABLE_AIRJUMP(new UnpatchableAirjumpFlight(Wrapper.getModule(Flight.class))),
        UPDATED_N_C_P(new UpdatedNCPFlight(Wrapper.getModule(Flight.class)));

        @Getter
        public ModeProcessor processor;

        Mode(ModeProcessor processor) {
            this.processor = processor;
        }
    }
}
