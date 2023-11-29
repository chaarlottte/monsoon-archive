package wtf.monsoon.newcommon.packet.impl.server.community;


import wtf.monsoon.newcommon.community.Message;
import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;
import wtf.monsoon.newcommon.packet.type.ServerPacket;

public final class ServerCommunityMessageSend extends ServerPacket {
    public Message message;

    public ServerCommunityMessageSend(Message message) {
        this.message = message;

        this.type = EnumPacketType.SERVER_COMMUNITY_MESSAGE_SEND;
    }

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
