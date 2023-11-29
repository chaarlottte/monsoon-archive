package viamcp.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventAttackEntity;

public class AttackOrder
{
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final int VER_1_8_ID = 47;

    public static void sendConditionalSwing(MovingObjectPosition mop)
    {
        if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
        {
            mc.thePlayer.swingItem();
        }
    }

    public static void sendFixedAttack(EntityPlayer entityIn, Entity target)
    {
        // Using this instead of ViaMCP.PROTOCOL_VERSION so does not need to be changed between 1.8.x and 1.12.2 base
        // getVersion() can be null, but not in this case, as ID 47 exists, if not removed
        if(ViaMCP.getInstance().getVersion() <= 47) {
            send1_8Attack(entityIn, target);
        } else {
            send1_9Attack(entityIn, target);
        }
    }

    private static void send1_8Attack(EntityPlayer entityIn, Entity targetEntity)
    {
        EventAttackEntity eventAttackEntity = new EventAttackEntity(targetEntity);
        Wrapper.getEventBus().post(eventAttackEntity);

        mc.thePlayer.swingItem();

        mc.playerController.syncCurrentPlayItem();
        mc.playerController.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));

        if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
            entityIn.attackTargetEntityWithCurrentItem(targetEntity);
        }
    }

    private static void send1_9Attack(EntityPlayer entityIn, Entity targetEntity) {
        EventAttackEntity eventAttackEntity = new EventAttackEntity(targetEntity);
        Wrapper.getEventBus().post(eventAttackEntity);

        mc.playerController.syncCurrentPlayItem();
        mc.playerController.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));

        if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
            entityIn.attackTargetEntityWithCurrentItem(targetEntity);
        }

        mc.thePlayer.swingItem();
    }
}
