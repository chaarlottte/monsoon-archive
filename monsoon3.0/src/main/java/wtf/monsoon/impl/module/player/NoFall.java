package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Flight;

public class NoFall extends Module {

    public Setting<Mode> mode = new Setting<>("Mode", Mode.GROUNDSPOOF)
            .describedBy("How to prevent fall damage");

    public Setting<Double> fallSpeed = new Setting<>("Fall Speed", 0.5)
            .minimum(0.1)
            .maximum(1.0)
            .incrementation(0.05)
            .describedBy("Speed at which you fall")
            .visibleWhen(() -> mode.getValue() == Mode.VULCAN);

    public NoFall() {
        super("No Fall", "Take no fall damage", Category.PLAYER);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch(mode.getValue()) {
            case EDIT:
                if (mc.thePlayer.fallDistance > 3) e.setOnGround(true);
                break;
            case VERUS:
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3 && !Wrapper.getModule(Flight.class).isEnabled()) {
                    mc.thePlayer.motionY = 0.0;

                    mc.thePlayer.motionX *= 0.7;
                    mc.thePlayer.motionZ *= 0.7;

                    mc.thePlayer.fallDistance = 0.0f;
                    mc.thePlayer.setPosition(e.getX(), e.getY(), e.getZ());
                    e.setOnGround(true);
                }
                break;
            case PACKET:
                if (mc.thePlayer.fallDistance > 3) {
                    PacketUtil.sendPacket(new C03PacketPlayer(true));
                    mc.thePlayer.fallDistance = 0;
                }
                break;
            case HYPIXEL:
                // e.setOnGround(true);
                e.setY(256 + Math.random() * 10);
                break;
            case VULCAN:
                if(mc.thePlayer.fallDistance >= 3.7) {
                    e.setOnGround(true);
                    mc.thePlayer.motionY = -fallSpeed.getValue();
                    mc.thePlayer.fallDistance = 0;
                }
                break;
        }
    };

    @EventLink
    public final Listener<EventMove> eventMove = e -> {
        switch(mode.getValue()) {
            case YCHANGE:
                if (mc.thePlayer.fallDistance > 3) {
                    e.setY(e.getY() - (mc.thePlayer.fallDistance - 0.5));
                    PacketUtil.sendPacket(new C03PacketPlayer(true));
                }
                break;
            case EDIT:
                break;
        }
    };

    @EventLink
    public final Listener<EventPacket> eventUpdateListener = e -> {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.fallDistance > 3) {
            switch (mode.getValue()) {
                case GROUNDSPOOF:
                    mc.thePlayer.onGround = true;
                    break;
                case POSITION:
                    mc.thePlayer.onGround = true;
                    PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - (mc.thePlayer.fallDistance - 1), mc.thePlayer.posZ, true));
                    break;
            }
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (e.getPacket() instanceof C03PacketPlayer) {
            if (mc.thePlayer.fallDistance > 3) {
                if (mode.getValue() == Mode.INTERCEPT) {
                    ((C03PacketPlayer) e.getPacket()).onGround = true;
                }
            }
        }

        switch (mode.getValue()) {
            case INTERCEPT:
                if (e.getPacket() instanceof C03PacketPlayer)
                    if (mc.thePlayer.fallDistance > 3)
                        ((C03PacketPlayer) e.getPacket()).onGround = true;
                break;
            case EDIT:
                break;
        }
    };

    enum Mode {
        //this, "GroundSpoof", "GroundSpoof", "Intercept", "Packet", "Position", "Edit", "MLG", "Verus", "Hypixel"
        GROUNDSPOOF,
        INTERCEPT,
        PACKET,
        POSITION,
        EDIT,
        YCHANGE,
        MLG,
        VERUS,
        HYPIXEL,
        VULCAN
    }

}
