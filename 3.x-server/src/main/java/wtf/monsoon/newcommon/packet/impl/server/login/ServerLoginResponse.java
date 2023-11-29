package wtf.monsoon.newcommon.packet.impl.server.login;

import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;
import wtf.monsoon.newcommon.packet.type.ServerPacket;
import wtf.monsoon.newcommon.vantage.api.models.AuthResponse;

public final class ServerLoginResponse extends ServerPacket {

    private final boolean success;
    private final AuthResponse resp;
    private final String information;

    public ServerLoginResponse(boolean success, AuthResponse resp, String information) {
        this.success = success;
        this.resp = resp;
        this.information = information;

        this.type = EnumPacketType.SERVER_LOGIN_RESPONSE;
    }

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public AuthResponse getResp() {
        return resp;
    }

    public String getInformation() {
        return information;
    }

    public boolean isSuccess() {
        return success;
    }
}
