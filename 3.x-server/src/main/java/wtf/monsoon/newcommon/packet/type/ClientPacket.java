package wtf.monsoon.newcommon.packet.type;

import wtf.monsoon.newcommon.packet.Packet;
import wtf.monsoon.newcommon.packet.handler.impl.IClientPacketHandler;

public class ClientPacket extends Packet<IClientPacketHandler> {
    @Override
    public void process(final IClientPacketHandler handler) {};
}
