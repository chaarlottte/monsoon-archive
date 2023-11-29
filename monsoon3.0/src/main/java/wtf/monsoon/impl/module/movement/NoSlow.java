package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockChest;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPostMotion;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class NoSlow extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.VANILLA)
            .describedBy("How to prevent slow down");

    private boolean sneakState = false;
    private boolean sprintState = false;

    public NoSlow() {
        super("No Slow", "Doesn't slow you down", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (sneakState && !mc.thePlayer.isSneaking()) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            sneakState = false;
        }

        if (sprintState && mc.thePlayer.isSprinting()) {
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            sprintState = false;
        }
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (mode.getValue()) {
            case NCP:
                if(mc.thePlayer.isUsingItem() && player.isMoving()) {
                    if(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
                        PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    else
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                break;
            case WATCHDOG:
                if (player.isMoving() && mc.thePlayer.isUsingItem())
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
        }
    };

    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        switch (mode.getValue()) {
            case NCP:
                if(mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)
                    PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                break;
            case VANILLA:
                break;
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if(mc.thePlayer == null || mc.theWorld == null) return;
        switch (mode.getValue()) {
            case WATCHDOG:
                /*MovingObjectPosition mop = mc.thePlayer.rayTrace(4, mc.thePlayer.ticksExisted);
                if(e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                    Aura aura = Wrapper.getModule(Aura.class);
                    if(mc.thePlayer.isUsingItem()
                            && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                            && !(aura.isEnabled() && aura.getTarget() != null)
                            && !(mop.getBlockPos() != null && mc.theWorld.getBlockState(mop.getBlockPos()).getBlock() instanceof BlockChest)
                            && player.isMoving()) {
                        e.setCancelled(true);
                    }
                }*/
                break;
            case VANILLA:
                break;
        }
    };

    enum Mode {
        VANILLA("Vanilla"),
        WATCHDOG("Watchdog"),
        NCP("NCP");

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
