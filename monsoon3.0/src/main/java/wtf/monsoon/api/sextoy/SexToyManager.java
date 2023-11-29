package wtf.monsoon.api.sextoy;


import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SexToyManager {

    @Getter
    private List<MonsoonLovenseToy> toys;

    @Getter @Setter
    private int vibrationAmount, vibrationIncrement;

    public void init() {
        Wrapper.getLogger().info("Initializing the Monsoon Sex Toy Integration");
        try {
            toys = new ArrayList<>();
            findToys();
            vibrationAmount = 0;
            vibrationIncrement = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findToys() throws Exception {
        String url = "https://api.lovense.com/api/lan/getToys";
        Map<String, String> requestParameter = new HashMap<>();
        HttpClient httpClient = HttpClients.custom().build();
        HttpPost httpPost = new HttpPost(url);

        HttpResponse response = httpClient.execute(httpPost);

        String responseBody = EntityUtils.toString(response.getEntity());

        JSONObject jsonObject = new JSONObject(responseBody);
        System.out.println(responseBody);
    }

    public MonsoonLovenseToy getMainToy() {
        if (toys.isEmpty()) {
            return new MonsoonLovenseToy();
        } else return toys.get(0);
    }

    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        if (Minecraft.getMinecraft().thePlayer.hurtTime <= 0) {
            vibrationAmount = 0;
        }

        if (vibrationAmount < vibrationIncrement) {
            vibrationIncrement--;
        } else if (vibrationAmount > vibrationIncrement) {
            vibrationIncrement++;
        }

        if (vibrationIncrement < 0) {
            vibrationIncrement = 0;
        }

        if (vibrationAmount < 0) {
            vibrationAmount = 0;
        }

        getMainToy().vibrate(vibrationIncrement);
    };

    public void vibrate(int amount) {
        setVibrationAmount(amount);
    }

}
