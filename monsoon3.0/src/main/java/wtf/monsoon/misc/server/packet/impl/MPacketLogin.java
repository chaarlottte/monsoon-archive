package wtf.monsoon.misc.server.packet.impl;

import net.minecraft.client.Minecraft;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.Identification;
import wtf.monsoon.misc.server.packet.MPacket;

public class MPacketLogin implements MPacket {

    private String token;

    public MPacketLogin() {}

    public MPacketLogin(String token) {
        this.token = token;
    }

    @Override
    public JSONObject getPacketData() {
        JSONObject jsonObject = new JSONObject();

        try {
            JSONObject loginData = new JSONObject();

            loginData.put("username", Wrapper.getMonsoonAccount().getUsername());
            loginData.put("hwid", Identification.getHWID());
            loginData.put("uid", Wrapper.getMonsoonAccount().getUid());
            loginData.put("ip", Identification.getIp());

            if (token != null) {
                loginData.put("token", token);
            }

            if (token != null) {
                loginData.put("minecraftUsername", Minecraft.getSessionInfo().get("X-Minecraft-Username"));
            }

            if (token != null) {
                jsonObject.put("id", "c1");
            } else {
                jsonObject.put("id", "c2");
            }

            jsonObject.put("loginData", loginData);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        return jsonObject;
    }

}
