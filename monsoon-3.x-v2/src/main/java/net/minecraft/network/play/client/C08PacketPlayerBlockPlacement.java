package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import spritz.api.annotations.Excluded;
import spritz.api.annotations.Identifier;
import wtf.monsoon.backend.manager.script.link.PlayerLink;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer>
{
    private static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);

    @Excluded
    public BlockPos position;

    @Identifier(identifier = "placed_block_direction")
    public int placedBlockDirection;
    public ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;

    public C08PacketPlayerBlockPlacement()
    {
    }

    public C08PacketPlayerBlockPlacement(ItemStack stackIn)
    {
        this(field_179726_a, 255, stackIn, 0.0F, 0.0F, 0.0F);
    }

    public C08PacketPlayerBlockPlacement(BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn)
    {
        this.position = positionIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.stack = stackIn != null ? stackIn.copy() : null;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.position = buf.readBlockPos();
        this.placedBlockDirection = buf.readUnsignedByte();
        this.stack = buf.readItemStackFromBuffer();
        this.facingX = (float)buf.readUnsignedByte() / 16.0F;
        this.facingY = (float)buf.readUnsignedByte() / 16.0F;
        this.facingZ = (float)buf.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.position);
        buf.writeByte(this.placedBlockDirection);
        buf.writeItemStackToBuffer(this.stack);
        buf.writeByte((int)(this.facingX * 16.0F));
        buf.writeByte((int)(this.facingY * 16.0F));
        buf.writeByte((int)(this.facingZ * 16.0F));
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayerBlockPlacement(this);
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

    @Identifier(identifier = "get_placed_block_direction")
    public int getPlacedBlockDirection()
    {
        return this.placedBlockDirection;
    }

    @Identifier(identifier = "get_stack")
    public ItemStack getStack()
    {
        return this.stack;
    }

    /**
     * Returns the offset from xPosition where the actual click took place.
     */
    @Identifier(identifier = "get_placed_block_offset_x")
    public float getPlacedBlockOffsetX()
    {
        return this.facingX;
    }

    /**
     * Returns the offset from yPosition where the actual click took place.
     */
    @Identifier(identifier = "get_placed_block_offset_y")
    public float getPlacedBlockOffsetY()
    {
        return this.facingY;
    }

    /**
     * Returns the offset from zPosition where the actual click took place.
     */
    @Identifier(identifier = "get_placed_block_offset_z")
    public float getPlacedBlockOffsetZ()
    {
        return this.facingZ;
    }
}
