package wtf.monsoon.impl.module.movement;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.player.ChestStealer;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class InventoryMove extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.NORMAL)
            .describedBy("The mode to use.");

    private boolean shouldBlink = false;
    private LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();

    public InventoryMove() {
        super("Inventory Move", "Inventory move", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        shouldBlink = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        shouldBlink = false;
    }

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if(Wrapper.getModule(ChestStealer.class).isEnabled() && Wrapper.getModule(ChestStealer.class).stop.getValue() && mc.currentScreen instanceof GuiChest) return;

        if(mc.currentScreen != null)
            this.shouldBlink = true;
        else
            this.shouldBlink = false;

        if (mc.currentScreen != null) {
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && !(this.mc.currentScreen instanceof GuiChat))
                mc.thePlayer.rotationYaw += 8F;
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && !(this.mc.currentScreen instanceof GuiChat))
                mc.thePlayer.rotationYaw -= 8F;
            if (Keyboard.isKeyDown(Keyboard.KEY_UP) && !(this.mc.currentScreen instanceof GuiChat))
                mc.thePlayer.rotationPitch -= 8F;
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && !(this.mc.currentScreen instanceof GuiChat))
                mc.thePlayer.rotationPitch += 8F;

            KeyBinding[] moveKeys = new KeyBinding[]{ this.mc.gameSettings.keyBindRight, this.mc.gameSettings.keyBindLeft, this.mc.gameSettings.keyBindBack, this.mc.gameSettings.keyBindForward, this.mc.gameSettings.keyBindJump, this.mc.gameSettings.keyBindSprint };
            if (this.mc.currentScreen == null || (this.mc.currentScreen instanceof GuiChat)) {
                for (KeyBinding bind : moveKeys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            } else {
                for (KeyBinding key : moveKeys) {
                    key.pressed = Keyboard.isKeyDown(key.getKeyCode());
                }
            }
        }

        switch(mode.getValue()) {
            case WATCHDOG:
                //if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChest))
                    //PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(new Random().nextInt(9999999)));
                break;
            case NORMAL:
                break;
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        switch(mode.getValue()) {
            case WATCHDOG:
                break;
            case NORMAL:
                break;
        }
    };

    enum Mode {
        NORMAL, WATCHDOG
    }
}
