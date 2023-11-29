package wtf.monsoon.client.modules.player

import me.bush.eventbuskotlin.Event
import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.init.Blocks
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.*
import wtf.monsoon.client.util.math.randomNumber
import wtf.monsoon.client.util.misc.Stopwatch
import wtf.monsoon.client.util.network.PacketUtil
import wtf.monsoon.client.util.player.RotationUtil
import wtf.monsoon.misc.InstanceAccess
import java.awt.Color
import java.util.*

/*
   TODO: (thanks spinta)
    - Tower
    - Raycast
    - Rotations
    - Functioning reach
    - Render
    - Block Search customizability (kinda arbitrary, but constantly switching to the fullest stack or switching on depletion)
    - Expand
    - Keep Rot modes (never, always, place conditions)
 */

class Scaffold : Module("Scaffold", "Place blocks below you automatically.", Category.PLAYER) {

    private val placementContainer = Setting("Placement Settings", "container")
    private val cps: Setting<Double> = Setting("CPS", "Maximum clicks per second", 10.0)
        .minimum(1.0)
        .maximum(20.0)
        .incrementation(0.5)
        .childOf(placementContainer)
    private val placeEvent = Setting("Place Event", PlaceEvent.LEGIT).childOf(placementContainer)
    private val switchType = Setting("Item Switch Mode", ItemSwitch.SPOOF).childOf(placementContainer)

    private val movementContainer = Setting("Movement Settings", "container")
    val allowSprinting = Setting("Allow Sprint", true).childOf(movementContainer)
    private val keepY = Setting("Keep Y", false).childOf(movementContainer)
    private val autoJump = Setting("Auto Jump", false).childOf(this.keepY)
    private val timerSpeed: Setting<Float> = Setting("Timer Boost", "Control the speed of the game.", 1.0f)
        .minimum(1.0f)
        .maximum(2.0f)
        .incrementation(0.05f)
        .childOf(movementContainer)
    private val timerCondition = Setting("Timer Condition", TimerCondition.ALWAYS).childOf(movementContainer)

    private val bypassContainer = Setting("Bypass Settings", "container")
    private val sprintBypass = Setting("Sprint Bypass", SprintBypass.NONE).childOf(bypassContainer)
    private val sneakBypass = Setting("Sneak Bypass", SneakBypass.NONE).childOf(bypassContainer)

    private var ticksSincePlaced = 0
    private var info: BlockInfo? = null

    private var lastSlot: Int = -1
    private var lastItem: ItemStack? = null
    private var oldSlot: Int = -1
    private var oldItem: ItemStack? = null
    private var blockYaw: Float = 0f
    private var item: ItemStack? = null
    private var slot: Int = -1

    private var yaw = 0f
    private var pitch = 0f

    private var lastTickYaw = 0f
    private var lastTickPitch = 0f
    private var lastPlaceYaw = 0f
    private var lastPlacePitch = 0f

    private var lastDelay = 1000 / this.cps.getValue()
    private val placeTimer: Stopwatch = Stopwatch()

    private var savedYCoordinate = 0.0
    private val yCoordinateValue: Double
        get() {
            return if(this.keepY.getValue()) {
                if(mc.thePlayer.onGround || mc.gameSettings.keyBindJump.isKeyDown)
                    savedYCoordinate = mc.thePlayer.posY

                savedYCoordinate - 1
            } else mc.thePlayer.posY - 1
        }

    private var sprintBlinkPackets = mutableListOf<Packet<*>>()

    var isPlaceTick: Boolean = false
    var isSneaking: Boolean = false

    override fun enable() {
        super.enable()
        this.placeTimer.reset()
        this.savedYCoordinate = mc.thePlayer.posY
        this.isSneaking = mc.thePlayer.isSneaking

        mc.thePlayer.heldItem
            .also { this.lastItem = it }
            .also { this.oldItem = it }

        mc.thePlayer.inventory.currentItem
            .also { this.lastSlot = it }
            .also { this.oldSlot = it }
    }

    override fun disable() {
        super.disable()
        this.oldItem?.let { this.switchToSlot(it, this.oldSlot) }

        if(this.sprintBlinkPackets.isNotEmpty())
            sprintBlinkPackets.forEach(PacketUtil::sendPacketNoEvent)

        this.unsneak()
        mc.timer.timerSpeed = 1.0f
    }

    @EventListener
    val onUpdate = fun(e: EventUpdate) {
        this.isPlaceTick = false

        if(this.placeTimer.hasTimeElapsed(this.lastDelay)) {
            this.isPlaceTick = true
            this.lastDelay = (1000 / this.cps.getValue()) + randomNumber(10, -10)
        }

        if(this.timerCondition.getValue().shouldTimer.invoke())
            mc.timer.timerSpeed = this.timerSpeed.getValue()
        else
            mc.timer.timerSpeed = 1.0f
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        this.info = this.getDiagonalBlockInfo(BlockPos(mc.thePlayer.posX, this.yCoordinateValue, mc.thePlayer.posZ))
        if(!this.isReplaceable(this.info!!))
            return

        RotationUtil.getScaffoldRotations(this.info!!)
            // Set yaw values
            .also { this.yaw = it[0] }
            .also { e.yaw = it[0] }
            .also { mc.thePlayer.rotationYawHead = it[0] }
            .also { mc.thePlayer.renderYawOffset = RotationUtil.getRenderYawOffset(it[0]) }
            .also { this.lastTickYaw = it[0] }

            // Set pitch values
            .also { this.pitch = it[1] }
            .also { e.pitch = it[1] }
            .also { mc.thePlayer.rotationPitchHead = it[1] }
            .also { this.lastTickPitch = it[1] }

        this.getStackToPlace()
            .also { this.item = it.first }
            .also { this.slot = it.second }

        if(mc.thePlayer.onGround && this.keepY.getValue() && this.autoJump.getValue())
            player.jump(player.getJumpHeight(0.42f))

        if(!this.allowSprinting.getValue())
            mc.thePlayer.isSprinting = false
        else {
            if(this.sprintBypass.getValue() == SprintBypass.ALTERNATE)
                mc.thePlayer.isSprinting = mc.thePlayer.ticksExisted % 2 == 0
        }

        this.sneak()

        if(this.isPlaceTick)
            this.info?.let { this.placeBlock(it, e) }
    }

    @EventListener
    val postMotion = fun(e: EventPostMotion) {
        if(this.isPlaceTick)
            this.info?.let { this.placeBlock(it, e) }
    }

    @EventListener
    val rightClick = fun(e: EventRightClick) {
        if(this.isPlaceTick)
            this.info?.let { this.placeBlock(it, e) }
    }

    @EventListener
    val eventPacket = fun(e: EventPacket) {
        if (e.packet is S2FPacketSetSlot) {
            val packet = e.packet as S2FPacketSetSlot
            this.lastSlot = packet.slot
            this.lastItem = packet.itemStack
            e.cancel()
        }

        if (e.packet is C09PacketHeldItemChange) {
            if(this.switchType.getValue() != ItemSwitch.LITE) {
                val packet = e.packet as C09PacketHeldItemChange
                this.lastSlot = packet.slotId
            }
        }

        if(e.packet is C0BPacketEntityAction) {
            val packet = e.packet as C0BPacketEntityAction
            val startSprint = packet.action == C0BPacketEntityAction.Action.START_SPRINTING
            val stopSprint = packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING

            when(this.sprintBypass.getValue()) {
                SprintBypass.CANCEL -> {
                    if(startSprint || stopSprint)
                        e.cancel()
                }
                SprintBypass.BLINK -> {
                    if(startSprint || stopSprint) {
                        this.sprintBlinkPackets += packet
                        e.cancel()
                    }
                }
                else -> {}
            }
        }
    }

    @EventListener
    val render2d = fun(e: EventRender2D) {
        val blockCount = this.getBlockCount()
        val fr = mc.fontRendererObj
        val sr: ScaledResolution = e.scaledResolution
        val s: String = blockCount.toString()
        val percentage = blockCount.coerceAtMost(256) / 256f / 3f
        val l1 = sr.scaledWidth / 2 - fr.getStringWidth(s) / 2
        val i1: Int = sr.scaledHeight / 2 - fr.FONT_HEIGHT - 10
        fr.drawString(s, l1 + 1, i1, 0)
        fr.drawString(s, l1 - 1, i1, 0)
        fr.drawString(s, l1, i1 + 1, 0)
        fr.drawString(s, l1, i1 - 1, 0)
        fr.drawString(s, l1, i1, Color(Color.HSBtoRGB(percentage, 1.0f, 1.0f)).rgb)
    }

    private fun placeBlock(info: BlockInfo, event: Event) {
        if(event.javaClass == this.placeEvent.getValue().clazz) {
            this.item?.let { this.liteSpoof(it, this.slot, true) }
            this.item?.let { this.switchToSlot(it, this.slot) }
            val placed = mc.playerController.onPlayerRightClick(
                mc.thePlayer,
                mc.theWorld,
                this.item,
                info.pos,
                info.facing,
                this.getVec3(info)
            )
            if(placed) {
                this.ticksSincePlaced = 0;
                this.lastPlaceYaw = this.yaw
                this.lastPlacePitch = this.pitch
            }
            this.liteSpoof(mc.thePlayer.heldItem!!, mc.thePlayer.inventory.currentItem, false)
        }
    }

    private fun sneak() {
        when(this.sneakBypass.getValue()) {
            SneakBypass.ALWAYS -> {
                if (!this.isSneaking) {
                    PacketUtil.sendPacketNoEvent(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                    this.isSneaking = true
                }
            }
            SneakBypass.PLACE -> {
                if(this.isPlaceTick) {
                    if(!this.isSneaking) PacketUtil.sendPacketNoEvent(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                    else PacketUtil.sendPacketNoEvent(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    this.isSneaking = !this.isSneaking
                }
            }
            SneakBypass.SPAM -> {
                if(!this.isSneaking) PacketUtil.sendPacketNoEvent(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                else PacketUtil.sendPacketNoEvent(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                this.isSneaking = !this.isSneaking
            }
            SneakBypass.CLIENTSIDE -> {
                mc.thePlayer.movementInput.sneak = true
                this.isSneaking = true
            }
            else -> {}
        }
    }

    private fun unsneak() {
        when(this.sneakBypass.getValue()) {
            SneakBypass.CLIENTSIDE -> {
                mc.thePlayer.movementInput.sneak = false
                this.isSneaking = false
            }
            SneakBypass.NONE -> {}
            else -> {
                if(this.isSneaking) {
                    PacketUtil.sendPacketNoEvent(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    this.isSneaking = false
                }
            }
        }
    }

    private fun getBlockCount(): Int {
        var blockCount = 0
        for (i in 0..44) {
            val slot: Slot = mc.thePlayer.inventoryContainer.getSlot(i)
            if (!slot.hasStack) continue
            val stack = slot.stack
            if(slot.stack.item is ItemBlock)
                blockCount += stack.stackSize
        }
        return blockCount
    }

    private fun getVec3(info: BlockInfo) : Vec3 {
        val pos = info.pos
        val face = info.facing
        var x = pos.x.toDouble() + 0.5
        var y = pos.y.toDouble() + 0.5
        var z = pos.z.toDouble() + 0.5
        if (face != EnumFacing.UP && face != EnumFacing.DOWN)
            y += 0.5
        else {
            x += 0.3
            z += 0.3
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST)
            z += 0.15
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH)
            x += 0.15

        return Vec3(x, y, z)
    }

    private fun switchToSlot(item: ItemStack, slot: Int) {
        when(this.switchType.getValue()) {
            ItemSwitch.SPOOF ->
                if(this.lastSlot != slot)
                    PacketUtil.sendPacketNoEvent(C09PacketHeldItemChange(slot))

            ItemSwitch.SWITCH ->
                mc.thePlayer.inventory.currentItem = slot

            else -> {}
        }
        this.lastSlot = slot
        this.lastItem = item
    }

    private fun liteSpoof(item: ItemStack?, slot: Int, pre: Boolean) {
        if(this.switchType.getValue() == ItemSwitch.LITE) {
            if(pre) {
                if(this.lastSlot != slot) {
                    PacketUtil.sendPacketNoEvent(C09PacketHeldItemChange(slot))
                    this.lastSlot = slot
                    this.lastItem = item
                }
            } else {
                if(this.lastSlot != mc.thePlayer.inventory.currentItem) {
                    PacketUtil.sendPacketNoEvent(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    this.lastSlot = mc.thePlayer.inventory.currentItem
                    this.lastItem = mc.thePlayer.heldItem
                }
            }
        }
    }

    private fun getStackToPlace(): Pair<ItemStack, Int> {
        var item = this.lastItem
        var slot = this.lastSlot
        if(item != null && item.item != null && item.item !is ItemBlock)
            item = null
        for (g in 0..8) {
            if (mc.thePlayer.inventoryContainer.getSlot(g + 36).hasStack &&
                isValidBlock(mc.thePlayer.inventoryContainer.getSlot(g + 36).stack) &&
                item == null) {
                if (mc.thePlayer.inventoryContainer.getSlot(g + 36).stack.stackSize <= 0) continue
                slot = g
                item = mc.thePlayer.inventoryContainer.getSlot(g + 36).stack
            }
        }
        return Pair(item!!, slot)
    }

    private fun isValidBlock(stack: ItemStack): Boolean {
        return stack.item is ItemBlock &&
                !(stack.item as ItemBlock).block.localizedName.lowercase(Locale.getDefault()).contains("chest") &&
                !(stack.item as ItemBlock).block.localizedName.lowercase(Locale.getDefault()).contains("table") &&
                !(stack.item as ItemBlock).block.localizedName.lowercase(Locale.getDefault()).contains("tnt") &&
                !(stack.item as ItemBlock).block.localizedName.lowercase(Locale.getDefault()).contains("slab")
    }

    private fun getDiagonalBlockInfo(pos: BlockPos): BlockInfo? {
        val up = BlockPos(0, -1, 0)
        val east = BlockPos(-1, 0, 0)
        val west = BlockPos(1, 0, 0)
        val north = BlockPos(0, 0, 1)
        val south = BlockPos(0, 0, -1)
        if (canPlaceAt(pos.add(up)))
            return BlockInfo(pos.add(up), EnumFacing.UP)

        if (canPlaceAt(pos.add(east))) {
            blockYaw = 90f
            return BlockInfo(pos.add(east), EnumFacing.EAST)
        }
        if (canPlaceAt(pos.add(west))) {
            blockYaw = -90f
            return BlockInfo(pos.add(west), EnumFacing.WEST)
        }
        if (canPlaceAt(pos.add(south))) {
            blockYaw = 180f
            return BlockInfo(pos.add(south), EnumFacing.SOUTH)
        }
        if (canPlaceAt(pos.add(north))) {
            blockYaw = 0f
            return BlockInfo(pos.add(north), EnumFacing.NORTH)
        }
        val positions = arrayOf(east, west, south, north)
        var data: BlockInfo? = null
        for (offset in positions)
            if (getBlockInfo(pos.add(offset)).also { data = it } != null)
                return data

        for (offset1 in positions) for (offset2 in positions) if (getBlockInfo(pos.add(offset1).add(offset2)).also {
                data = it
            } != null) {
            return data
        }
        for (offset1 in positions) for (offset2 in positions) for (offset3 in positions) if (getBlockInfo(
                pos.add(
                    offset1
                ).add(offset2).add(offset3)
            ).also {
                data = it
            } != null
        ) {
            return data
        }
        return BlockInfo(pos, EnumFacing.DOWN)
    }

    private fun getBlockInfo(pos: BlockPos): BlockInfo? {
        if (mc.theWorld.getBlockState(pos.add(0, -1, 0)).block !== Blocks.air) {
            return BlockInfo(pos.add(0, -1, 0), EnumFacing.UP)
        } else if (mc.theWorld.getBlockState(pos.add(-1, 0, 0)).block !== Blocks.air) {
            blockYaw = 90f
            return BlockInfo(pos.add(-1, 0, 0), EnumFacing.EAST)
        } else if (mc.theWorld.getBlockState(pos.add(1, 0, 0)).block !== Blocks.air) {
            blockYaw = -90f
            BlockInfo(pos.add(1, 0, 0), EnumFacing.WEST)
        } else if (mc.theWorld.getBlockState(pos.add(0, 0, -1)).block !== Blocks.air) {
            blockYaw = 180f
            return BlockInfo(pos.add(0, 0, -1), EnumFacing.SOUTH)
        } else if (mc.theWorld.getBlockState(pos.add(0, 0, 1)).block !== Blocks.air) {
            blockYaw = 0f
            return BlockInfo(pos.add(0, 0, 1), EnumFacing.NORTH)
        }
        return null
    }

    private fun isReplaceable(info: BlockInfo): Boolean {
        return mc.theWorld.getBlockState(info.pos).block.canCollideCheck(mc.theWorld.getBlockState(info.pos), false)
    }

    private fun canPlaceAt(pos: BlockPos?): Boolean {
        return mc.theWorld.getBlockState(pos).block !== Blocks.air
    }

    class BlockInfo(val position: BlockPos?, val facing: EnumFacing) : InstanceAccess() {
        val pos: BlockPos
            get() {
                return position ?: BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)
            }
    }

    enum class PlaceEvent(var clazz: Class<out Event>) {
        LEGIT(EventRightClick::class.java),
        PRE(EventPreMotion::class.java),
        POST(EventPostMotion::class.java)
    }

    enum class ItemSwitch {
        SWITCH, SPOOF, LITE
    }

    enum class SprintBypass {
        NONE, CANCEL, ALTERNATE, BLINK
    }

    enum class SneakBypass {
        NONE, PLACE, SPAM, ALWAYS, CLIENTSIDE
    }

    enum class TimerCondition(var shouldTimer: () -> Boolean) {
        ALWAYS({ true }),
        TOWER({ Minecraft.getMinecraft().thePlayer.movementInput.jump }),
        PLACE({ Wrapper.monsoon.getModule(Scaffold::class.java).isPlaceTick }),
        NON_PLACE({ !Wrapper.monsoon.getModule(Scaffold::class.java).isPlaceTick })
    }
}