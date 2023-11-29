package wtf.monsoon.api.manager.alt;

import com.thealtening.api.retriever.BasicDataRetriever;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Surge
 * @since 22/08/2022
 */
public class AltManager {


    @Getter
    private final List<Alt> alts = new ArrayList<>();

    @Getter
    private final TheAlteningAuthentication alteningAuthentication;

    @Getter
    private final BasicDataRetriever alteningAltFetcher;

    @Getter
    private String apiKey;

    public AltManager() {
        apiKey = "api-0000-0000-0000";
        alteningAuthentication = TheAlteningAuthentication.mojang();
        alteningAltFetcher = new BasicDataRetriever(apiKey);
        alteningAuthentication.updateService(AlteningServiceType.MOJANG);
    }

    public void addAlt(Alt alt) {
        alts.add(alt);
    }

    public void removeAlt(Alt alt) {
        alts.remove(alt);
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();

        alts.forEach(alt -> {
            JSONObject altJson = new JSONObject();

            try {
                altJson.put("password", alt.getPassword());
                altJson.put("auth", alt.getAuthenticator().name());
                altJson.put("username", alt.getUsername());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                json.put(alt.getEmail(), altJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        try {
            json.put("altening-api-key", getApiKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        alteningAltFetcher.updateKey(apiKey);
        Wrapper.getMonsoon().getConfigSystem().saveAlts(this);
    }
}
