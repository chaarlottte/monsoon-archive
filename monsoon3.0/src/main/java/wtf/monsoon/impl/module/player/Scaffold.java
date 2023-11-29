package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.MovementUtil;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.*;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.module.combat.Aura;

import java.awt.*;

import static net.minecraft.util.EnumFacing.*;

public class Scaffold extends Module {

    private final Setting<Mode> mode = new Setting<>("Mode", Mode.NORMAL)
            .describedBy("The mode of the scaffold.");

    private final Setting<Rotations> rotations = new Setting<>("Rotations", Rotations.NORMAL)
            .describedBy("The rotations of the scaffold.");

    @Getter
    private final Setting<String> placementSettings = new Setting<>("Placement Settings", "Placement Settings")
            .describedBy("Configure block placement.");

    private final Setting<PlaceMode> placeMode = new Setting<>("PlaceMode", PlaceMode.PRE)
            .describedBy("The place mode of the scaffold.")
            .childOf(placementSettings);

    private final Setting<VecMode> vecMode = new Setting<>("Vec3 Mode", VecMode.LEGIT)
            .describedBy("The mode of the Vec3s.")
            .childOf(placementSettings);

    private final Setting<ItemMode> itemMode = new Setting<>("ItemMode", ItemMode.SPOOF)
            .describedBy("The item mode of the scaffold.")
            .childOf(placementSettings);

    @Getter
    private final Setting<Long> placeDelay = new Setting<>("Place Delay", 50L)
            .minimum(50L)
            .maximum(1000L)
            .incrementation(1L)
            .describedBy("The amount of times to attack per second")
            .childOf(placementSettings);

    @Getter
    private final Setting<String> movementSettings = new Setting<>("Movement Settings", "Movement Settings")
            .describedBy("Configure your movement while scaffolding.");

    private final Setting<Boolean> sprint = new Setting<>("Allow Sprinting", true)
            .describedBy("Whether or not to allow sprinting.")
            .childOf(movementSettings);


    private final Setting<Double> timer = new Setting<>("TimerBoost", 1.0)
            .minimum(1.0)
            .maximum(2.0)
            .incrementation(0.1)
            .describedBy("The timer boost of the scaffold.")
            .childOf(movementSettings);

    private final Setting<Boolean> keepY = new Setting<>("KeepY", false)
            .describedBy("The blocks will stay at the same Y coordinate. Useful for using speed while scaffolding.")
            .childOf(movementSettings);

    private final Setting<Boolean> autoJump = new Setting<>("AutoJump", false)
            .describedBy("Automatically jumps while Keep Y is enabled.")
            .childOf(keepY);

    private final Setting<TowerMode> towerMode = new Setting<>("Tower Mode", TowerMode.MOTION)
            .describedBy("The mode of the tower.")
            .childOf(movementSettings);

    private final Setting<Boolean> towerMove = new Setting<>("Tower Move", false)
            .describedBy("Allows you to go up faster and easier.")
            .childOf(movementSettings);

    @Getter
    private final Setting<String> bypassSettings = new Setting<>("Bypass Settings", "Bypass Settings")
            .describedBy("Configure settings relating to bypassing.");

    private final Setting<Boolean> sprintFix = new Setting<>("Sprint Fix", false)
            .describedBy("Helps with bypassing some anticheats.")
            .childOf(bypassSettings);

    @Getter
    private final Setting<Boolean> gcdFix = new Setting<>("GCD Fix", false)
            .describedBy("Whether to enable a GCD fix.")
            .childOf(bypassSettings);


    private BlockInfo info;
    private int lastSlot, oldSlot, sprintTicks, towerTicks;
    private ItemStack stackToPlace;

    private float finalRotationYaw, finalRotationPitch, blockYaw;

    private boolean sneaking = false, isPlacing = false, snuckThisTick = false;
    public double yCoordinate;

    private Timer placeTimer = new Timer();

    private int blockCount = 0;

    public Scaffold() {
        super("Scaffold", "Automatically places blocks below you.", Category.PLAYER);
        this.setMetadata(() -> StringUtil.formatEnum(mode.getValue()));
    }

    public void onEnable() {
        super.onEnable();
        lastSlot = mc.thePlayer.inventory.currentItem;
        oldSlot = mc.thePlayer.inventory.currentItem;
        blockYaw = mc.thePlayer.rotationYaw - 180;
        sprintTicks = 0;
        towerTicks = 0;
        sneaking = false;
        isPlacing = false;
        yCoordinate = mc.thePlayer.posY;
        placeTimer.reset();
        blockCount = getBlockCount();
        mc.thePlayer.setSprinting(false);
    }

    public void onDisable() {
        super.onDisable();
        lastSlot = mc.thePlayer.inventory.currentItem;
        mc.gameSettings.keyBindSneak.pressed = false;
        mc.getTimer().timerSpeed = 1F;

        if(itemMode.getValue() == ItemMode.SPOOF) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        } else {
            mc.thePlayer.inventory.currentItem = oldSlot;
        }

        // PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
    }

    @EventLink
    public final Listener<EventRender2D> eventRender2DListener = e -> {
        blockCount = getBlockCount();
        FontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = e.getSr();

        String s = String.valueOf(blockCount);

        float percentage = (Math.min(blockCount, 256) / 256f) / 3f;

        int l1 = sr.getScaledWidth() / 2 - (fr.getStringWidth(s) / 2);
        int i1 = sr.getScaledHeight() / 2 - fr.getHeight() - 10;
        fr.drawString(s, l1 + 1, i1, 0);
        fr.drawString(s, l1 - 1, i1, 0);
        fr.drawString(s, l1, i1 + 1, 0);
        fr.drawString(s, l1, i1 - 1, 0);
        fr.drawString(s, l1, i1, new Color(Color.HSBtoRGB(percentage, 1.0F, 1.0F)));
    };

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if(mc.thePlayer == null || mc.theWorld == null) this.toggle();
        if(Wrapper.getModule(Aura.class).getTarget() != null) return;

        if(keepY.getValue() && !mc.thePlayer.movementInput.jump) info = getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, yCoordinate - 1, mc.thePlayer.posZ));
        else info = getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));

        if(mode.getValue() == Mode.WATCHDOG) {
            if(mc.thePlayer.onGround && !(mc.thePlayer.ticksExisted % 3 == 0)) mc.getTimer().timerSpeed = timer.getValue().floatValue();
            else mc.getTimer().timerSpeed = 1.0f;
        } else mc.getTimer().timerSpeed = timer.getValue().floatValue();

        if(info == null || info.pos == null) return;

        sprintTicks++;
    };

    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if(info == null || info.pos == null || Wrapper.getModule(Aura.class).getTarget() != null) return;

        if(!isReplaceable(info)) return;

        this.setRotations(e);

        if(sneaking) PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));

        if(mc.thePlayer.onGround) {
            yCoordinate = mc.thePlayer.posY;
            if(keepY.getValue() && autoJump.getValue() && player.isMoving()) mc.thePlayer.jump();
        }

        if(mode.getValue() == Mode.WATCHDOG) {

        } else {
            mc.thePlayer.setSprinting(sprint.getValue() && player.isMoving());
        }

        stackToPlace = setStackToPlace();

        if(placeMode.getValue() == PlaceMode.PRE && !mode.getValue().equals(Mode.WATCHDOG)) {
            if(keepY.getValue() && !mc.thePlayer.movementInput.jump) info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, yCoordinate - 1, mc.thePlayer.posZ));
            else info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
            if(info.pos != null) this.placeBlock();
        } else if(mode.getValue().equals(Mode.WATCHDOG)) {
            if(!mc.gameSettings.keyBindJump.isKeyDown()) {
                if(keepY.getValue() && !mc.thePlayer.movementInput.jump) info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, yCoordinate - 1, mc.thePlayer.posZ));
                else info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
                if(info.pos != null) this.placeBlock();
            }
        }

        this.towerMotion(e);
    };

    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        if(placeMode.getValue() == PlaceMode.POST && !mode.getValue().equals(Mode.WATCHDOG)) {
            if(keepY.getValue() && !mc.thePlayer.movementInput.jump) info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, yCoordinate - 1, mc.thePlayer.posZ));
            else info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
            if(info.pos != null) this.placeBlock();
        } else if(mode.getValue().equals(Mode.WATCHDOG)) {
            if(mc.gameSettings.keyBindJump.isKeyDown()) {
                if(keepY.getValue() && !mc.thePlayer.movementInput.jump) info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, yCoordinate - 1, mc.thePlayer.posZ));
                else info = this.getDiagonalBlockInfo(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
                if(info.pos != null) this.placeBlock();
            }
        }
    };

    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        this.towerMotion(e);

        if(mode.getValue() == Mode.WATCHDOG && player.isMoving()) {
            /*if(mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                mc.thePlayer.setSprinting(true);
                e.setX(mc.thePlayer.motionX / 1.7);
                e.setZ(mc.thePlayer.motionZ / 1.7);
            } else {
                mc.thePlayer.setSprinting(true);
                e.setX(mc.thePlayer.motionX / 1.3);
                e.setZ(mc.thePlayer.motionZ / 1.3);
            }*/
            // mc.thePlayer.setSprinting(true);
            if(sprint.getValue()) {
                if(mc.thePlayer.ticksExisted % 7 == 0) {
                    mc.thePlayer.motionY = 0.00112;
                    player.setSpeed(e, 0.35);
                } else {
                    player.setSpeed(e, 0.205);
                }
            } else {
                player.setSpeed(e, 0.2);
            }
        }
    };

    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if(e.getDirection() == EventPacket.Direction.RECEIVE) {
            if(itemMode.getValue() == ItemMode.SPOOF) {
                if (e.packet instanceof S2FPacketSetSlot) {
                    PlayerUtil.debug("S2F");
                    e.setCancelled(true);
                }
            }
        }

        if(e.getDirection() == EventPacket.Direction.SEND) {
            if(itemMode.getValue() == ItemMode.SPOOF) {
                if (e.getPacket() instanceof C09PacketHeldItemChange) {
                    PlayerUtil.debug("C09 OUT");
                    C09PacketHeldItemChange packet = (C09PacketHeldItemChange) e.getPacket();
                    packet.setSlotId(lastSlot);
                    // lastSlot = ((C09PacketHeldItemChange) e.getPacket()).getSlotId();
                    // e.setCancelled(true);
                }
            }

            if(mode.getValue() == Mode.WATCHDOG) {
                if(e.getPacket() instanceof C0BPacketEntityAction) {
                    C0BPacketEntityAction packet = (C0BPacketEntityAction) e.getPacket();

                    if(packet.getAction() == C0BPacketEntityAction.Action.START_SPRINTING ||
                            packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        e.setCancelled(true);
                    }
                }
            }

            if(mode.getValue() == Mode.VERUS) {
                if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                    C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement) e.getPacket();
                    packet.setFacingX(0);
                    packet.setFacingY(0);
                    packet.setFacingZ(0);
                }
            }
        }
    };


    private void placeBlock() {
        boolean placed = false;
        if(sprintFix.getValue() && mc.thePlayer.isSprinting()) PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        if(canPlace()) placed = sendPlace();

        if(placed) {
            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
            new Thread(this::stopPlacing).start();
        }

    }

    private boolean sendPlace() {
        boolean placed = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, getPlacingItem(), info.getPos(), info.getFacing(), getVec3(info));
        if(placed) {
            if(mode.getValue() == Mode.VULCAN) {
                if(!this.sneaking && !this.snuckThisTick) {
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    // PlayerUtil.sendClientMessage("start sneak");
                    this.sneaking = true;
                } else if(this.sneaking) {
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    // PlayerUtil.sendClientMessage("stop sneak");
                    this.sneaking = false;
                }
            }
        }
        isPlacing = placed;
        return placed;
    }

    private boolean canPlace() {
        boolean correctMotion =
                (player.isMoving() || mc.thePlayer.motionY != 0);
        boolean correctBlockStuff =
                isReplaceable(info);
        boolean timerReady =
                placeTimer.hasTimeElapsed(placeDelay.getValue(), true);

        return correctMotion && correctBlockStuff;
    }

    private ItemStack getPlacingItem() {
        return (itemMode.getValue() == ItemMode.SPOOF ? stackToPlace : (mc.thePlayer.getHeldItem() != null ? mc.thePlayer.getHeldItem() : null));
    }

    private Vec3 getVec3(BlockInfo info) {
        switch (vecMode.getValue()) {
            case DIR:
                final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                Vec3 rotationVector = getVectorForRotation(finalRotationYaw, finalRotationPitch);
                return eyesPos.addVector(rotationVector.x * 4, rotationVector.y * 4, rotationVector.z * 4);
            case POS:
                return new Vec3(info.getPos().getX(), info.getPos().getY(), info.getPos().getZ());
            case STRICT:
                BlockPos pos = info.getPos();
                EnumFacing face = info.getFacing();
                double x = (double) pos.getX() + 0.5, y = (double) pos.getY() + 0.5, z = (double) pos.getZ() + 0.5;
                if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
                    y += 0.5;
                } else {
                    x += 0.3;
                    z += 0.3;
                }
                if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
                    z += 0.15;
                }
                if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
                    x += 0.15;
                }
                return new Vec3(x, y, z);
            case LEGIT:
                double x1 = info.getPos().getX() + 0.5f + (0.25f * info.getFacing().getDirectionVec().getX()),
                        y1 = info.getPos().getY() + 0.5f + (0.25f * info.getFacing().getDirectionVec().getY()),
                        z1 = info.getPos().getZ() + 0.5f + (0.25f * info.getFacing().getDirectionVec().getZ());
                return new Vec3(x1, y1, z1);
            case ZERO:
            default:
                return new Vec3(0, 0, 0);
        }
    }

    private Vec3 getVectorForRotation(final float yaw, final float pitch) {
        float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }


    private void towerMotion(Event event) {
        switch (towerMode.getValue()) {
            case MOTION:
                if(event instanceof EventMove) {
                    EventMove e = (EventMove) event;
                    if (canTower(1.0))
                        e.setY(mc.thePlayer.motionY = 0.42f);
                }
                break;
            case NCP:
                if(event instanceof EventMove) {
                    EventMove e = (EventMove) event;
                    if (mc.thePlayer.onGround)
                        towerTicks = 0;

                    if (canTower(1.1)) {
                        int position = (int) mc.thePlayer.posY;
                        if (mc.thePlayer.posY - position < 0.05) {
                            mc.thePlayer.posY = position;
                            e.setY(mc.thePlayer.motionY = 0.42f);
                            towerTicks = 1;
                        } else if (towerTicks == 1) {
                            e.setY(mc.thePlayer.motionY = 0.34f);
                            towerTicks++;
                        } else if (towerTicks == 2) {
                            e.setY(mc.thePlayer.motionY = 0.25f);
                            towerTicks++;
                        }
                    }
                }
                break;
            case WATCHDOG:
                if(event instanceof EventMove) {
                    EventMove e = (EventMove) event;
                    if (canTower(1.5)) e.setY(mc.thePlayer.motionY = 0.42f);
                }
                break;
            case POSITION:
                if(event instanceof EventPreMotion)
                    if(mc.thePlayer.ticksExisted % 10 == 0)
                        for(int i = 0; i < 5; i++)
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ);
                break;
            case SMALLHOP:
                if(event instanceof EventPreMotion)
                    if(mc.thePlayer.ticksExisted % 10 == 0)
                        mc.thePlayer.motionY = 0.2f;
                break;
            case VERUS:
                if(event instanceof EventPreMotion)
                    if(mc.thePlayer.ticksExisted % 2 == 0)
                        mc.thePlayer.jump();
                break;
            case NONE:
                break;
        }
    }

    private boolean canTower(final double down) {
        return towerMode.getValue() != TowerMode.NONE &&
                mc.gameSettings.keyBindJump.isKeyDown() &&
                (!player.isMoving() || towerMove.getValue()) &&
                !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - down, mc.thePlayer.posZ)).getBlock() instanceof BlockAir);
    }

    private void setRotations(EventPreMotion e) {
        float[] rots;
        float yaw, pitch;
        switch(rotations.getValue()) {
            case NORMAL:
                rots = getRotations(info);
                yaw = processRotation(rots[0]);
                pitch = processRotation(rots[1]);
                e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalRotationYaw = yaw);
                e.setPitch(mc.thePlayer.rotationPitchHead = finalRotationPitch = pitch);
                break;
            case AAC:
                rots = getAACRotations();
                yaw = rots[0];
                pitch = processRotation(rots[1]);
                e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalRotationYaw = yaw);
                e.setPitch(mc.thePlayer.rotationPitchHead = finalRotationPitch = pitch);
                break;
            case BRUTE_FORCE:
                rots = getBruteForceRotations(info);

                yaw = processRotation(rots[0]);
                pitch = processRotation(rots[1]);
                e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalRotationYaw = yaw);
                e.setPitch(mc.thePlayer.rotationPitchHead = finalRotationPitch = pitch);
                break;
            case SNAP:
                if(isPlacing) {
                    rots = getRotations(info);
                    yaw = processRotation(rots[0]);
                    pitch = processRotation(rots[1]);
                    e.setYaw(mc.thePlayer.rotationYawHead = finalRotationYaw = yaw);
                    e.setPitch(mc.thePlayer.rotationPitchHead = finalRotationPitch = pitch);
                } else {
                    e.setYaw(mc.thePlayer.rotationYawHead = finalRotationYaw = mc.thePlayer.rotationYaw);
                    e.setPitch(mc.thePlayer.rotationPitchHead = finalRotationPitch = mc.thePlayer.rotationPitch);
                }
                break;
            case WATCHDOG:
                double xDiff = info.getPos().getX() - mc.thePlayer.posX;
                double yDiff = info.getPos().getY() - mc.thePlayer.posY - 1.7;
                double zDiff = info.getPos().getZ() - mc.thePlayer.posZ;
                double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
                yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
                pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);

                yaw = processRotation(MovementUtil.getDirection() - 150);
                yaw = processRotation(yaw);
                pitch = processRotation((mc.thePlayer.movementInput.jump ? 90 : pitch));
                e.setYaw(mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = finalRotationYaw = yaw);
                e.setPitch(mc.thePlayer.rotationPitchHead = finalRotationPitch = pitch);
                break;
        }
    }

    private float[] getRotations(BlockInfo info) {
        if(mc.thePlayer == null || mc.theWorld == null) this.toggle();
        float yaw = 0, pitch = 90;

        final Vec3 eyes = mc.thePlayer.getPositionEyes(RandomUtils.nextFloat(2.997f, 3.997f));
        final Vec3 position = new Vec3(info.getPos().getX() + 0.49,info.getPos().getY() + 0.49,info.getPos().getZ() + 0.49).add(new Vec3(info.getFacing().getDirectionVec()));
        final Vec3 resultPosition = position.subtract(eyes);
        yaw = (float) Math.toDegrees(Math.atan2(resultPosition.z, resultPosition.x)) - 90.0F;
        pitch = (float) -Math.toDegrees(Math.atan2(resultPosition.y, Math.hypot(resultPosition.x, resultPosition.z)));

        //pitch = 83;
        return new float[]  { yaw, pitch };
    }

    private float[] getAACRotations() {
        if(mc.thePlayer == null || mc.theWorld == null) this.toggle();
        float clientYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        //float gottenYaw = lastPos != null && lastFacing != null ? BlockUtil.getDirectionToBlock(lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastFacing)[0] : mc.thePlayer.rotationYaw;
        float diff = (int) ((blockYaw - clientYaw) / 45.0F);

        float yaw = clientYaw + diff * 45;

        float pitch;

        if(!mc.thePlayer.onGround || mc.gameSettings.keyBindJump.isKeyDown()) {
            pitch = 90;
        } else if(MovementUtil.isGoingDiagonally()) {
            pitch = 83;
        } else {
            pitch = 81.5F;
        }
        return new float[] {yaw, pitch};
    }

    private float[] getBruteForceRotations(BlockInfo info) {
        if(mc.thePlayer == null || mc.theWorld == null) this.toggle();

        float yaw = mc.thePlayer.rotationYaw - 180, pitch = getRotations(info)[1];

        float ogyaw = yaw;

        for (int i = (int) ogyaw; i < ogyaw - 180; i++) {
            yaw = MathHelper.wrapAngleTo180_float(i);
            Vec3 src = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 rotationVec = Entity.getVectorForRotation(pitch, i);
            Vec3 dest = src.addVector(rotationVec.x * 3, rotationVec.y * 3, rotationVec.z * 3);
            IBlockState blockState = mc.theWorld.getBlockState(info.pos);
            AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, info.pos, blockState);

            if (bb.calculateIntercept(src, dest) != null) {
                yaw = i;
                break;
            }
        }

        return new float[] { yaw, pitch };
    }

    public BlockInfo getDiagonalBlockInfo(BlockPos pos) {
        BlockPos up = new BlockPos(0, -1, 0),
                east = new BlockPos(-1, 0, 0),
                west = new BlockPos(1, 0, 0),
                north = new BlockPos(0, 0, 1),
                south = new BlockPos(0, 0, -1);

        if (canPlaceAt(pos.add(up))) {
            return new BlockInfo(pos.add(up),  UP);
        }

        if (canPlaceAt(pos.add(east))) {
            blockYaw = 90;
            return new BlockInfo(pos.add(east), EAST);
        }

        if (canPlaceAt(pos.add(west))) {
            blockYaw = -90;
            return new BlockInfo(pos.add(west), WEST);
        }

        if (canPlaceAt(pos.add(south))) {
            blockYaw = 180;
            return new BlockInfo(pos.add(south), SOUTH);
        }

        if (canPlaceAt(pos.add(north))) {
            blockYaw = 0;
            return new BlockInfo(pos.add(north), NORTH);
        }

        BlockPos[] positions = {east, west, south, north};

        BlockInfo data = null;

        for (BlockPos offset : positions) {
            if ((data = getBlockInfo(pos.add(offset))) != null) {
                return  data;
            }
        }

        for (BlockPos offset1 : positions)
            for (BlockPos offset2 : positions)
                if ((data = getBlockInfo(pos.add(offset1).add(offset2))) != null) {
                    return data;
                }

        for(BlockPos offset1 : positions)
            for(BlockPos offset2 : positions)
                for(BlockPos offset3 : positions)
                    if((data = getBlockInfo(pos.add(offset1).add(offset2).add(offset3))) != null) {
                        return data;
                    }


        return new BlockInfo(pos,  DOWN);
    }

    public BlockInfo getBlockInfo(BlockPos pos) {
        if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).getBlock() != Blocks.air) {
            return new BlockInfo(pos.add(0, -1, 0), UP);
        } else if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock() != Blocks.air) {
            blockYaw = 90;
            return new BlockInfo(pos.add(-1, 0, 0), EAST);
        } else if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).getBlock() != Blocks.air) {
            blockYaw = -90;
            new BlockInfo(pos.add(1, 0, 0), EnumFacing.WEST);
        } else if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).getBlock() != Blocks.air) {
            blockYaw = 180;
            return new BlockInfo(pos.add(0, 0, -1), EnumFacing.SOUTH);
        } else if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).getBlock() != Blocks.air) {
            blockYaw = 0;
            return new BlockInfo(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        return null;
    }

    public boolean canPlaceAt(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock() != Blocks.air;
    }

    private ItemStack setStackToPlace() {

        ItemStack block = mc.thePlayer.getCurrentEquippedItem();
        if (block != null && block.getItem() != null && !(block.getItem() instanceof ItemBlock)) {
            block = null;
        }

        int slot = lastSlot;
        for (short g = 0; g < 9; g++) {


            if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() &&
                    isValidBlock(mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack())
                    && (block == null)) {

                //mc.thePlayer.inventory.currentItem = g;
                if(mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize <= 0) continue;
                slot = g;
                block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();
            }
        }

        if (lastSlot != slot) {
            slot = slot;
            if(itemMode.getValue() == ItemMode.SWITCH) mc.thePlayer.inventory.currentItem = slot;
            else PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
            // PlayerUtil.sendClientMessage("switche");
            PlayerUtil.debug(String.format("Switched from slot %1$s to slot %2$s", lastSlot, slot));

            lastSlot = slot;
        } else {
           // PlayerUtil.sendClientMessage(String.format("Chose not to switch from slot %1$s to slot %2$s", lastSlot, slot));
        }
        return block;
    }

    private boolean isValidBlock(ItemStack stack) {
        return (stack.getItem() instanceof ItemBlock) &&
                !((ItemBlock) stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("chest") &&
                !((ItemBlock) stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("table") &&
                !((ItemBlock) stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("tnt") &&
                !((ItemBlock) stack.getItem()).getBlock().getLocalizedName().toLowerCase().contains("slab");
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            Slot slot;
            try {
                slot = mc.thePlayer.inventoryContainer.getSlot(i);
            } catch (Exception ex) {
                continue;
            }

            if (!slot.getHasStack()) continue;

            ItemStack stack = slot.getStack();
            Item item = stack.getItem();

            if(!isValidBlock(stack)) continue;

            blockCount += stack.stackSize;
        }
        return blockCount;
    }

    private void stopPlacing() {
        try {
            Thread.sleep(100);
            isPlacing = false;
        } catch (Exception ignored) {}
    }

    private float processRotation(float value) {
        float toReturn = value;
        if(gcdFix.getValue()) {
            double m = 0.005 * mc.gameSettings.mouseSensitivity;
            double gcd = m * m * m * 1.2;
            toReturn -= toReturn % gcd;
            return MathHelper.wrapAngleTo180_float(toReturn);
        } else return MathHelper.wrapAngleTo180_float(toReturn);
    }

    private boolean isReplaceable(BlockInfo info) {
        return mc.theWorld.getBlockState(info.pos).getBlock().canCollideCheck(mc.theWorld.getBlockState(info.pos), false);
    }

    enum Mode {
        NORMAL, WATCHDOG, VERUS, VULCAN
    }

    enum Rotations {
        NORMAL, AAC, BRUTE_FORCE, SNAP, NONE, WATCHDOG
    }

    enum PlaceMode {
        PRE, POST
    }

    enum ItemMode {
        SWITCH, SPOOF
    }

    enum TowerMode {
        NONE, MOTION, POSITION, NCP, WATCHDOG, SMALLHOP, VERUS
    }

    enum VecMode {
        ZERO, POS, LEGIT, DIR, STRICT
    }

    private class BlockInfo {
        private BlockPos pos;

        @Getter
        private EnumFacing facing;

        public BlockInfo(BlockPos position, EnumFacing face) {
            this.pos = position;
            this.facing = face;
        }

        public BlockPos getPos() {
            if(pos == null) {
                return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
            } else return pos;
        }
    }

}