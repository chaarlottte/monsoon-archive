package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Flight;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AntiVoid extends Module {

    Setting<Mode> mode = new Setting<>("Mode", Mode.FLAG);
    Setting<Float> flagAfterBlocks = new Setting<>("AfterBlocks", 7f)
            .minimum(1f)
            .maximum(20f)
            .incrementation(0.1f);

    private ArrayList<Packet> packets = new ArrayList<>();
    private double flyHeight;
    private Timer timer = new Timer();
    private boolean flagged;

    double xPos, yPos, zPos;

    boolean shouldHop;

    public AntiVoid() {
        super("Anti Void", "Avoid the void.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        shouldHop = false;
        packets.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        packets.clear();
    }

    @EventLink
    private Listener<EventPreMotion> pre = e -> {
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
        if(mc.thePlayer.onGround) shouldHop = true;
        switch (mode.getValue()) {
            case FLAG:
                if(mc.thePlayer.fallDistance > flagAfterBlocks.getValue()) PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                break;
            case JUMP:
                if(mc.thePlayer.fallDistance > flagAfterBlocks.getValue()) mc.thePlayer.jump();
                break;
            case GROUND:
                if (!isBlockUnder() && mc.thePlayer.fallDistance > flagAfterBlocks.getValue() && mc.thePlayer.motionY < 0) {
                    e.setOnGround(true);
                }
                break;
            case POSITION:
                if(mc.thePlayer.fallDistance > flagAfterBlocks.getValue()) e.setY(e.getY() + mc.thePlayer.fallDistance);
                break;
            case TEST:
                if (!isBlockUnder() && mc.thePlayer.fallDistance > 5) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 20, mc.thePlayer.posZ, false));
                }
                if(mc.thePlayer.onGround) {
                    xPos = mc.thePlayer.posX - mc.thePlayer.motionX;
                    yPos = mc.thePlayer.posY;
                    zPos = mc.thePlayer.posZ - mc.thePlayer.motionZ;
                }
                break;
            case VOID_HOP:
                if (mc.thePlayer.fallDistance > flagAfterBlocks.getValue() && !isBlockUnder() && mc.thePlayer.fallDistance > 6 && mc.thePlayer.ticksExisted % 5 == 0 && !mc.thePlayer.isDead && shouldHop && mc.thePlayer.posY < 30) {
                    mc.thePlayer.motionY = 3f;
                    shouldHop = false;
                }
                break;
            case VULCAN:
                if (mc.thePlayer.fallDistance > flagAfterBlocks.getValue() && !isBlockUnder() && mc.thePlayer.motionY < 0 && !mc.thePlayer.isDead) {
                    PlayerUtil.sendClientMessage("vulcan antivoiding");
                    int quotient = (int) (e.getY() / (1.0 / 64.0));
                    e.setY((1.0 / 64.0) * quotient);
                    e.setOnGround(true);
                }
                break;
            case NOBO_LION_TASTE_ANTIVOID:
                this.setMetadata(() -> "$$ Blink Bypass $$");
                updateFlyHeight();
                if(mc.thePlayer.isCollidedHorizontally) {
                    flagged = false;
                }
                if (flyHeight > 40 && mc.thePlayer.fallDistance > 0 && !Wrapper.getModule(Flight.class).isEnabled() && !packets.isEmpty() && !flagged && mc.thePlayer.motionY < 0) {
                    Collections.reverse(packets);
                    PacketUtil.sendPacketNoEvent(new C18PacketSpectate(mc.thePlayer.getUniqueID()));
                    for (Packet value : packets) {
                        PacketUtil.sendPacketNoEvent(value);
                    }
                    packets.clear();
                    Wrapper.getNotifManager().notify(NotificationType.INFO, "BLINK", "BLINKING ANTIVOIDING!!!! (NOVOLINE REFERENCE)");
                    PlayerUtil.sendClientMessage("BLINKING ANTIVOIDING!!!! (NOVOLINE REFERENCE)");
                }
                break;
        }
    };

    @EventLink
    private Listener<EventPacket> packet = e -> {
        if(e.direction.equals(EventPacket.Direction.SEND)) {
            switch (mode.getValue()) {
                case TEST:
                    break;
                case NOBO_LION_TASTE_ANTIVOID:
                    Packet packet = (Packet) e.getPacket();

                    if (packet instanceof C03PacketPlayer) {
                        if (!(flyHeight > 40) || !(mc.thePlayer.fallDistance > 0) || Wrapper.getModule(Flight.class).isEnabled()) {

                            packets.add(packet);
                            if (packets.size() > 5) {
                                packets.remove(0);
                            }
                        } else if (!isBlockUnder() && mc.thePlayer.motionY < 0) {
                            e.setCancelled(true);
                        }
                    }
                    break;
            }
        }
    };

    public void updateFlyHeight() {
        double h = 1.0D;
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.0625D, 0.0625D, 0.0625D);

        for (this.flyHeight = 0.0D; this.flyHeight < mc.thePlayer.posY; this.flyHeight += h) {
            AxisAlignedBB nextBox = box.offset(0.0D, -this.flyHeight, 0.0D);
            if (mc.theWorld.checkBlockCollision(nextBox)) {
                if (h < 0.0625D) {
                    break;
                }

                this.flyHeight -= h;
                h /= 2.0D;
            }
        }

    }

    private boolean isBlockUnder() {
        for (int offset = (int) mc.thePlayer.posY; offset > 0; offset -= 1) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -(mc.thePlayer.posY - offset), 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }

    enum Mode {
        FLAG, JUMP, GROUND, POSITION, TEST, VOID_HOP, VULCAN, NOBO_LION_TASTE_ANTIVOID
    }
}
