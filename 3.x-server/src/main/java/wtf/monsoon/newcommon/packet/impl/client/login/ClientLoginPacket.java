package wtf.monsoon.newcommon.packet.impl.client.login;


import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.handler.impl.IClientPacketHandler;
import wtf.monsoon.newcommon.packet.type.ClientPacket;

public final class ClientLoginPacket extends ClientPacket {

    private final String username, hostName, systemName, osName, hardwareID;

    public ClientLoginPacket(String username, String hostName, String systemName, String osName, String hardwareID) {
        this.username = username;
        this.hostName = hostName;
        this.systemName = systemName;
        this.osName = osName;
        this.hardwareID = hardwareID;

        this.type = EnumPacketType.CLIENT_LOGIN;
    }

    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }

    public String getUsername() {
        return username;
    }

    public String getHostName() {
        return hostName;
    }

    public String getSystemName() {
        return systemName;
    }

    public String getOsName() {
        return osName;
    }

    public String getHardwareID() {
        return hardwareID;
    }

}
