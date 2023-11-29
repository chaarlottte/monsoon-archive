package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

public class HighJump extends Module {

    public Setting<Mode> mode = new Setting<>("Mode", Mode.HYPIXEL)
            .describedBy("How to prevent fall damage");

    private final Setting<Boolean> onSpace = new Setting<>("On Space", false)
            .describedBy("If you should only go up while holding space.")
            .visibleWhen(() -> mode.getValue() == Mode.VERUS);

    private boolean shouldJump;

    public HighJump() {
        super("High Jump", "Jump higher.", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        shouldJump = false;
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (mode.getValue()) {
            case VERUS:
                if (mc.gameSettings.keyBindJump.isKeyDown() || !onSpace.getValue()) {
                    player.setSpeed(0);
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        mc.thePlayer.jump();
                    }
                }
                break;
            case HYPIXEL:
                if (shouldJump) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY += 2.75f;

                    player.setSpeed(1f);
                }
                if (mc.thePlayer.onGround && shouldJump) {
                    shouldJump = false;
                }
                break;
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (e.getPacket() instanceof S27PacketExplosion) {
            if (mc.thePlayer.hurtTime > 0) {
                shouldJump = true;
            }
        }
    };

    enum Mode {
        HYPIXEL,
        VERUS
    }

}
