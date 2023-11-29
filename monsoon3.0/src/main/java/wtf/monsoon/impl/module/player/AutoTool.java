package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class AutoTool extends Module {

    public AutoTool() {
        super("Auto Tool", "Automatically switch to the correct tool when mining a block.", Category.PLAYER);
    }

    @EventLink
    private Listener<EventPreMotion> pre = e -> {
        if(Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null) {
            float bestStr = 0.0F;
            int itemToUse = -1;

            for(int i = 0; i < 9; i++) {
                ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
                if (itemStack == null) continue;

                if(!(itemStack.getItem() instanceof ItemSword)) continue;

                ItemSword item = (ItemSword) itemStack.getItem();

                if (item.attackDamage > bestStr) {
                    bestStr = item.attackDamage;
                    itemToUse = i;
                }
            }
            if(itemToUse != -1) mc.thePlayer.inventory.currentItem = itemToUse;
            return;
        }

        if(!mc.gameSettings.keyBindAttack.pressed || mc.objectMouseOver == null) return;

        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null) return;

        int itemToUse = getBestToolSlot(pos);
        if(itemToUse == -1) return;

        mc.thePlayer.inventory.currentItem = itemToUse;
    };

    private int getBestToolSlot(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();

        float bestStr = 1.0F;
        int itemTouse = -1;

        for(int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) continue;

            if (itemStack.getStrVsBlock(block) > bestStr) {
                bestStr = itemStack.getStrVsBlock(block);
                itemTouse = i;
            }
        }

        return itemTouse;
    }

    @EventLink
    private Listener<EventPacket> packet = e -> {

    };

}
