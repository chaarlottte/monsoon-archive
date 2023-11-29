package wtf.monsoon.misc.server;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.luaj.vm2.ast.Str;
import wtf.monsoon.Wrapper;
import wtf.monsoon.misc.server.packet.MPacket;
import wtf.monsoon.misc.server.packet.impl.*;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MonsoonServerConnection {

    @Setter
    private String address;

    @Setter
    private int port;

    @Getter @Setter
    private Socket socket;

    @Getter @Setter
    private String loginToken;

    @Getter
    private Map<String, String> onlineMonsoonUsers;

    @Getter @Setter
    private String motd;

    public MonsoonServerConnection(String address, int port) {
        setAddress(address);
        setPort(port);
    }

    public void login() throws Exception {
        Socket loginSocket = new Socket(address, port);

        Wrapper.getLogger().info("Connecting to Monsoon servers...");

        while (!loginSocket.isConnected()) {}

        if(loginSocket.isConnected()) {
            Wrapper.getLogger().info("Connected to Monsoon servers!");
            sendPacket(new MPacketLogin(), loginSocket);

            Scanner scanner = new Scanner(loginSocket.getInputStream());
            while(scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                Wrapper.getLogger().debug("got packet: " + nextLine);

                JSONObject packet = new JSONObject(nextLine);
                if(!packet.isNull("response")) {
                    if(packet.getString("response").equalsIgnoreCase("ok")) {
                        Wrapper.getLogger().debug("Got response, connecting to new server...");
                        setLoginToken(packet.getJSONObject("data").getString("token"));
                        loginSocket.close();
                        useServer(packet.getJSONObject("data").getString("ip"), packet.getJSONObject("data").getInt("port"));
                    }
                }
            }
        }
    }

    public void useServer(String ip, int port) throws Exception {
        Wrapper.getLogger().info("Connecting to client server...");

        onlineMonsoonUsers = new HashMap<>();

        socket = new Socket(ip, port);

        sendPacket(new MPacketLogin(getLoginToken()));

        Scanner scanner = new Scanner(socket.getInputStream());
        while(scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            Wrapper.getLogger().debug("got packet: " + nextLine);

            JSONObject packet = new JSONObject(nextLine);
            String packetId = packet.getString("id");

            switch(packetId) {
                case "s2":
                    if(packet.getString("response").equalsIgnoreCase("ok")) {
                        JSONObject data = packet.getJSONObject("data");

                        setMotd(data.getString("motd"));

                        /*JSONArray onlineUsers = data.getJSONArray("onlineUsers");
                        for(int i = 0; i < onlineUsers.length(); i++) {
                            String onlineUser = onlineUsers.getString(i);
                            onlineMonsoonUsers.put(onlineUser.split(":")[0], onlineUser.split(":")[1]); // is sent back to us as minecraftusername:monsoonusername
                        }*/

                        double serverVersion = Double.parseDouble(data.getString("clientVersion")), clientVersion = Double.parseDouble(Wrapper.getMonsoon().getVersion());

                        if(serverVersion != clientVersion) {
                            Wrapper.getLogger().error("You are currently running a" + (serverVersion > clientVersion ? "n older" : "newer") + " version of Monsoon than our servers! Use this at your own risk.");
                        }
                    }
                    break;
                case "s3":
                    JSONArray onlineUsers = packet.getJSONArray("onlineUsers");
                    onlineMonsoonUsers.clear();
                    for(int i = 0; i < onlineUsers.length(); i++) {
                        String onlineUser = onlineUsers.getString(i);
                        onlineMonsoonUsers.put(onlineUser.split(":")[0], onlineUser.split(":")[1]); // is sent back to us as minecraftusername:monsoonusername
                    }
                    break;
            }
        }
    }

    public void sendPacket(MPacket packet) {
        try {
            sendMessageToServer(packet.getPacketData().toString(), this.socket);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void sendPacket(MPacket packet, Socket socket) throws Exception {
        sendMessageToServer(packet.getPacketData().toString(), socket);
    }

    private void sendMessageToServer(String message, Socket socket) throws Exception {
        if(socket == null) return;
        if(!socket.isConnected()) return;
        Wrapper.getLogger().debug("sent packet: " + message);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message + "\n");
        writer.flush();
    }

}
