package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

public class Criticals extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.PACKET)
            .describedBy("The mode of the criticals.");

    public Criticals() {
        super("Criticals", "Always land a critical hit.", Category.COMBAT);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if(e.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
            if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                switch (mode.getValue()) {
                    case NCP:
                        if(packet.getEntityFromWorld(mc.theWorld) instanceof EntityPlayer && mc.thePlayer.onGround) {
                            EntityPlayer target = (EntityPlayer) packet.getEntityFromWorld(mc.theWorld);
                            if(target.hurtTime < 3) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1E-4, mc.thePlayer.posZ, false));
                            }
                        }
                        break;
                    case PACKET:
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.02, mc.thePlayer.posZ, false));
                        break;
                }
            }
        }
    };

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        EntityLivingBase target = Wrapper.getModule(Aura.class).getTarget();
        switch (mode.getValue()) {
            case EDIT:
                if(target == null) return;
                if(target.hurtTime == 3) {
                    e.setY(e.getY() + 0.08);
                    e.setOnGround(false);
                } else if(target.hurtTime < 3) {
                    e.setY(e.getY() + 1E-3);
                    e.setOnGround(false);
                }
                break;
            case NCP:
                break;
        }
    };

    enum Mode {
        PACKET("Packet"),
        EDIT("Edit"),
        NCP("NCP"),
        UPDATED_NCP("Updated NCP");

        String modeName;

        Mode(String modeName) {
            this.modeName = modeName;
        }

        @Override
        public String toString() {
            return this.modeName;
        }
    }

}
