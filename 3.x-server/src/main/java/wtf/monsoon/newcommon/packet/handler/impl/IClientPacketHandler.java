package wtf.monsoon.newcommon.packet.handler.impl;

import wtf.monsoon.newcommon.packet.impl.client.community.*;
import wtf.monsoon.newcommon.packet.impl.client.login.*;
import wtf.monsoon.newcommon.packet.handler.PacketHandler;

public interface IClientPacketHandler extends PacketHandler {

    void handle(final ClientLoginPacket packet);
    void handle(final ClientCommunityMessageSend packet);
    void handle(final ClientPopulationRequest packet);

}
