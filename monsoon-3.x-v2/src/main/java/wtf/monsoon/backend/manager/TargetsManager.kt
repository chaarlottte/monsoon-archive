package wtf.monsoon.backend.manager

import me.bush.eventbuskotlin.EventListener
import me.bush.eventbuskotlin.listener
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import wtf.monsoon.client.event.EventClientTick
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.stream.Collectors

class TargetsManager : ConcurrentLinkedQueue<TargetsManager.Target>() {

    val mc: Minecraft = Minecraft.getMinecraft()

    var players = true
    var invisibles = false
    var animals = false
    var mobs = false
    var teams = false

    private var loadedEntitySize = 0

    @EventListener
    val clientTick = fun(_: EventClientTick) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return

        if (mc.thePlayer.ticksExisted % 150 == 0 || loadedEntitySize != mc.theWorld.loadedEntityList.size) {
            this.updateTargets()
            this.loadedEntitySize = mc.theWorld.loadedEntityList.size
        }
    }

    private fun updateTargets() {
        mc.theWorld.loadedEntityList.forEach { entity: Entity ->
            if(entity is EntityLivingBase) {
                this.add(
                    Target(
                        entity, entity.isInvisible,
                        entity is EntityAnimal, entity is EntityMob
                    )
                )
            }
        }
    }

    fun getTargets(range: Double, validityCheck: (e: EntityLivingBase) -> Boolean = { true }): List<EntityLivingBase> {
        val targets: List<Target> = stream()
            .filter { target: Target ->
                mc.thePlayer.getDistanceToEntity(
                    target.entity
                ) < range
            }
            .filter { target: Target ->
                mc.theWorld.loadedEntityList.contains(
                    target.entity
                )
            }
            .filter { target: Target ->
                validityCheck.invoke(target.entity)
            }
            .sorted(Comparator.comparingDouble { target: Target ->
                mc.thePlayer.getDistanceSqToEntity(
                    target.entity
                )
            }).collect(Collectors.toList())

        val entities: ArrayList<EntityLivingBase> = ArrayList()

        targets.forEach {
            entities.add(it.entity)
        }

        return entities
    }

    data class Target(var entity: EntityLivingBase, var invisible: Boolean, var animal: Boolean, var monster: Boolean)

}