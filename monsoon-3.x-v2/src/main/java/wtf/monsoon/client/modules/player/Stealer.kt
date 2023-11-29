package wtf.monsoon.client.modules.player

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.init.Blocks
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.Item
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.network.play.client.C0EPacketClickWindow
import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.util.math.randomNumber
import wtf.monsoon.client.util.misc.Stopwatch
import wtf.monsoon.client.util.network.PacketUtil
import wtf.monsoon.client.util.player.ItemUtil
import java.util.*


class Stealer : Module("Stealer", "Automatically steal items from chests.", Category.PLAYER) {

    private val mode = Setting("Mode", Mode.NORMAL)
    private val delay = Setting("Delay", "The delay to use", 50L, 0L, 1000L, 10L)

    private val enableRandomization = Setting("Randomization", true)
    private val randomDelay = Setting("Range", "Range of randomization", 10L, 1L, 50L, 1L)
        .visibleWhen { enableRandomization.getValue() }

    private val autoClose = Setting("Auto Close Chest", true)
    private val strict = Setting("Strict Steal", true)

    var timer = Stopwatch()
    var time = this.delay.getValue()
    var index = 0

    override fun enable() {
        super.enable()
        this.timer.reset()
        this.time = this.delay.getValue()
        this.index = 0
    }

    @EventListener
    val preMotion = fun(e: EventPreMotion) {
        if(this.timer.hasTimeElapsed(this.time)) {
            if(mc.currentScreen is GuiChest && mc.thePlayer.openContainer!! is ContainerChest) {
                val chest = mc.thePlayer.openContainer as ContainerChest
                if(this.index >= chest.lowerChestInventory.sizeInventory)
                    this.index = 0

                if(!this.validChest(mc.currentScreen as GuiChest))
                    return

                if(this.getTotalItems(chest) <= 0 && this.autoClose.getValue())
                    mc.thePlayer.closeScreen()

                if(chest.getSlot(index).hasStack) {
                    when(this.mode.getValue()) {
                        Mode.NORMAL ->
                            return this.steal(this.index, chest.windowId)

                        Mode.SMART -> {
                            val stack: ItemStack = chest.getSlot(index).stack
                            val item: Item = stack.item

                            if (item is ItemSword || item is ItemArmor || item is ItemTool) {
                                if (item is ItemSword)
                                    if (ItemUtil.isBestSword(stack, item))
                                        return this.steal(this.index, chest.windowId)

                                if (item is ItemArmor)
                                    if (ItemUtil.isBestArmor(stack, item))
                                        return this.steal(this.index, chest.windowId)

                                if (item is ItemTool)
                                    if (ItemUtil.isBestTool(stack, item))
                                        return this.steal(this.index, chest.windowId)
                            } else
                                if (!ItemUtil.isJunk(stack, item))
                                    return this.steal(this.index, chest.windowId)
                        }
                    }
                }
                this.index++
            } else
                this.index = 0
        }
    }

    private fun steal(slotId: Int, windowId: Int) {
        val stack = mc.thePlayer.openContainer.slotClick(slotId, 0, 1, mc.thePlayer)
        if(this.strict.getValue()) {
            val transactionId = mc.thePlayer.openContainer.getNextTransactionID(mc.thePlayer.inventory)
            PacketUtil.sendPacketNoEvent(C0EPacketClickWindow(windowId, slotId, 0, 0, stack, transactionId))
        }

        if(this.enableRandomization.getValue())
            this.time += randomNumber(randomDelay.getValue(), -randomDelay.getValue())
        this.timer.reset()
    }

    private fun getTotalItems(chest: ContainerChest): Int {
        var count = 0;
        for (i in 0 until chest.lowerChestInventory.sizeInventory)
            if (chest.getSlot(i).hasStack)
                count++

        return count
    }

    private fun validChest(chest: GuiChest): Boolean {
        val chestName: String = chest.lowerChestInventory.displayName.unformattedText.lowercase(Locale.getDefault())
        val forbiddenWords = arrayOf(
            "game",
            "select",
            "compass",
            "teleport",
            "hypixel",
            "play",
            "skywars",
            "bedwars",
            "cakewars",
            "lobby",
            "mode",
            "shop",
            "map",
            "cosmetic",
            "duel",
            "menu"
        )
        for (word in forbiddenWords)
            if (chestName.contains(word))
                return false
        return !chestName.contains("clique")
    }

    enum class Mode {
        NORMAL, SMART
    }
}