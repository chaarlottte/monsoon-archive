package wtf.monsoon.newcommon.packet;

import wtf.monsoon.newcommon.packet.handler.PacketHandler;

import java.io.*;

public class Packet<T extends PacketHandler> implements Serializable {
    public EnumPacketType type;
    public void process(final T handler) { }

    public EnumPacketType getType() { return type; }
}
