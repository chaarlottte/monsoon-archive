package wtf.monsoon.api.util.entity;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import wtf.monsoon.Wrapper;


public class EntityFakePlayer extends EntityOtherPlayerMP {
    public EntityFakePlayer() {
        super(Wrapper.getMinecraft().theWorld, Wrapper.getMinecraft().thePlayer.getGameProfile());
        copyLocationAndAnglesFrom(Wrapper.getMinecraft().thePlayer);

        inventory.copyInventory(Wrapper.getMinecraft().thePlayer.inventory);

        rotationYawHead = Wrapper.getMinecraft().thePlayer.rotationYawHead;
        renderYawOffset = Wrapper.getMinecraft().thePlayer.renderYawOffset;
        rotationPitchHead = Wrapper.getMinecraft().thePlayer.rotationPitchHead;

        chasingPosX = posX;
        chasingPosY = posY;
        chasingPosZ = posZ;

        Wrapper.getMinecraft().theWorld.addEntityToWorld(getEntityId(), this);
    }

    public void updateLocation() {
        copyLocationAndAnglesFrom(Wrapper.getMinecraft().thePlayer);

        inventory.copyInventory(Wrapper.getMinecraft().thePlayer.inventory);

        rotationYawHead = Wrapper.getMinecraft().thePlayer.rotationYawHead;
        renderYawOffset = Wrapper.getMinecraft().thePlayer.renderYawOffset;
        rotationPitchHead = Wrapper.getMinecraft().thePlayer.rotationPitchHead;

        chasingPosX = posX;
        chasingPosY = posY;
        chasingPosZ = posZ;

        Wrapper.getMinecraft().theWorld.addEntityToWorld(getEntityId(), this);
    }

    public void despawn() {
        Wrapper.getMinecraft().theWorld.removeEntityFromWorld(getEntityId());
    }
}