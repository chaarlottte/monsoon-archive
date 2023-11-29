package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.PotionEffect;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.combat.AutoPot;

import java.util.*;

public class InventoryManager extends Module {

    private final Timer timer = new Timer();

    private int lastSlot;

    public Setting<Double> delay = new Setting<>("Delay", 50.0)
            .minimum(0D)
            .maximum(500D)
            .incrementation(5D)
            .describedBy("The delay between each inventory update.");

    public Setting<Boolean> open = new Setting<>("Open Inventory", false)
            .describedBy("Open the inventory.");

    public Setting<Boolean> spoof = new Setting<>("Spoof Inventory", false)
            .describedBy("Spoof having your inventory open server-side. Can cause desyncing.");

    public Setting<Boolean> whileNotMoving = new Setting<>("While not Moving", false)
            .describedBy("Only manage your inventory if you aren't moving.");

    public Setting<Boolean> moveBlocksToHotbar = new Setting<>("Move blocks to hotbar", true)
            .describedBy("Whether to move blocks to your hotbar.");

    public Setting<Boolean> movePotionsToHotbar = new Setting<>("Move potions to hotbar", true)
            .describedBy("Whether to move potions to your hotbar.");

    public List<String> junk = Arrays.asList("stick", "egg", "string", "cake", "mushroom", "flint", "dyePowder", "feather", "chest", "snowball", "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "piston", "snow", "bottle", "poison", "shield", "web", "chest", "bucket");

    private boolean inventoryOpen = false;

    public InventoryManager() {
        super("Manager", "Manages your Inventory", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
        lastSlot = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        lastSlot = 0;
    }

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {

        if (timer.hasTimeElapsed((long) delay.getValue().doubleValue(), true) /*&& !Monsoon.INSTANCE.manager.getModuleByClass(AutoArmor.class).isRunning*/) {
            if (isInventoryOpen() && (!player.isMoving() || !whileNotMoving.getValue())) {
                if ((mc.currentScreen == null || (mc.currentScreen instanceof GuiContainer && ((GuiContainer) mc.currentScreen).inventorySlots == mc.thePlayer.inventoryContainer))) {
                    for (int i = 9; i < 45; i++) {
                        if (lastSlot >= 45) lastSlot = 0;
                        while (i <= lastSlot) i++;
                        lastSlot = i;
                        Slot slot;
                        try {
                            slot = mc.thePlayer.inventoryContainer.getSlot(i);
                        } catch (Exception ex) {
                            continue;
                        }

                        if (!slot.getHasStack()) continue;

                        ItemStack stack = slot.getStack();
                        Item item = stack.getItem();

                        if (item instanceof ItemSword && isBestSword(i)) {
                            openInv();
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 2, mc.thePlayer);
                            closeInv();
                            return;
                        } else if (item instanceof ItemSword && !isBestSword(i)) {
                            dropItem(i);
                        }

                        if (item instanceof ItemTool && !isBestTool(i)) {
                            //Monsoon.sendMessage("not best tool " + stack.getDisplayName());
                            dropItem(i);
                            return;
                        }

                        if (isJunk(i) && !(item instanceof ItemArmor)) {
                            // Monsoon.sendMessage("junk " + stack.getDisplayName());
                            dropItem(i);
                            return;
                        }

                        if(item instanceof ItemPotion && movePotionsToHotbar.getValue()) {

                            ItemPotion potion = (ItemPotion) item;

                            if(!ItemPotion.isSplash(stack.getMetadata())) continue;

                            boolean shouldMove = false;

                            for(PotionEffect potionEffect : potion.getEffects(stack.getMetadata())) {
                                if(Wrapper.getModule(AutoPot.class).isValidEffect(potionEffect)) shouldMove = true;
                            }

                            if(shouldMove) {
                                openInv();
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                                closeInv();
                            }
                            return;
                        }

                        int blocksInHotbar = 0;

                        for (int j = 0; j < 9; j++) {
                            if(blocksInHotbar > 128) continue;
                            try {
                                if (!mc.thePlayer.inventoryContainer.getSlot(j + 36).getHasStack()) continue;
                                if(mc.thePlayer.inventoryContainer.getSlot(j + 36).getStack().getItem() instanceof ItemBlock) {
                                    blocksInHotbar += mc.thePlayer.inventoryContainer.getSlot(j + 36).getStack().stackSize;
                                }
                            } catch (Exception ignored) {
                            }
                        }

                        if(item instanceof ItemBlock && stack.stackSize > 8 && i < 36 && moveBlocksToHotbar.getValue() && blocksInHotbar < 96)  {
                            openInv();
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                            closeInv();
                            return;
                        }

                        if(item.getItemStackLimit() > 1 && i < 36) {
                            for (int j = 0; j < 9; j++) {
                                try {
                                    if (!mc.thePlayer.inventoryContainer.getSlot(j + 36).getHasStack()) continue;
                                    ItemStack jStack = mc.thePlayer.inventoryContainer.getSlot(j + 36).getStack();
                                    Item jItem = jStack.getItem();
                                    if(jItem.getClass().equals(item.getClass()) && jStack.stackSize < 64) {
                                        openInv();
                                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                                        closeInv();
                                        return;
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }
            } else {
                lastSlot = 0;
            }
        }
    };

    private boolean isJunk(int slotid) {
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(slotid);
        ItemStack stack = slot.getStack();

        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof ItemBanner) return true;
            if (stack.getDisplayName().toLowerCase().contains("tnt")) return true;
            if (stack.getDisplayName().toLowerCase().contains("bucket")) return true;
            if (stack.getDisplayName().toLowerCase().contains("bottle")) return true;
            for (String shortName : junk) {
                if (stack.getDisplayName().toLowerCase().contains(shortName)) return true;
                if (item.getUnlocalizedName().toLowerCase().contains(shortName)) return true;
            }
        }

        return false;
    }

    private boolean isBestTool(int slotid) {
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(slotid);
        ItemStack compareStack = slot.getStack();

        for (int i = 9; i < 45; i++) {
            Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack2 = slot2.getStack();

            if (stack2 != null && compareStack != stack2 && stack2.getItem() instanceof ItemTool) {
                ItemTool item = (ItemTool) stack2.getItem();
                ItemTool compare = (ItemTool) compareStack.getItem();
                if (item.getClass() == compare.getClass()) {
                    if (compare.getStrVsBlock(stack2, preferredBlock(item.getClass())) <= item.getStrVsBlock(compareStack, preferredBlock(compare.getClass())))
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isBestSword(int slotid) {
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(slotid);
        ItemStack compareStack = slot.getStack();
        for (int i = 9; i < 45; i++) {
            Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot2.getStack();

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemSword) {
                if (compareStack.getItem() instanceof ItemSword) {
                    ItemSword item = (ItemSword) stack.getItem();
                    ItemSword compare = (ItemSword) compareStack.getItem();
                    if (item.getClass() == compare.getClass()) {
                        if (compare.attackDamage + getSwordStrength(compareStack) <= item.attackDamage + getSwordStrength(stack))
                            return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean dropItem(int slot) {
        openInv();
        Slot slo2t = mc.thePlayer.inventoryContainer.getSlot(slot);
        ItemStack stack = slo2t.getStack();
        // Monsoon.sendMessage("dropped " + stack.getDisplayName());
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
        closeInv();
        return true;
    }

    private Block preferredBlock(Class clazz) {
        return clazz == ItemPickaxe.class ? Blocks.cobblestone : clazz == ItemAxe.class ? Blocks.log : Blocks.dirt;
    }

    private float getSwordStrength(ItemStack stack) {
        return (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F) + (!(stack.getItem() instanceof ItemSword) ? 0.0F : (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack));
    }

    private int checkProtection(ItemStack item) {
        return EnchantmentHelper.getEnchantmentLevel(0, item);
    }

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if(e.getPacket() instanceof C16PacketClientStatus) {
            C16PacketClientStatus packet = (C16PacketClientStatus) e.getPacket();
            if(packet.getStatus().equals(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)) {
                inventoryOpen = true;
            }
        }

        if(e.getPacket() instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow packet = (C0DPacketCloseWindow) e.getPacket();
            if(packet.getWindowId() == mc.thePlayer.inventoryContainer.windowId) {
                inventoryOpen = false;
            }
        }
    };

    private void openInv() {
        if (!inventoryOpen && spoof.getValue()) PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        inventoryOpen = true;
    }

    private void closeInv() {
        if (inventoryOpen && spoof.getValue()) PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
        inventoryOpen = false;
    }

    private boolean isInventoryOpen() {
        if(open.getValue()) return mc.currentScreen instanceof GuiInventory;
        //else if(spoof.getValue()) return this.inventoryOpen;
        else return true;
    }

}
