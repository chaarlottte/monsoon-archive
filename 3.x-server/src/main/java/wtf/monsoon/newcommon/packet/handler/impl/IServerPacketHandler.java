package wtf.monsoon.newcommon.packet.handler.impl;

import wtf.monsoon.newcommon.packet.impl.server.community.*;
import wtf.monsoon.newcommon.packet.impl.server.login.*;
import wtf.monsoon.newcommon.packet.impl.server.misc.*;

import wtf.monsoon.newcommon.packet.handler.PacketHandler;

public interface IServerPacketHandler extends PacketHandler {
    void handle(final ServerLoginResponse packet);
    void handle(final ServerCommunityMessageSend packet);
    void handle(final ServerPopulationUpdate packet);
    void handle(final ServerPopulationResponse packet);
    void handle(final ServerInvalidAccessTokenResponse packet);
    void handle(final ServerErrorResponse packet);
}
