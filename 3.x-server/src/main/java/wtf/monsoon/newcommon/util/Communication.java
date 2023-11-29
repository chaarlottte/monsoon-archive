package wtf.monsoon.newcommon.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import wtf.monsoon.newcommon.encryption.impl.AESEncryption;
import wtf.monsoon.newcommon.encryption.impl.RSAEncryption;
import wtf.monsoon.newcommon.packet.EnumPacketType;
import wtf.monsoon.newcommon.packet.Packet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import sun.security.rsa.RSAPublicKeyImpl;
import wtf.monsoon.newcommon.packet.impl.client.community.*;
import wtf.monsoon.newcommon.packet.impl.client.login.*;
import wtf.monsoon.newcommon.packet.impl.server.login.*;
import wtf.monsoon.newcommon.packet.impl.server.community.*;
import wtf.monsoon.newcommon.packet.impl.server.misc.*;

import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public final class Communication {

    private final Socket socket;

    private RSAEncryption rsaEncryption;
    private AESEncryption aesEncryption;

    private PrintWriter output;
    private BufferedReader input;

    private LinkedBlockingQueue<Packet<?>> packetQueue;
    private Thread packetSendThread;

    private boolean isServer;

    public Communication(final Socket socket, final boolean server) {
        this.socket = socket;
        this.isServer = server;

        try {
            this.output = new PrintWriter(socket.getOutputStream(), true);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (this.isServer) {
                this.rsaEncryption = new RSAEncryption();

                this.output.println(new Gson().toJson(this.rsaEncryption.getPublicKey()));

                this.aesEncryption = new AESEncryption(new String(this.rsaEncryption.decrypt(Base64.getDecoder().decode(this.input.readLine()))));

                this.packetQueue = new LinkedBlockingQueue<>();
                this.packetSendThread = new Thread(() -> {
                    while(true) {
                        if(!this.packetQueue.isEmpty()) {
                            try {
                                this.writeToClient(this.packetQueue.poll());
                            } catch (IOException exception) {
                                exception.printStackTrace();
                                try {
                                    Thread.sleep(250);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                continue;
                            }
                        }
                        try {
                            Thread.sleep(50);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                this.packetSendThread.start();
            } else {
                this.rsaEncryption = new RSAEncryption(null, new Gson().fromJson(this.input.readLine(), RSAPublicKeyImpl.class));

                final String key = UUID.randomUUID().toString().replace("-", "");

                this.aesEncryption = new AESEncryption(key);
                this.output.println(Base64.getEncoder().encodeToString(this.rsaEncryption.encrypt(key.getBytes(StandardCharsets.UTF_8))));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void write(final Packet<?> packet) {
        if(this.isServer) {
            this.packetQueue.add(packet);
        } else {
            try {
                this.writeToClient(packet);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /*private void writeToClient(final Packet<?> packet) throws IOException {
        try {
            final String json = new Gson().toJson(packet);
            this.output.println(Arrays.toString(this.aesEncryption.encrypt(json.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }*/

    private void writeToClient(final Packet<?> packet) throws IOException {
        try {
            final JsonObject json = new JsonObject();
            json.addProperty("type", packet.getType().toString());
            json.addProperty("data", new Gson().toJson(packet));
            final String jsonString = json.toString();
            // System.out.println(jsonString);
            this.output.println(Arrays.toString(this.aesEncryption.encrypt(jsonString.getBytes(StandardCharsets.UTF_8))));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /*public Packet<?> read() {
        try {
            String input = this.input.readLine();
            final byte[] data = aesEncryption.decrypt(this.stringToByteArray(input));

            final JsonObject json = new JsonParser().parse(new String(data, StandardCharsets.UTF_8)).getAsJsonObject();
            final String type = json.get("type").getAsString();
            final String packetData = json.get("data").getAsString();

            final Packet<?> packet = new Gson().fromJson(packetData, Packet.class);
            System.out.println(packet);
            return packet;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    public Packet<?> read() {
        try {
            String input = this.input.readLine();
            final byte[] data = aesEncryption.decrypt(this.stringToByteArray(input));

            final JsonObject json = JsonParser.parseString(new String(data, StandardCharsets.UTF_8)).getAsJsonObject();
            final String type = json.get("type").getAsString();
            final String packetData = json.get("data").getAsString();

            // Class<? extends Packet<?>> packetClass = getClassForType(type);
            Class<? extends Packet<?>> packetClass = EnumPacketType.valueOf(type).getPacketClass();
            return new Gson().fromJson(packetData, packetClass);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private Class<? extends Packet<?>> getClassForType(String type) {
        switch (type) {

            // Client packets
            case "client_community_message_send":
                return ClientCommunityMessageSend.class;
            case "client_population_request":
                return ClientPopulationRequest.class;
            case "client_login":
                return ClientLoginPacket.class;

            // Server packets
            case "server_community_message_send":
                return ServerCommunityMessageSend.class;
            case "server_population_response":
                return ServerPopulationResponse.class;
            case "server_population_update":
                return ServerPopulationUpdate.class;
            case "server_invalid_access_token_response":
                return ServerInvalidAccessTokenResponse.class;
            case "server_login_response":
                return ServerLoginResponse.class;
            case "server_error_message":
                return ServerErrorResponse.class;

            // add additional cases for each packet type
            default:
                throw new IllegalArgumentException("Invalid packet type: " + type);
        }
    }

    /*public Packet<?> read() {
        try {
            String input = this.input.readLine();
            final byte[] data = aesEncryption.decrypt(this.stringToByteArray(input));

            final Packet<?> packet = new Gson().fromJson(new String(data, StandardCharsets.UTF_8), Packet.class);
            System.out.println(packet);
            return packet;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    private byte[] stringToByteArray(String byteString) {
        byteString = byteString.replaceAll("\\[|\\]|\\s", ""); // Remove brackets and spaces
        String[] byteValues = byteString.split(","); // Split into array of substrings
        byte[] byteArray = new byte[byteValues.length];

        for (int i = 0; i < byteValues.length; i++) {
            byteArray[i] = Byte.parseByte(byteValues[i]); // Convert to byte value
        }

        return byteArray;
    }
}
