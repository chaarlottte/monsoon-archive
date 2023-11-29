package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import spritz.api.annotations.Identifier;

public class C0BPacketEntityAction implements Packet<INetHandlerPlayServer>
{
    @Identifier(identifier = "entity_id")
    public int entityID;

    @Identifier(identifier = "action")
    public C0BPacketEntityAction.Action action;

    @Identifier(identifier = "aux_data")
    public int auxData;

    public C0BPacketEntityAction()
    {
    }

    public C0BPacketEntityAction(Entity entity, C0BPacketEntityAction.Action action)
    {
        this(entity, action, 0);
    }

    public C0BPacketEntityAction(Entity entity, C0BPacketEntityAction.Action action, int auxData)
    {
        this.entityID = entity.getEntityId();
        this.action = action;
        this.auxData = auxData;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityID = buf.readVarIntFromBuffer();
        this.action = (C0BPacketEntityAction.Action)buf.readEnumValue(C0BPacketEntityAction.Action.class);
        this.auxData = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityID);
        buf.writeEnumValue(this.action);
        buf.writeVarIntToBuffer(this.auxData);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processEntityAction(this);
    }

    @Identifier(identifier = "get_action")
    public C0BPacketEntityAction.Action getAction()
    {
        return this.action;
    }

    @Identifier(identifier = "get_aux_data")
    public int getAuxData()
    {
        return this.auxData;
    }

    @Identifier(identifier = "Action")
    public static enum Action
    {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        RIDING_JUMP,
        OPEN_INVENTORY;
    }
}