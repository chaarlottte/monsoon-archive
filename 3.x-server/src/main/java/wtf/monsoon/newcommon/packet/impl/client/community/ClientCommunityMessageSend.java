package wtf.monsoon.newcommon.packet.impl.client.community;


import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IClientPacketHandler;
import wtf.monsoon.newcommon.packet.type.ClientPacket;

public final class ClientCommunityMessageSend extends ClientPacket {
    public String message;

    public ClientCommunityMessageSend(String message) {
        this.message = message;

        this.type = EnumPacketType.CLIENT_COMMUNITY_MESSAGE_SEND;
    }

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
