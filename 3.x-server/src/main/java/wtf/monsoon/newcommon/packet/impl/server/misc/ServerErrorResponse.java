package wtf.monsoon.newcommon.packet.impl.server.misc;

import wtf.monsoon.newcommon.community.Message;
import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IServerPacketHandler;
import wtf.monsoon.newcommon.packet.type.ServerPacket;

public class ServerErrorResponse extends ServerPacket {
    public String message;

    public ServerErrorResponse(String message) {
        this.message = message;

        this.type = EnumPacketType.SERVER_ERROR_MESSAGE;
    }

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
