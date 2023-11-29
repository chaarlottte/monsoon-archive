package wtf.monsoon.backend.manager.script.link

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier

/**
 * @author surge
 * @since 28/03/2023
 */
class EntityWrapper(@Excluded val entity: Entity) {

    @Identifier("get_name")
    fun getName(): String = entity.displayName.unformattedText

    @Identifier("get_health")
    fun getHealth(): Float = if (entity is EntityLivingBase) entity.health else 0f

    @Identifier("get_position")
    fun getPosition(): PlayerLink.Vector3 = PlayerLink.Vector3(entity.posX.toFloat(), entity.posY.toFloat(), entity.posZ.toFloat())

}