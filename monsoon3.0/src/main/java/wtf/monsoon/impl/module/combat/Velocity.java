package wtf.monsoon.impl.module.combat;

import com.mojang.authlib.GameProfile;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.movement.Speed;

import java.util.Objects;
import java.util.UUID;

public class Velocity extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.CANCEL)
            .describedBy("The mode of velocity.");

    private final Setting<Integer> horMod = new Setting<>("Horizontal", 0)
            .minimum(0)
            .maximum(100)
            .incrementation(1)
            .describedBy("Horizontal velocity modifier.")
            .visibleWhen(() -> mode.getValue().equals(Mode.CANCEL));

    private final Setting<Integer> vertMod = new Setting<>("Vertical", 0)
            .minimum(0)
            .maximum(100)
            .incrementation(1)
            .describedBy("Vertical velocity modifier.")
            .visibleWhen(() -> mode.getValue().equals(Mode.CANCEL));

    private final Setting<Double> strength = new Setting<>("Strength", 3D)
            .minimum(1D)
            .maximum(10D)
            .incrementation(1D)
            .describedBy("The strength of velocity.")
            .visibleWhen(() -> mode.getValue().equals(Mode.DRAG_CLICK));

    private boolean receivedVelocity;

    public Velocity() {
        super("Velocity", "Take no knockback.", Category.COMBAT);

        this.setMetadata(() -> {
            switch (mode.getValue()) {
                case DRAG_CLICK: return StringUtil.formatEnum(mode.getValue()) + ", " + strength.getValue();
                case WATCHDOG: return "Watchdog";
                case CANCEL: return horMod.getValue() + "% " + vertMod.getValue() + "%";
            }

            return "";
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        receivedVelocity = false;
    }

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();

            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                int amount = ((packet.getMotionX() + packet.getMotionY() + packet.getMotionZ()) / 3) / 200;
                Wrapper.getSexToyManager().vibrate(amount);
            }

            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                receivedVelocity = true;
            }

            switch (mode.getValue()) {
                case CANCEL:
                    if(horMod.getValue() == 0 && vertMod.getValue() == 0) e.setCancelled(true);

                    packet.setMotionX((int) ((packet.getMotionX() * 0.01) * horMod.getValue()));
                    packet.setMotionY((int) ((packet.getMotionY() * 0.01) * vertMod.getValue()));
                    packet.setMotionZ((int) ((packet.getMotionZ() * 0.01) * horMod.getValue()));
                    break;
                case WATCHDOG:

                    if(!(Wrapper.getModule(Speed.class).isEnabled() && Wrapper.getModule(Speed.class).getMode().equals(Mode.WATCHDOG))) {
                        if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                            e.setCancelled(true);
                            // mc.thePlayer.motionY = packet.getMotionY() / 8000.0D;
                            mc.thePlayer.motionY = packet.getMotionY() / 8000.0D;
                        }
                    }

                    break;
            }
        } else if (e.getPacket() instanceof S27PacketExplosion) {
            switch (mode.getValue()) {
                case CANCEL:
                    e.setCancelled(true);
                    break;

                case WATCHDOG:
                    if(mc.thePlayer.hurtTime > 0) {
                        e.setCancelled(true);
                        mc.thePlayer.motionY += 0.001 - Math.random() / 100f;
                    }

                    break;
            }
        }
    };

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if (mode.getValue() == Mode.DRAG_CLICK) {
            if (mc.thePlayer.hurtTime == 10 && receivedVelocity) {
                for (int i = 0; i < 10; i++) {
                    EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(UUID.randomUUID(), "a"));

                    PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                    PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(fakePlayer, C02PacketUseEntity.Action.ATTACK));
                    mc.thePlayer.motionX *= (1. / strength.getValue());
                    mc.thePlayer.motionZ *= (1. / strength.getValue());
                }
            }
        }

        if (mc.thePlayer.hurtTime == 0) {
            receivedVelocity = false;
        }
    };

    enum Mode {
        CANCEL, WATCHDOG, DRAG_CLICK
    }

}
