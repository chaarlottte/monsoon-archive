package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import spritz.api.annotations.Excluded;
import spritz.api.annotations.Identifier;
import wtf.monsoon.backend.manager.script.link.PlayerLink;

public class C07PacketPlayerDigging implements Packet<INetHandlerPlayServer>
{
    private BlockPos position;
    private EnumFacing facing;

    /** Status of the digging (started, ongoing, broken). */
    private C07PacketPlayerDigging.Action status;

    public C07PacketPlayerDigging()
    {
    }

    public C07PacketPlayerDigging(C07PacketPlayerDigging.Action statusIn, BlockPos posIn, EnumFacing facingIn)
    {
        this.status = statusIn;
        this.position = posIn;
        this.facing = facingIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.status = (C07PacketPlayerDigging.Action)buf.readEnumValue(C07PacketPlayerDigging.Action.class);
        this.position = buf.readBlockPos();
        this.facing = EnumFacing.getFront(buf.readUnsignedByte());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.status);
        buf.writeBlockPos(this.position);
        buf.writeByte(this.facing.getIndex());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayerDigging(this);
    }

    @Excluded
    public BlockPos getPosition()
    {
        return this.position;
    }

    @Identifier(identifier = "get_position")
    public PlayerLink.Vector3 getPositionS() {
        BlockPos pos = getPosition();
        return new PlayerLink.Vector3(pos.getX(), pos.getY(), pos.getZ());
    }

    @Identifier(identifier = "get_facing")
    public EnumFacing getFacing()
    {
        return this.facing;
    }

    @Identifier(identifier = "get_status")
    public C07PacketPlayerDigging.Action getStatus()
    {
        return this.status;
    }

    @Identifier(identifier = "get_action")
    public static enum Action {
        @Identifier(identifier = "START_DESTROY_BLOCK")
        START_DESTROY_BLOCK,

        @Identifier(identifier = "ABORT_DESTROY_BLOCK")
        ABORT_DESTROY_BLOCK,

        @Identifier(identifier = "STOP_DESTROY_BLOCK")
        STOP_DESTROY_BLOCK,

        @Identifier(identifier = "DROP_ALL_ITEMS")
        DROP_ALL_ITEMS,

        @Identifier(identifier = "DROP_ITEM")
        DROP_ITEM,

        @Identifier(identifier = "RELEASE_USE_ITEM")
        RELEASE_USE_ITEM;
    }
}
