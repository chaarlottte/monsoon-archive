package wtf.monsoon.impl.module.hud;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;

import java.awt.*;

public class InventoryDisplay extends HUDModule {
    public InventoryDisplay() {
        super("Inventory Display", "Displays items in your inventory", 4, 88 + 54);
    }

    @Override
    public void render() {
        /*Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];

        Color cc1, cc2, cc3, cc4;
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }

        RoundedUtils.round(getX() + 1.5f, getY() + 1.5f, getWidth() - 3, getHeight() - 3, 7.5f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));
        RoundedUtils.outline(getX(), getY(), getWidth(), getHeight(), 10, 1f, 2f,
                cc1,
                cc2,
                cc3,
                cc4
        );*/
        RenderUtil.getDefaultHudRenderer(this);


        int slotX = 0, slotY = 0;
        for (int slotIndex = 9; slotIndex < 9 + 27; slotIndex++) {
            Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(slotIndex);

            Color faded = ColorUtil.fadeBetween(10, slotIndex, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
            Color c = ColorUtil.fadeBetween(20, slotIndex * 27, new Color(faded.getRed(), faded.getGreen(), faded.getBlue(), 150), new Color(0xC34B4B4B, true));
            RoundedUtils.round(getX() + 5 + slotX * 18 - 1, getY() + 5 + slotY * 18 - 1, 18, 18, 6, c);

            if (slot.getHasStack()) {
                RenderHelper.enableGUIStandardItemLighting();
                RenderUtil.renderItem(slot.getStack(), getX() + 5 + slotX * 18, getY() + 5 + slotY * 18, 12);
                RenderHelper.disableStandardItemLighting();
            }

            slotX++;
            if (slotX == 9) {
                slotY++;
                slotX = 0;
            }
        }
    }

    @Override
    public void blur() {
        RoundedUtils.glRound(getX(), getY(), getWidth(), getHeight(), 10, Wrapper.getPallet().getBackground().getRGB());
    }

    @Override
    public float getWidth() {
        return 8 + 18 * 9;
    }

    @Override
    public float getHeight() {
        return 8 + 18 * 3;
    }
}
