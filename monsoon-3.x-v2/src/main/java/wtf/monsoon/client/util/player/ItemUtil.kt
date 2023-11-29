package wtf.monsoon.client.util.player

import net.minecraft.block.Block
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.init.Blocks
import net.minecraft.inventory.Slot
import net.minecraft.item.*
import wtf.monsoon.Monsoon
import wtf.monsoon.Wrapper
import wtf.monsoon.client.util.Util
import java.util.*


object ItemUtil : Util() {

    fun isJunk(stack: ItemStack, item: Item): Boolean {
        val junk = listOf(
            "stick",
            "egg",
            "string",
            "cake",
            "mushroom",
            "flint",
            "dyePowder",
            "feather",
            "chest",
            "snowball",
            "fish",
            "enchant",
            "exp",
            "shears",
            "anvil",
            "torch",
            "seeds",
            "leather",
            "reeds",
            "skull",
            "record",
            "piston",
            "snow",
            "bottle",
            "poison",
            "shield",
            "web",
            "chest",
            "bucket",
            "bottle",
            "banner",
        )

        for (shortName in junk)
            if (stack.displayName.lowercase(Locale.getDefault()).contains(shortName)
                || item.unlocalizedName.lowercase(Locale.getDefault()).contains(shortName))
                return true

        return false
    }

    fun isBestTool(stack: ItemStack, tool: ItemTool): Boolean {
        for (i in 0..44) {
            val slot2: Slot = mc.thePlayer.inventoryContainer.getSlot(i)
            if(!slot2.hasStack) continue
            val stack2: ItemStack = slot2.stack
            if (stack != stack2 && stack2.item is ItemTool) {
                val item = stack2.item as ItemTool
                if(preferredBlock(item.javaClass) != preferredBlock(tool.javaClass))
                    continue
                val block = preferredBlock(item.javaClass)
                if (tool.getStrVsBlock(stack2, block) < item.getStrVsBlock(stack, block))
                    return false
            }
        }
        return true
    }

    fun isBestSword(stack: ItemStack, sword: ItemSword): Boolean {
        for (i in 0..44) {
            val slot2: Slot = mc.thePlayer.inventoryContainer.getSlot(i)
            if(!slot2.hasStack) continue
            val comparingStack: ItemStack = slot2.stack
            if (stack != comparingStack && comparingStack.item is ItemSword) {
                val item = comparingStack.item as ItemSword
                if (sword.attackDamage + getSwordStrength(stack) < item.attackDamage + getSwordStrength(comparingStack))
                    return false
            }
        }

        return true
    }

    fun isBestArmor(stack: ItemStack, armor: ItemArmor): Boolean {
        var equippedReduction = 0
        var checkReduction = 0
        if (mc.thePlayer.inventory.getStackInSlot(39 - armor.armorType) != null) {
            val equippedArmor = mc.thePlayer.inventory.getStackInSlot(39 - armor.armorType).item as ItemArmor
            val equippedStack = mc.thePlayer.inventory.getStackInSlot(39 - armor.armorType)

            equippedReduction = equippedArmor.armorMaterial.getDamageReductionAmount(armor.armorType)
            equippedReduction += checkProtection(mc.thePlayer.inventory.getStackInSlot(39 - armor.armorType))

            checkReduction = armor.armorMaterial.getDamageReductionAmount(armor.armorType)
            checkReduction += checkProtection(stack)

            return checkReduction > equippedReduction || (checkReduction == equippedReduction && stack.itemDamage < equippedStack.itemDamage)
        }
        return true
    }

    fun dropItem(slot: Int): Boolean {
        val slo2t: Slot = mc.thePlayer.inventoryContainer.getSlot(slot)
        val stack: ItemStack = slo2t.stack
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer)
        return true
    }

    fun preferredBlock(clazz: Class<*>): Block? {
        return if (clazz == ItemPickaxe::class.java) Blocks.cobblestone else if (clazz == ItemAxe::class.java) Blocks.log else Blocks.dirt
    }

    fun getSwordStrength(stack: ItemStack): Float {
        return (if (stack.item !is ItemSword) 0.0f else EnchantmentHelper.getEnchantmentLevel(
            Enchantment.sharpness.effectId,
            stack
        ).toFloat() * 1.25f) + if (stack.item !is ItemSword) 0.0f else EnchantmentHelper.getEnchantmentLevel(
            Enchantment.fireAspect.effectId,
            stack
        ).toFloat()
    }

    fun checkProtection(item: ItemStack): Int {
        return EnchantmentHelper.getEnchantmentLevel(0, item)
    }

}