package wtf.monsoon.impl.module.hud;

//import com.wrapper.spotify.SpotifyApi;
//import com.wrapper.spotify.exceptions.SpotifyWebApiException;
//import com.wrapper.spotify.model_objects.specification.User;
//import com.wrapper.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import lombok.Setter;
//import org.apache.hc.core5.http.ParseException;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;

import java.io.IOException;

public class Spotify extends HUDModule {
    @Setter
    String token = "";
    boolean initialized;
//    SpotifyApi api;
    public Spotify() {
        super("Spotify", "Displays your current spotify song");
    }

    void initSpotify() {/*
        api = new SpotifyApi.Builder().setAccessToken(token).build();
        GetCurrentUsersProfileRequest b = api.getCurrentUsersProfile().build();
        try {
            User user = b.execute();
            System.out.println(user.getProduct());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public void onEnable() {
        if(token.isEmpty() || token.length() < 100) {
            PlayerUtil.sendClientMessage("No token found!");
            PlayerUtil.sendClientMessage("Do .spotify [Spotify OAuth Token]");
            toggle();
        } else
            initSpotify();
        super.onEnable();
    }

    @Override
    public void render() {

    }

    @Override
    public void blur() {

    }
}
