package wtf.monsoon;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.*;
import wtf.monsoon.api.config.ConfigSystem;
import wtf.monsoon.api.manager.*;
import wtf.monsoon.api.manager.alt.AltManager;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;
import wtf.monsoon.impl.event.*;
import wtf.monsoon.impl.module.visual.Accent;
import wtf.monsoon.impl.module.hud.HUD;
import wtf.monsoon.impl.ui.character.CharacterManager;
import wtf.monsoon.impl.ui.panel.PanelGUI;
import wtf.monsoon.impl.ui.recode.panel.PanelScreen;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.misc.protection.BuildType;
import wtf.monsoon.misc.script.ScriptLoader;
import wtf.monsoon.misc.server.MonsoonServerConnection;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Monsoon {

    @Getter
    private final String version = "3.0-A6";

    @Getter
    private final BuildType buildType = BuildType.DEVELOPER;

    @Getter @Setter
    private ModuleManager moduleManager;

    @Getter @Setter
    private CommandManager commandManager;

    @Getter @Setter
    private AltManager altManager = new AltManager();

    @Getter @Setter
    private FriendManager friendManager = new FriendManager();

    @Getter @Setter
    private ConfigSystem configSystem;

    @Getter @Setter
    private WindowGUI windowGUI;

    @Getter @Setter
    private PanelGUI panelGUI;
    @Getter @Setter
    private PanelScreen recodePanelGUI;

    @Getter @Setter
    private MonsoonServerConnection server;


    @Getter @Setter
    private MonsoonPlayerObject player;

    @Getter @Setter
    private CharacterManager characterManager;

    @Getter @Setter
    private ProcessorManager processorManager;

    @Getter @Setter
    private ScriptLoader scriptLoader;

    @Getter @Setter
    private Proxy proxy = Proxy.NO_PROXY;

    public static boolean DEBUG_MODE = false;

    @EventLink
    public final Listener<EventKey> eventKeyListener = event -> {
        for (Module module : getModuleManager().getModules()) {
            if (module.getKey() != null) {
                if (module.getKey().getValue().getButtonCode() == event.getKey()) {
                    module.toggle();
                }
            }
        }
    };

    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = event -> {
        if (!getModuleManager().getModule(HUD.class).isEnabled()) {
            getModuleManager().getModule(HUD.class).setEnabled(true);
        }

        if (!getModuleManager().getModule(Accent.class).isEnabled()) {
            getModuleManager().getModule(Accent.class).setEnabled(true);
        }
    };

    public void exit() {
        Wrapper.getLogger().info("Stopping Monsoon");
        getConfigSystem().save("current");
    }

    // if you delete this you will be removed from the dev team
    public void downloadFurryPorn() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClients.createDefault();
                HttpGet request = new HttpGet("https://e621.net/posts.json?limit=500&tags=sylveon+solo+rating%3Aexplicit");
                request.setHeader("User-Agent", "Monsoon/1.0 (by monsoon_development)");
                HttpResponse response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                String jsonText = sb.toString();
                JSONObject json = new JSONObject(jsonText);

                JSONArray jsonArray = json.getJSONArray("posts");
                String furryPornUrl = jsonArray.getJSONObject((int) MathUtils.randomNumber(319, 0)).getJSONObject("sample").getString("url");

                InputStream in = new URL(furryPornUrl).openStream();
                Files.copy(in, new File(System.getProperty("user.home") + "/Desktop", "monsooon-client-" + furryPornUrl.substring(furryPornUrl.length() - 35) + furryPornUrl.substring(furryPornUrl.length() - 5)).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }
}
