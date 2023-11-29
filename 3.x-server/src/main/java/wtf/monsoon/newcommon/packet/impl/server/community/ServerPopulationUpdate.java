package wtf.monsoon.newcommon.packet.impl.server.community;


import wtf.monsoon.newcommon.community.Message;
import wtf.monsoon.newcommon.community.User;
import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;
import wtf.monsoon.newcommon.packet.type.ServerPacket;

import java.util.List;
import java.util.UUID;

public final class ServerPopulationUpdate extends ServerPacket {

    public User user;
    public UUID uuid;
    public Action action;

    public ServerPopulationUpdate(User user, UUID uuid, Action action) {
        this.user = user;
        this.uuid = uuid;
        this.action = action;

        this.type = EnumPacketType.SERVER_POPULATION_UPDATE;
    }

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public enum Action {
        ADD, REMOVE
    }
}
