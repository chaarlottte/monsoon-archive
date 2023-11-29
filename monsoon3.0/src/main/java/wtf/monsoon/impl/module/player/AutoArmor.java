package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;

public class AutoArmor extends Module {

    public boolean isRunning;

    Timer timer = new Timer();
    public Setting<Float> delay = new Setting<>("Delay", 50f)
            .minimum(0f)
            .maximum(500f)
            .incrementation(5f)
            .describedBy("The delay between each armor swap.");

    public Setting<Boolean> open = new Setting<>("Open Inventory", false)
            .describedBy("If enabled, the inventory will be opened");

    public Setting<Boolean> spoof = new Setting<>("Spoof Inventory", false)
            .describedBy("Spoof having your inventory open server-side. Can cause desyncing.");

    public Setting<Boolean> whileNotMoving = new Setting<>("While not Moving", false)
            .describedBy("Only manage your inventory if you aren't moving.");

    private boolean inventoryOpen = false;

    public AutoArmor() {
        super("Auto Armor", "Automatically equips armor", Category.PLAYER);
    }

    public void onEnable() {
        super.onEnable();
        isRunning = false;
    }

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {

        if (!isChestInventory() && isInventoryOpen() && (!player.isMoving() || !whileNotMoving.getValue())) {
            isRunning = true;
            for (int i = 0; i < 36; i++) {
                ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                if (item != null && item.getItem() instanceof ItemArmor) {
                    ItemArmor armour = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(i).getItem();
                    int equippedReduction = 0;
                    int equippedDur = 0;
                    int checkReduction = 0;
                    if (mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType) != null) {
                        ItemArmor equippedArmor = (ItemArmor) mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType).getItem();
                        ItemStack equippedItemStack = mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType);
                        equippedReduction = equippedArmor.getArmorMaterial().getDamageReductionAmount(armour.armorType);
                        equippedReduction = checkProtection(mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType)) + equippedReduction;
                        equippedDur = equippedItemStack.getItemDamage();
                        checkReduction = armour.getArmorMaterial().getDamageReductionAmount(armour.armorType);
                        checkReduction = checkProtection(mc.thePlayer.inventory.getStackInSlot(i)) + checkReduction;
                    }

                    if (getFreeSlot() != -1) {
                        if (mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType) != null) {
                            if (checkReduction > equippedReduction || (checkReduction == equippedReduction && item.getItemDamage() < equippedDur)) {

                                if (i < 9) {
                                    i = i + 36;
                                }
                                openInv();
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 5 + armour.armorType, 0, 4, mc.thePlayer);
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                                closeInv();
                            } else {
                                if (i < 9) {
                                    i = i + 36;
                                }
                                openInv();
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                                closeInv();
                                return;
                            }
                        }
                    }
                    if (mc.thePlayer.inventory.getStackInSlot(39 - armour.armorType) == null && timer.hasTimeElapsed((long) delay.getValue().doubleValue(), true)) {
                        if (i < 9) {
                            i = i + 36;
                        }
                        openInv();
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        closeInv();
                    }
                }
            }
            isRunning = false;
        }

    };

    public int getFreeSlot() {
        for (int i = 35; i > 0; i--) {
            ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
            if (item == null) {
                return i;
            }
        }
        return -1;

    }

    public static int checkProtection(ItemStack item) {
        return EnchantmentHelper.getEnchantmentLevel(0, item);
    }

    public boolean isChestInventory() {
        return mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest;
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
