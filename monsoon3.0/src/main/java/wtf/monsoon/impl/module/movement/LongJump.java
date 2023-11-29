package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventUpdate;

import java.util.Objects;

public class LongJump extends Module {

    public Setting<Mode> mode = new Setting<>("Mode", Mode.WATCHDOG)
            .describedBy("Mode of the module.");

    public Setting<HypixelMode> hypixelMode = new Setting<>("Watchdog Mode", HypixelMode.BOW)
            .describedBy("How to bypass on Hypixel")
            .visibleWhen(() -> mode.getValue().equals(Mode.WATCHDOG));

    public Timer timer = new Timer(), mineplexTimer = new Timer();

    public static int lastSlot = -1;

    protected boolean boosted = false, doneBow = false, hasStartedGlide = false;
    protected double motionVa = 2.8;

    private int stage;


    double distanceX = 0;
    double distanceZ = 0;
    double oldPosY = 0;
    double yPos = 0;
    boolean hasBoosted, hasJumped;

    public LongJump() {
        super("Long Jump", "Jump longer", Category.MOVEMENT);
        this.setMetadata(() -> {
            if (mode.getValue() == Mode.WATCHDOG) {
                return "Watchdog (" + StringUtil.formatEnum(hypixelMode.getValue()) + ")";
            }

            return StringUtil.formatEnum(mode.getValue());
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        stage = 0;
        timer.reset();
        hasBoosted = false;
        hasJumped = false;
        hasStartedGlide = false;

        doneBow = false;
        if (mode.getValue() == Mode.WATCHDOG) {
            if (hypixelMode.getValue() == HypixelMode.BOW) {
                if (hasBow()) {
                    selfBow();
                } else {
                }
            }
        }

        lastSlot = -1;
        oldPosY = mc.thePlayer.posY;
        yPos = mc.thePlayer.posY;
        distanceX = mc.thePlayer.posX;
        distanceZ = mc.thePlayer.posZ;
    }

    public void onDisable() {
        super.onDisable();
        mc.getTimer().timerSpeed = 1F;
        lastSlot = -1;
        mc.thePlayer.speedInAir = 0.02F;
        boosted = false;
        motionVa = 2.8;
        if (mode.getValue().equals(Mode.WATCHDOG) && hypixelMode.getValue().equals(HypixelMode.BOW)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        switch (mode.getValue()) {
            case WATCHDOG:
                if (hypixelMode.getValue() == HypixelMode.BOW) {
                    if (doneBow) {
                        if (mc.thePlayer.hurtTime > 0) hasBoosted = true;
                        if (hasBoosted) mc.gameSettings.keyBindForward.pressed = true;
                    } else if (doneBow) {
                        mc.gameSettings.keyBindBack.pressed = false;
                        mc.gameSettings.keyBindForward.pressed = false;
                        mc.gameSettings.keyBindRight.pressed = false;
                        mc.gameSettings.keyBindLeft.pressed = false;
                    }
                    if (timer.hasTimeElapsed(1800, false) && mc.thePlayer.onGround && doneBow) {
                        this.toggle();
                    }
                }
                break;
            case NCP:
                if (mc.thePlayer.onGround) {
                    if (!hasJumped) {
                        mc.thePlayer.jump();
                        hasJumped = true;
                    }
                } else {
                    float dir = mc.thePlayer.rotationYaw + ((mc.thePlayer.moveForward < 0) ? 180 : 0) + ((mc.thePlayer.moveStrafing > 0) ? (-90F * ((mc.thePlayer.moveForward < 0) ? -.5F : ((mc.thePlayer.moveForward > 0) ? .4F : 1F))) : 0);
                    float xDir = (float) Math.cos((dir + 90F) * Math.PI / 180);
                    float zDir = (float) Math.sin((dir + 90F) * Math.PI / 180);
                    if (mc.thePlayer.motionY == .33319999363422365 && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())) {
                        mc.thePlayer.motionX = xDir * 1.0381;
                        mc.thePlayer.motionZ = zDir * 1.0381;
                    }
                }

                if (timer.hasTimeElapsed(300, false) && mc.thePlayer.onGround) {
                    this.toggle();
                }
                break;
            case FUNCRAFT:
                if (mc.thePlayer.onGround) {
                    if (!hasJumped) {
                        mc.thePlayer.jump();
                        hasJumped = true;
                        timer.reset();
                    }
                } else {
                    float dir = mc.thePlayer.rotationYaw + ((mc.thePlayer.moveForward < 0) ? 180 : 0) + ((mc.thePlayer.moveStrafing > 0) ? (-90F * ((mc.thePlayer.moveForward < 0) ? -.5F : ((mc.thePlayer.moveForward > 0) ? .4F : 1F))) : 0);
                    float xDir = (float) Math.cos((dir + 90F) * Math.PI / 180);
                    float zDir = (float) Math.sin((dir + 90F) * Math.PI / 180);
                    if (mc.thePlayer.motionY == .33319999363422365 && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())) {
                        mc.thePlayer.motionX = xDir * 1.6561;
                        if (stage != 2) mc.thePlayer.motionY += 0.05f;
                        mc.thePlayer.motionZ = zDir * 1.6561;
                    }

                    if (mc.thePlayer.motionY < 0) {
                        stage = 2;
                    }
                }

                if (timer.hasTimeElapsed(700, false) && mc.thePlayer.onGround) {
                    this.toggle();
                }
                break;
            case VULCAN_BOAT:
                //if(mc.thePlayer.isRiding()) mc.thePlayer.dismountEntity(mc.thePlayer.ridingEntity);
                if (mc.thePlayer.onGround) {
                    if (!hasJumped) {
                        mc.getTimer().timerSpeed = 0.7f;
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY = 1.0f;
                        hasJumped = true;
                        timer.reset();
                        stage = 0;
                    }
                } else {
                    float dir = mc.thePlayer.rotationYaw + ((mc.thePlayer.moveForward < 0) ? 180 : 0) + ((mc.thePlayer.moveStrafing > 0) ? (-90F * ((mc.thePlayer.moveForward < 0) ? -.5F : ((mc.thePlayer.moveForward > 0) ? .4F : 1F))) : 0);
                    float xDir = (float) Math.cos((dir + 90F) * Math.PI / 180);
                    float zDir = (float) Math.sin((dir + 90F) * Math.PI / 180);
                    if (hasJumped && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())) {
                        mc.getTimer().timerSpeed = 1.0f;
                        mc.thePlayer.motionX = xDir * 6;
                        mc.thePlayer.motionZ = zDir * 6;
                        mc.thePlayer.motionY = 0;
                    }
                }

                if (hasJumped) {
                    stage++;
                }

                PlayerUtil.sendClientMessage(stage + " " + hasJumped);

                if (stage >= 19 && hasJumped) {
                    player.setSpeed(0);
                    mc.thePlayer.setVelocity(0, 0, 0);
                    this.toggle();
                }
                break;
            case VERUS:
                mc.thePlayer.cameraYaw = 0.099999994f;
                if (mc.thePlayer.onGround) {
                    if (!boosted) {
                        PlayerUtil.damageVerus();
                        mc.thePlayer.motionY = 0.8;
                        player.setSpeed(3);
                    }
                    boosted = true;
                }
                if (timer.hasTimeElapsed(1000, false) && mc.thePlayer.onGround) {
                    player.setSpeed(0);
                    mc.thePlayer.motionY = 0;
                    this.toggle();
                }
                break;
        }
    };

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {

        if (mode.getValue() == Mode.WATCHDOG) {
            switch (hypixelMode.getValue()) {
                case BOW:
                    if (hasBoosted) {
                        mc.thePlayer.cameraYaw = 0.099999994f;
                        if (mc.thePlayer.onGround) {
                            if (!hasJumped) {
                                //Monsoon.INSTANCE.manager.blink.setEnabled(true);
                                //mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F) * 1.81;
                                //mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F);
                                mc.thePlayer.jump();
                                mc.thePlayer.motionY = 0.7f;
                                hasJumped = true;
                            }
                        } else if (mc.thePlayer.motionY > 0) {
                            PlayerUtil.sendClientMessage("sex");
                            //MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 2.1);
                            //mc.thePlayer.motionY += 0.02099999910593033;
                            player.setSpeed(player.getBaseMoveSpeed() * 1.2);
                        } else {
                            //SpeedUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.08);
                        }
                    } else {
                        mc.gameSettings.keyBindBack.pressed = false;
                        mc.gameSettings.keyBindForward.pressed = false;
                        mc.gameSettings.keyBindRight.pressed = false;
                        mc.gameSettings.keyBindLeft.pressed = false;
                    }
                    break;
                case NO_DMG:
                    if (mc.thePlayer.onGround && !timer.hasTimeElapsed(200, false)) {
                        mc.getTimer().timerSpeed = 1.6f;
                        player.setSpeed(player.getBaseMoveSpeed() * 1.5);
                        mc.thePlayer.jump();
                        timer.reset();
                    } else {
                        if (!timer.hasTimeElapsed(800, false)) {
                            if (mc.thePlayer.motionY < 0) {
                                //mc.thePlayer.motionY *= 0.85;
                                mc.getTimer().timerSpeed = 1.15f;
                                if (!mc.thePlayer.onGround)
                                    player.setSpeed(player.getBaseMoveSpeed() * 1.25);
                            } else {
                                mc.getTimer().timerSpeed = 1.3f;
                                if (!mc.thePlayer.onGround)
                                    player.setSpeed(player.getBaseMoveSpeed() * 1.3);
                            }
                        } else this.toggle();
                    }
                    break;
            }
        }

        /*if(mode.is("Hypixel2")) {
			mc.thePlayer.cameraYaw = 0.099999994f;
			if (mc.thePlayer.onGround) {
				//Monsoon.INSTANCE.manager.blink.setEnabled(true);
				if(!boosted) {
					DamageUtil.oldNCPDamage();
					SpeedUtil.setSpeed(0.6);
					//mc.thePlayer.motionY = 0.7;
					mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F) * 1.42;
					boosted = true;
				} else {
					mc.timer.timerSpeed = 1.0f;
				}
			}
			if(boosted && timer.hasTimeElapsed(500, false) && mc.thePlayer.onGround) {
				//Monsoon.INSTANCE.manager.blink.setEnabled(false);
				this.toggle();
			}


        }*/

    };

    public void selfBow() {
        Thread thread = new Thread() {
            public void run() {
                mc.gameSettings.keyBindBack.pressed = false;
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindRight.pressed = false;
                mc.gameSettings.keyBindLeft.pressed = false;
                int oldSlot = mc.thePlayer.inventory.currentItem;
                ItemStack block = mc.thePlayer.getCurrentEquippedItem();

                if (block != null) {
                    block = null;
                }
                int slot = mc.thePlayer.inventory.currentItem;
                for (short g = 0; g < 9; g++) {

                    if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
                            && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow
                            && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
                            && (block == null || (block.getItem() instanceof ItemBow))) {

                        slot = g;
                        block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();

                    }
                }

                mc.gameSettings.keyBindBack.pressed = false;
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindRight.pressed = false;
                mc.gameSettings.keyBindLeft.pressed = false;

                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -90, mc.thePlayer.onGround));
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -90, true));
                if (block.getItem() != null && !(block.getItem() instanceof ItemFishingRod))
                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, 3, -1), EnumFacing.UP));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(oldSlot));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                doneBow = true;
                mc.gameSettings.keyBindBack.pressed = false;
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindRight.pressed = false;
                mc.gameSettings.keyBindLeft.pressed = false;
            }
        };

        thread.start();

        /*Timer fuck = new Timer();
        fuck.reset();
        int oldSlot = mc.thePlayer.inventory.currentItem;

        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
        Thread thread = new Thread(){
            public void run(){
                int oldSlot = mc.thePlayer.inventory.currentItem;
                ItemStack block = mc.thePlayer.getCurrentEquippedItem();

                if (block != null) {
                    block = null;
                }
                int slot = mc.thePlayer.inventory.currentItem;
                for (short g = 0; g < 9; g++) {

                    if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
                            && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow
                            && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
                            && (block == null
                            || (block.getItem() instanceof ItemBow))) {

                        slot = g;
                        block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();

                    }

                }

                if(slot == 0) {
                    //Monsoon.sendNotif("You need a bow in your hotbar!");
                    toggle();
                }

                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                mc.thePlayer.inventory.currentItem = slot;
                mc.gameSettings.keyBindUseItem.pressed = true;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, -90, true));
                mc.gameSettings.keyBindUseItem.pressed = false;

                try {
                    Thread.sleep(180);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doneBow = true;

                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(oldSlot));
                mc.thePlayer.inventory.currentItem = oldSlot;
            }
        };

        thread.start();

        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;

        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(oldSlot));
        mc.thePlayer.inventory.currentItem = oldSlot;*/

    }

    public boolean hasBow() {
        for (int g = 0; g < 9; g++) {
            if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0) {
                return true;
            }
        }
        return false;
    }

    enum Mode {
        WATCHDOG, NCP, FUNCRAFT, VULCAN_BOAT, VERUS
    }

    enum HypixelMode {
        BOW,
        NO_DMG
    }

}
