package wtf.monsoon.newcommon.packet;

import wtf.monsoon.newcommon.packet.impl.client.community.*;
import wtf.monsoon.newcommon.packet.impl.client.login.*;
import wtf.monsoon.newcommon.packet.impl.server.community.*;
import wtf.monsoon.newcommon.packet.impl.server.login.*;
import wtf.monsoon.newcommon.packet.impl.server.misc.*;

public enum EnumPacketType {

    // Client packets
    CLIENT_COMMUNITY_MESSAGE_SEND(ClientCommunityMessageSend.class),
    CLIENT_POPULATION_REQUEST(ClientPopulationRequest.class),
    CLIENT_LOGIN(ClientLoginPacket.class),

    // Server packets
    SERVER_COMMUNITY_MESSAGE_SEND(ServerCommunityMessageSend.class),
    SERVER_POPULATION_RESPONSE(ServerPopulationResponse.class),
    SERVER_POPULATION_UPDATE(ServerPopulationUpdate.class),
    SERVER_INVALID_ACCESS_TOKEN_RESPONSE(ServerInvalidAccessTokenResponse.class),
    SERVER_LOGIN_RESPONSE(ServerLoginResponse.class),
    SERVER_ERROR_MESSAGE(ServerErrorResponse.class);

    Class<? extends Packet<?>> clazz;

    EnumPacketType(Class<? extends Packet<?>> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Packet<?>> getPacketClass() {
        return clazz;
    }
}
