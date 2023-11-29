package wtf.monsoon;

import dev.quickprotect.NativeObf;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;
import wtf.monsoon.api.event.Event;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.opengui.IRendererImpl;
import wtf.monsoon.api.sextoy.SexToyManager;
import wtf.monsoon.api.util.Logger;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.font.impl.FontRenderer;
import wtf.monsoon.api.util.render.NVGR;
import wtf.monsoon.impl.ui.Pallet;
import wtf.monsoon.impl.ui.notification.NotificationManager;
import wtf.monsoon.misc.protection.MonsoonAccount;
import wtf.monsoon.misc.proxy.ProxyInitThread;
import wtf.monsoon.misc.script.ScriptLoader;
import wtf.opengui.GuiGL;

import java.awt.*;
import java.net.Proxy;

public class Wrapper {

    // This is just for testing, some1 please make good backend for it!
    public static boolean loggedIn = false;

    public static boolean enableBedrock = false;

    @Getter
    private final static EventBus<Event> eventBus = new EventBus<>();

    @Getter
    private static Minecraft minecraft;

    @Getter @Setter
    public static MonsoonAccount monsoonAccount;

    @Getter
    private static Monsoon monsoon;

    @Getter
    private final static Logger logger = new Logger();

    @Getter
    private static final FontUtil fontUtil = new FontUtil();

    @Getter @Setter
    private static FontRenderer font;

    @Getter
    private static final NotificationManager notifManager = new NotificationManager();

    @Getter
    private static Pallet pallet;

    @Getter @Setter
    private static SexToyManager sexToyManager;

    @Getter @Setter
    private static long initTime;

    @Getter @Setter
    private static long sessionTime;

    @Getter @Setter
    private static String serverIP;

    @Getter @Setter
    private static boolean debugModeEnabled;

    @Getter
    private static NVGR NVG;

    @Getter
    static GuiGL glgui;

    @NativeObf
    public static void init(boolean enableDebugMode) {
        setDebugModeEnabled(enableDebugMode);

        NVG = new NVGR();
        NVG.init();

        glgui = new GuiGL();
        glgui.setRenderer(new IRendererImpl());

        // For testing purposes. Will be replaced once Advantage is ready.
        monsoonAccount = new MonsoonAccount();
        monsoonAccount.setHwid("sex");
        monsoonAccount.setUsername("test_user");
        monsoonAccount.setUid("1337");

        // Initialize/subscribe to the Monsoon class.
        monsoon = new Monsoon();
        Wrapper.getEventBus().subscribe(monsoon);

        // Display.setTitle("Monsoon " + getMonsoon().getVersion());

        long startedAt = System.currentTimeMillis();
        Wrapper.getLogger().info("Starting Monsoon " + getMonsoon().getVersion());

        // THIS IS THE OLD PURPLE PALLET
        pallet = new Pallet(Color.decode("#8281E9"), Color.decode("#40407B"), Color.decode("#222242"), Color.decode("#0e0e1c"), Color.decode("#52529D"));
        // pallet = new Pallet(Color.decode("#2873ff"), Color.decode("#18305E"), Color.decode("#232327"), Color.decode("#141418"), Color.decode("#45454d"));

        // Assign the Minecraft variable for later use.
        minecraft = Minecraft.getMinecraft();

        // Bootstrapping the FontUtil. This has to be done as early as possible so the modules and anything else won't crash with a NullPointerException.
        fontUtil.bootstrap();

        // Start up ViaMCP. It doesn't really matter when this happens (from what I can tell).
        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();

        // Compatibility
        getMinecraft().gameSettings.guiScale = 2;
        getMinecraft().gameSettings.ofFastRender = false;

        // Performance settings
        getMinecraft().gameSettings.ofSmartAnimations = true;
        getMinecraft().gameSettings.ofSmoothFps = false;

        // Cheats
        getMinecraft().gameSettings.ofFastMath = true;
        /*getMonsoon().setServer(new MonsoonServerConnection("127.0.0.1", 1337));
        new Thread(() -> {
            try {
                // getMonsoon().getServer().login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/

        // if(!isDebugModeEnabled()) getMonsoon().downloadFurryPorn();

        Wrapper.getLogger().info("Finished starting Monsoon in " + (System.currentTimeMillis() - startedAt) + "ms.");
        initTime = System.currentTimeMillis();

        // getMonsoon().setProxy(new Proxy());

        //residential.proxies.gg:50000:7HvTcZsL:jljOECUgxt_country-us_city-boston_session-b61585b2_lifetime-24h
        // ProxyInitThread proxyInitThread = new ProxyInitThread("residential.proxies.gg", 50000, "7HvTcZsL", "jljOECUgxt_country-us_city-boston_session-b61585b2_lifetime-24h");
        // proxyInitThread.start();
    }

    public static void shutdown() {
        getMonsoon().getConfigSystem().save("current");
        getMonsoon().getConfigSystem().saveAlts(getMonsoon().getAltManager());
        getMonsoon().exit();
    }

    public static <T extends Module> T getModule(Class<T> clas) {
        if(getMonsoon() == null || getMonsoon().getModuleManager() == null) return null;
        return (T) getMonsoon().getModuleManager().getModules().stream().filter(module -> module.getClass() == clas).findFirst().orElse(null);
    }

}
