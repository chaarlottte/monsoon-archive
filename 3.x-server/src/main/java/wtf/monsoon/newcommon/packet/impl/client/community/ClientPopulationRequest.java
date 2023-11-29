package wtf.monsoon.newcommon.packet.impl.client.community;


import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IClientPacketHandler;
import wtf.monsoon.newcommon.packet.type.ClientPacket;

public final class ClientPopulationRequest extends ClientPacket {
    public String accessToken;

    public ClientPopulationRequest(String accessToken) {
        this.accessToken = accessToken;

        this.type = EnumPacketType.CLIENT_POPULATION_REQUEST;
    }

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
