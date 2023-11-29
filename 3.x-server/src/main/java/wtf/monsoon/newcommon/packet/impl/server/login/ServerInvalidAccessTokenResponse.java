package wtf.monsoon.newcommon.packet.impl.server.login;

import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;
import wtf.monsoon.newcommon.packet.type.ServerPacket;
import wtf.monsoon.newcommon.vantage.api.models.AuthResponse;

public final class ServerInvalidAccessTokenResponse extends ServerPacket {

    public ServerInvalidAccessTokenResponse() {
        this.type = EnumPacketType.SERVER_INVALID_ACCESS_TOKEN_RESPONSE;
    }

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
