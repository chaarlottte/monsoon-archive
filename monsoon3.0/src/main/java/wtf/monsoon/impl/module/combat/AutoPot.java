package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPreMotion;

public class AutoPot extends Module {

    private final Setting<Double> healthToUseRegen = new Setting<>("Maximum Health", 10D)
            .minimum(1D)
            .maximum(20D)
            .incrementation(1D)
            .describedBy("At what health to use a regeneration pot.");

    public AutoPot() {
        super("Auto Pot", "Automatically throw useful potions at your feet.", Category.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null &&
                    mc.thePlayer.inventory.getStackInSlot(i).getItem() != null &&
                    mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemPotion) {

                ItemPotion potion = (ItemPotion) mc.thePlayer.inventory.getStackInSlot(i).getItem();
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if (!ItemPotion.isSplash(stack.getMetadata())) {
                    continue;
                }

                boolean shouldSplash = false;

                for (PotionEffect potionEffect : potion.getEffects(stack.getMetadata())) {
                    if (isValidEffect(potionEffect)) {
                        shouldSplash = true;
                    }
                }

                if (shouldSplash) {
                    switchToSlot(i);
                    mc.thePlayer.rotationPitchHead = 90;

                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
                    // e.setPitch(90);
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(stack, new BlockPos(-1, -1, -1)));

                    switchToSlot(mc.thePlayer.inventory.currentItem);
                }
            }
        }
    };

    private void switchToSlot(int slot) {
        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
    }

    public boolean isValidEffect(PotionEffect potionEffect) {
        switch (potionEffect.getPotionID()) {
            case 1: // Potion.moveSpeed
                return !mc.thePlayer.isPotionActive(Potion.moveSpeed);
            case 10: // Potion.regeneration
                return !mc.thePlayer.isPotionActive(Potion.regeneration) && mc.thePlayer.getHealth() <= healthToUseRegen.getValue();
            case 6: // Potion.instant_health
            case 21: // Potion.healthBoost
                return mc.thePlayer.getHealth() <= healthToUseRegen.getValue();
            default:
                return false;
        }
    }

}
