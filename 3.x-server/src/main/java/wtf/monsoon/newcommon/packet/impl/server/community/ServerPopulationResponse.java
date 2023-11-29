package wtf.monsoon.newcommon.packet.impl.server.community;


import wtf.monsoon.newcommon.community.User;
import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;
import wtf.monsoon.newcommon.packet.type.ServerPacket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerPopulationResponse extends ServerPacket {

    public LinkedHashMap<UUID, User> userMap;

    public ServerPopulationResponse(LinkedHashMap<UUID, User> userMap) {
        this.userMap = userMap;

        this.type = EnumPacketType.SERVER_POPULATION_RESPONSE;
    }

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
