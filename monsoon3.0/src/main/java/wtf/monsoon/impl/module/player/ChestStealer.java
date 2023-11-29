package wtf.monsoon.impl.module.player;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventUpdate;

public class ChestStealer extends Module {

    public Setting<Double> delay = new Setting<>("Delay", 50D)
            .minimum(0D)
            .maximum(500D)
            .incrementation(5D)
            .describedBy("The delay between each stealing each item");

    public Setting<Boolean> stop = new Setting<>("StopMotion", false)
            .describedBy("Stop the player's motion when stealing");

    public ChestStealer() {
        super("Stealer", "Steals the contents of a chest for your lazy ass", Category.PLAYER);
        this.setMetadata(() -> String.valueOf(delay.getValue()));
    }

    Timer timer = new Timer();


    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (mc.thePlayer != null && (mc.thePlayer.openContainer != null) && ((mc.thePlayer.openContainer instanceof ContainerChest))) {
            ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
            if (stop.getValue()) {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            }
            for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                if ((chest.getLowerChestInventory().getStackInSlot(i) != null) && isGoodChest()) {
                    if (delay.getValue() <= 0 || timer.hasTimeElapsed((long) delay.getValue().doubleValue(), true)) {
                        shiftClick(i, chest.windowId);
                    }
                }
                if (i >= chest.getLowerChestInventory().getSizeInventory()) {
                    mc.thePlayer.closeScreen();
                }
            }
            if (chest.getInventory().isEmpty()) {
                mc.thePlayer.closeScreen();
            }

            int max = (chest.inventorySlots.size() == 90) ? 54 : 27;
            for (int i = 0; i < max; i++) {
                if (chest.getSlot(i).getHasStack()) {
                    return;
                }
            }
            mc.thePlayer.closeScreen();
        }
    };

    public void shiftClick(int slotId, int windowId) {
        ItemStack itemstack = mc.thePlayer.openContainer.slotClick(slotId, 0, 1, mc.thePlayer);
        short short1 = mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory);
        PacketUtil.sendPacketNoEvent(new C0EPacketClickWindow(windowId, slotId, 0, 1, itemstack, short1));
    }


    public boolean isGoodChest() {
        if (mc.currentScreen != null && mc.currentScreen instanceof GuiChest) {
            GuiChest currentChest = (GuiChest) mc.currentScreen;
            if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("game"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("select"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("compass"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("select"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("teleport"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("hypixel"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("play"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("skywars"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("bedwars"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("cakewars"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("lobby"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("mode"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("shop"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("map"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("cosmetic"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("duel"))
                return false;
            else if (currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("menu"))
                return false;
            else return !currentChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase().contains("clique");
        }
        return false;
    }

}
