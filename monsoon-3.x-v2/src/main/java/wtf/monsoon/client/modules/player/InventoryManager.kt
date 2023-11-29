package wtf.monsoon.client.modules.player

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.gui.inventory.GuiContainerCreative
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.*
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.module.Module
import wtf.monsoon.backend.setting.Setting
import wtf.monsoon.client.event.EventPacket
import wtf.monsoon.client.event.EventPreMotion
import wtf.monsoon.client.event.EventRender2D
import wtf.monsoon.client.util.math.randomNumber
import wtf.monsoon.client.util.misc.Stopwatch
import wtf.monsoon.client.util.player.ItemUtil
import java.awt.Color

class InventoryManager : Module("Manager", "Manages your inventory.", Category.PLAYER) {

    private val delay = Setting("Delay", "The delay to use", 50L, 0L, 1000L, 10L)

    private val enableRandomization = Setting("Randomization", true)
    private val randomDelay = Setting("Range", "Range of randomization", 10L, 1L, 50L, 1L)
        .visibleWhen { enableRandomization.getValue() }

    private val openInv = Setting("While Inventory Open", false)
    private val whileNotMoving = Setting("While not moving", false)

    private val maxBlocks = Setting("Maximum Blocks", "The maximum amount of blocks to allow in your inventory.", 128, 32, 512, 8)

    private val slotsGroup = Setting<String>("Slots", "container")
    private val swordSlot = Setting("Sword Slot", "Slot to place your sword in", 1, 0, 9, 1).childOf(slotsGroup)
    private val pickaxeSlot = Setting("Pickaxe Slot", "Slot to place your pickaxe in", 2, 0, 9, 1).childOf(slotsGroup)
    private val axeSlot = Setting("Axe Slot", "Slot to place your axe in", 3, 0, 9, 1).childOf(slotsGroup)
    private val shovelSlot = Setting("Shovel Slot", "Slot to place your shovel in", 4, 0, 9, 1).childOf(slotsGroup)
    private val blockSlot = Setting("Blocks Slot", "Slot to place your blocks in", 5, 0, 9, 1).childOf(slotsGroup)
    private val gappleSlot = Setting("Golden Apple Slot", "Slot to place your gaps in", 6, 0, 9, 1).childOf(slotsGroup)

    private val slots = arrayOf(swordSlot, pickaxeSlot, axeSlot, shovelSlot)

    var timer = Stopwatch()
    var time = this.delay.getValue()
    var index = 0
    var lastIndex = 0

    override fun enable() {
        super.enable()
        this.timer.reset()
        this.time = this.delay.getValue()
        this.index = 9
        this.lastIndex = 9
    }

    @EventListener
    val preMotion = fun(_: EventPreMotion) {
        this.manage()
    }

    private fun manage() {
        if(this.timer.hasTimeElapsed(this.time)) {
            if(this.canRun() && this.slotsAreValid()) {
                val inventory = mc.thePlayer.inventoryContainer

                if(this.index == this.lastIndex)
                    this.index++

                if(this.index >= inventory.inventorySlots.size)
                    this.index = 9

                this.lastIndex = this.index

                if(!inventory.getSlot(index).hasStack)
                    return

                val stack: ItemStack = inventory.getSlot(index).stack!!
                val item: Item = stack.item

                if (item is ItemSword || item is ItemArmor || item is ItemTool) {
                    if(this.isDuplicate(this.index, stack, item))
                        return this.dropItem(this.index, inventory.windowId)

                    if (item is ItemSword)
                        if (ItemUtil.isBestSword(stack, item))
                            if(this.moveToSlot(this.index, this.swordSlot.getValue() - 1, inventory.windowId)) return
                        else
                            return this.dropItem(this.index, inventory.windowId)

                    if (item is ItemArmor)
                        if (!ItemUtil.isBestArmor(stack, item))
                            return this.dropItem(this.index, inventory.windowId)
                        else
                            if(this.moveToSlot(this.index, this.getArmorSlot(item), inventory.windowId)) return

                    if (item is ItemTool)
                        if (!ItemUtil.isBestTool(stack, item))
                            return this.dropItem(this.index, inventory.windowId)
                        else
                            if(this.moveToSlot(this.index, this.getToolSlot(item.javaClass) - 1, inventory.windowId)) return
                } else {
                    if (item is ItemBlock) {
                        if(this.getBlocksInInventory() > this.maxBlocks.getValue()) {
                            return this.dropItem(this.index, inventory.windowId)
                        }

                        if(!inventory.getSlot(this.blockSlot.getValue()).hasStack) {
                            if(this.moveToSlot(this.index, this.blockSlot.getValue() - 1, inventory.windowId)) return
                        } else if(inventory.getSlot(this.blockSlot.getValue()).stack.item !is ItemBlock) {
                            if(this.moveToSlot(this.index, this.blockSlot.getValue() - 1, inventory.windowId)) return
                        }
                    }

                    if (item is ItemAppleGold)
                        if(this.moveToSlot(this.index, this.gappleSlot.getValue() - 1, inventory.windowId)) return


                    if (ItemUtil.isJunk(stack, item))
                        return this.dropItem(this.index, inventory.windowId)
                }
            } else
                this.index = 9
        }
    }

    @EventListener
    val render2D = fun(e: EventRender2D) {
        if(!this.slotsAreValid()) {
            val fr = mc.fontRendererObj
            val message = "Two or more slots are the same in Manager settings, the module will not function until this is fixed."
            val l1: Int = e.scaledResolution.scaledWidth / 2 - fr.getStringWidth(message) / 2
            val i1: Int = e.scaledResolution.scaledHeight / 2 + 8
            fr.drawString(message, l1 + 1, i1, 0)
            fr.drawString(message, l1 - 1, i1, 0)
            fr.drawString(message, l1, i1 + 1, 0)
            fr.drawString(message, l1, i1 - 1, 0)
            fr.drawString(message, l1, i1, Color.RED.rgb)
        }
    }

    private fun isDuplicate(slot: Int, stack: ItemStack, item: Item): Boolean {
        if(item is ItemTool) {
            for (i in 0..44) {
                if(i == slot) continue
                val slot2: Slot = ItemUtil.mc.thePlayer.inventoryContainer.getSlot(i)
                if(!slot2.hasStack) continue
                val stack2: ItemStack = slot2.stack
                if (stack != stack2 && stack2.item is ItemTool) {
                    val item2 = stack2.item as ItemTool
                    if(ItemUtil.preferredBlock(item2.javaClass) != ItemUtil.preferredBlock(item.javaClass))
                        continue
                    val block = ItemUtil.preferredBlock(item2.javaClass)
                    if (item.getStrVsBlock(stack2, block) == item2.getStrVsBlock(stack, block))
                        return true
                }
            }
        } else if(item is ItemSword) {
            for (i in 0..44) {
                val slot2: Slot = ItemUtil.mc.thePlayer.inventoryContainer.getSlot(i)
                if(!slot2.hasStack) continue
                val comparingStack: ItemStack = slot2.stack
                if (stack != comparingStack && comparingStack.item is ItemSword) {
                    val item2 = comparingStack.item as ItemSword
                    if (item.attackDamage + ItemUtil.getSwordStrength(stack) == item2.attackDamage + ItemUtil.getSwordStrength(comparingStack))
                        return true
                }
            }
        }

        return false
    }

    private fun moveToSlot(fromSlot: Int, toSlot: Int, windowId: Int): Boolean {
        if(toSlot >= 0) {
            if(toSlot < 9) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, fromSlot, toSlot, 2, mc.thePlayer);
            } else if (toSlot == 69) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, fromSlot, 0, 1, mc.thePlayer);
            } else {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, fromSlot, 0, 0, mc.thePlayer);
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, toSlot, 0, 0, mc.thePlayer);
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, fromSlot, 0, 0, mc.thePlayer);
            }
            if (this.enableRandomization.getValue())
                this.time += randomNumber(randomDelay.getValue(), -randomDelay.getValue())
            this.timer.reset()
            return true
        } else return false
    }

    private fun getToolSlot(clazz: Class<*>): Int {
        return if (clazz == ItemPickaxe::class.java)
            this.pickaxeSlot.getValue()
        else if (clazz == ItemAxe::class.java)
            this.axeSlot.getValue()
        else this.shovelSlot.getValue()
    }

    private fun getArmorSlot(armor: ItemArmor): Int {
        // return armor.armorType
        return 69
    }

    private fun getBlocksInInventory(): Int {
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

    private fun slotsAreValid(): Boolean {
        this.slots.forEach { s1 ->
            if(s1.getValue() != 0)
                this.slots.forEach { s2 ->
                    if(s2.getValue() != 0)
                        if (s1.getValue() == s2.getValue() && s1 != s2) return false
                }
        }
        return true
    }

    private fun dropItem(slot: Int, windowId: Int) {
        val slo2t: Slot = ItemUtil.mc.thePlayer.inventoryContainer.getSlot(slot)
        val stack: ItemStack = slo2t.stack
        mc.playerController.windowClick(ItemUtil.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, ItemUtil.mc.thePlayer)
        if(this.enableRandomization.getValue())
            this.time += randomNumber(randomDelay.getValue(), -randomDelay.getValue())
        this.timer.reset()
    }

    private fun canRun(): Boolean {
        return ((mc.currentScreen is GuiInventory && mc.currentScreen !is GuiContainerCreative) || !openInv.getValue()) && (!player.moving || !whileNotMoving.getValue())
    }
}