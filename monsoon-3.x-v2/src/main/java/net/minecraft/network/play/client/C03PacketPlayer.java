package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import spritz.api.annotations.Identifier;

public class C03PacketPlayer implements Packet<INetHandlerPlayServer>
{
    @Identifier(identifier = "x")
    public double x;

    @Identifier(identifier = "y")
    public double y;

    @Identifier(identifier = "z")
    public double z;

    @Identifier(identifier = "yaw")
    public float yaw;

    @Identifier(identifier = "pitch")
    public float pitch;

    @Identifier(identifier = "on_ground")
    public boolean onGround;

    @Identifier(identifier = "moving")
    public boolean moving;

    @Identifier(identifier = "rotating")
    public boolean rotating;

    public C03PacketPlayer()
    {
    }

    public C03PacketPlayer(boolean isOnGround)
    {
        this.onGround = isOnGround;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayer(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.onGround = buf.readUnsignedByte() != 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.onGround ? 1 : 0);
    }

    @Identifier(identifier = "getPositionX")
    public double getPositionX()
    {
        return this.x;
    }

    @Identifier(identifier = "getPositionY")
    public double getPositionY()
    {
        return this.y;
    }

    @Identifier(identifier = "getPositionZ")
    public double getPositionZ()
    {
        return this.z;
    }

    @Identifier(identifier = "getYaw")
    public float getYaw()
    {
        return this.yaw;
    }

    @Identifier(identifier = "getPitch")
    public float getPitch()
    {
        return this.pitch;
    }

    @Identifier(identifier = "isOnGround")
    public boolean isOnGround()
    {
        return this.onGround;
    }

    @Identifier(identifier = "isMoving")
    public boolean isMoving()
    {
        return this.moving;
    }

    @Identifier(identifier = "getRotating")
    public boolean getRotating()
    {
        return this.rotating;
    }

    @Identifier(identifier = "setMoving")
    public void setMoving(boolean isMoving)
    {
        this.moving = isMoving;
    }

    public static class C04PacketPlayerPosition extends C03PacketPlayer
    {
        public C04PacketPlayerPosition()
        {
            this.moving = true;
        }

        public C04PacketPlayerPosition(double posX, double posY, double posZ, boolean isOnGround)
        {
            this.x = posX;
            this.y = posY;
            this.z = posZ;
            this.onGround = isOnGround;
            this.moving = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            super.readPacketData(buf);
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
            super.writePacketData(buf);
        }
    }

    public static class C05PacketPlayerLook extends C03PacketPlayer
    {
        public C05PacketPlayerLook()
        {
            this.rotating = true;
        }

        public C05PacketPlayerLook(float playerYaw, float playerPitch, boolean isOnGround)
        {
            this.yaw = playerYaw;
            this.pitch = playerPitch;
            this.onGround = isOnGround;
            this.rotating = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            this.yaw = buf.readFloat();
            this.pitch = buf.readFloat();
            super.readPacketData(buf);
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            buf.writeFloat(this.yaw);
            buf.writeFloat(this.pitch);
            super.writePacketData(buf);
        }
    }

    public static class C06PacketPlayerPosLook extends C03PacketPlayer
    {
        public C06PacketPlayerPosLook()
        {
            this.moving = true;
            this.rotating = true;
        }

        public C06PacketPlayerPosLook(double playerX, double playerY, double playerZ, float playerYaw, float playerPitch, boolean playerIsOnGround)
        {
            this.x = playerX;
            this.y = playerY;
            this.z = playerZ;
            this.yaw = playerYaw;
            this.pitch = playerPitch;
            this.onGround = playerIsOnGround;
            this.rotating = true;
            this.moving = true;
        }

        public void readPacketData(PacketBuffer buf) throws IOException
        {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
            this.yaw = buf.readFloat();
            this.pitch = buf.readFloat();
            super.readPacketData(buf);
        }

        public void writePacketData(PacketBuffer buf) throws IOException
        {
            buf.writeDouble(this.x);
            buf.writeDouble(this.y);
            buf.writeDouble(this.z);
            buf.writeFloat(this.yaw);
            buf.writeFloat(this.pitch);
            super.writePacketData(buf);
        }
    }
}
