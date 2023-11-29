package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import spritz.api.annotations.Identifier;

public class C13PacketPlayerAbilities implements Packet<INetHandlerPlayServer>
{
    @Identifier(identifier = "invulnerable")
    public boolean invulnerable;

    @Identifier(identifier = "flying")
    public boolean flying;

    @Identifier(identifier = "allow_flying")
    public boolean allowFlying;

    @Identifier(identifier = "creative_mode")
    public boolean creativeMode;

    @Identifier(identifier = "fly_speed")
    public float flySpeed;

    @Identifier(identifier = "walk_speed")
    public float walkSpeed;

    public C13PacketPlayerAbilities()
    {
    }

    public C13PacketPlayerAbilities(PlayerCapabilities capabilities)
    {
        this.setInvulnerable(capabilities.disableDamage);
        this.setFlying(capabilities.isFlying);
        this.setAllowFlying(capabilities.allowFlying);
        this.setCreativeMode(capabilities.isCreativeMode);
        this.setFlySpeed(capabilities.getFlySpeed());
        this.setWalkSpeed(capabilities.getWalkSpeed());
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        byte b0 = buf.readByte();
        this.setInvulnerable((b0 & 1) > 0);
        this.setFlying((b0 & 2) > 0);
        this.setAllowFlying((b0 & 4) > 0);
        this.setCreativeMode((b0 & 8) > 0);
        this.setFlySpeed(buf.readFloat());
        this.setWalkSpeed(buf.readFloat());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        byte b0 = 0;

        if (this.isInvulnerable())
        {
            b0 = (byte)(b0 | 1);
        }

        if (this.isFlying())
        {
            b0 = (byte)(b0 | 2);
        }

        if (this.isAllowFlying())
        {
            b0 = (byte)(b0 | 4);
        }

        if (this.isCreativeMode())
        {
            b0 = (byte)(b0 | 8);
        }

        buf.writeByte(b0);
        buf.writeFloat(this.flySpeed);
        buf.writeFloat(this.walkSpeed);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayerAbilities(this);
    }

    @Identifier(identifier = "is_invulnerable")
    public boolean isInvulnerable()
    {
        return this.invulnerable;
    }

    @Identifier(identifier = "set_invulnerable")
    public void setInvulnerable(boolean isInvulnerable)
    {
        this.invulnerable = isInvulnerable;
    }

    @Identifier(identifier = "is_flying")
    public boolean isFlying()
    {
        return this.flying;
    }

    @Identifier(identifier = "set_flying")
    public void setFlying(boolean isFlying)
    {
        this.flying = isFlying;
    }

    @Identifier(identifier = "is_allow_flying")
    public boolean isAllowFlying()
    {
        return this.allowFlying;
    }

    @Identifier(identifier = "set_allow_flying")
    public void setAllowFlying(boolean isAllowFlying)
    {
        this.allowFlying = isAllowFlying;
    }

    @Identifier(identifier = "is_creative_mode")
    public boolean isCreativeMode()
    {
        return this.creativeMode;
    }

    @Identifier(identifier = "set_creative_mode")
    public void setCreativeMode(boolean isCreativeMode)
    {
        this.creativeMode = isCreativeMode;
    }

    @Identifier(identifier = "set_fly_speed")
    public void setFlySpeed(float flySpeedIn)
    {
        this.flySpeed = flySpeedIn;
    }

    @Identifier(identifier = "set_walk_speed")
    public void setWalkSpeed(float walkSpeedIn)
    {
        this.walkSpeed = walkSpeedIn;
    }
}
