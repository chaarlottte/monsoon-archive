package wtf.monsoon.backend.manager.script.link

import net.minecraft.entity.EntityLivingBase
import spritz.api.Coercion
import spritz.api.annotations.Identifier
import spritz.value.Value

/**
 * @author surge
 * @since 28/03/2023
 */
class WorldLink : Link() {

    @Identifier("get_loaded_entities")
    fun getLoadedEntities(): List<Value> = mc.theWorld.loadedEntityList.map { Coercion.IntoSpritz.coerce(EntityWrapper(it)) }

    @Identifier("get_living_entities")
    fun getLivingEntities(): List<Value> = mc.theWorld.loadedEntityList.filterIsInstance<EntityLivingBase>().map { Coercion.IntoSpritz.coerce(EntityWrapper(it)) }

}