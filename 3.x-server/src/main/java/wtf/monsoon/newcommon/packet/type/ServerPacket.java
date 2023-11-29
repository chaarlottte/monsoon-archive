package wtf.monsoon.newcommon.packet.type;

import wtf.monsoon.newcommon.packet.Packet;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;

public class ServerPacket extends Packet<IServerPacketHandler> {
    @Override
    public void process(final IServerPacketHandler handler) {};
}
