package wtf.monsoon.misc.server.packet.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.Identification;
import wtf.monsoon.misc.server.packet.MPacket;

public class MPacketUpdateUsername implements MPacket {

    @Getter @Setter
    private String newUsername;

    public MPacketUpdateUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    @Override
    public JSONObject getPacketData() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", "c3");
            jsonObject.put("newUsername", newUsername);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        return jsonObject;
    }

}
