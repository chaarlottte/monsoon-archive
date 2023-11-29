package wtf.monsoon.api.util.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

/**
 * @author Surge
 * @since 27/07/2022
 */
public class EntityUtil {

    /**
     * Checks if an entity is passive
     *
     * @param entityIn The entity to check
     * @return Whether the entity is passive
     */
    public static boolean isPassive(Entity entityIn) {
        // The entity is not an angry wolf
        if (entityIn instanceof EntityWolf) {
            return !((EntityWolf) entityIn).isAngry();
        }

        // The entity is not an angry iron golem
        if (entityIn instanceof EntityIronGolem) {
            return ((EntityIronGolem) entityIn).getAITarget() == null;
        }

        // Passive creatures
        return entityIn instanceof EntityAgeable || entityIn instanceof EntityAmbientCreature || entityIn instanceof EntitySquid;
    }

    /**
     * Checks if an entity is hostile
     *
     * @param entityIn The entity to check
     * @return Whether the entity is hostile
     */
    public static boolean isHostile(Entity entityIn) {
        return (entityIn instanceof EntityMob && !isNeutral(entityIn)) || entityIn instanceof EntitySpider;
    }

    /**
     * Checks if an entity is neutral
     *
     * @param entityIn The entity to check
     * @return Whether the entity is neutral
     */
    public static boolean isNeutral(Entity entityIn) {
        return entityIn instanceof EntityPigZombie && !((EntityPigZombie) entityIn).isAngry() || entityIn instanceof EntityWolf && !((EntityWolf) entityIn).isAngry() || entityIn instanceof EntityEnderman && ((EntityEnderman) entityIn).isScreaming();
    }

    /**
     * Gets the interpolated position of an entity
     *
     * @param entityIn The entity to get the interpolated position of
     * @return The interpolated position of an entity
     */
    public static Vec3 getInterpolatedPosition(Entity entityIn) {
        return new Vec3(entityIn.lastTickPosX, entityIn.lastTickPosY, entityIn.lastTickPosZ).add(getInterpolatedAmount(entityIn, Minecraft.getMinecraft().getTimer().renderPartialTicks));
    }

    /**
     * Gets the amount to interpolate by
     *
     * @param entity       The entity to get the interpolation amount of
     * @param partialTicks Delta I think
     * @return The interpolation amount
     */
    private static Vec3 getInterpolatedAmount(Entity entity, float partialTicks) {
        return new Vec3((entity.posX - entity.lastTickPosX) * partialTicks, (entity.posY - entity.lastTickPosY) * partialTicks, (entity.posZ - entity.lastTickPosZ) * partialTicks);
    }

    /**
     * Gets the text colour based on an entity's health
     *
     * @param entity The entity to use
     * @return The text colour
     */
    public static EnumChatFormatting getTextColourFromEntityHealth(EntityLivingBase entity) {
        float health = getTotalHealth(entity);

        if (health > 20) {
            return EnumChatFormatting.YELLOW;
        } else if (health <= 20 && health > 15) {
            return EnumChatFormatting.GREEN;
        } else if (health <= 15 && health > 10) {
            return EnumChatFormatting.GOLD;
        } else if (health <= 10 && health > 5) {
            return EnumChatFormatting.RED;
        } else if (health <= 5) {
            return EnumChatFormatting.DARK_RED;
        }

        return EnumChatFormatting.GRAY;
    }

    /**
     * Gets an entity's total health
     *
     * @param entity The entity to get the total health of
     * @return The total health of the entity
     */
    public static float getTotalHealth(EntityLivingBase entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

}
