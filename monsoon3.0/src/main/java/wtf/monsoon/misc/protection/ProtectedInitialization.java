package wtf.monsoon.misc.protection;

import dev.quickprotect.NativeObf;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.config.*;
import wtf.monsoon.api.manager.*;
import wtf.monsoon.api.setting.Exclude;
import wtf.monsoon.api.setting.*;
import wtf.monsoon.api.sextoy.SexToyManager;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;
import wtf.monsoon.impl.command.*;
import wtf.monsoon.impl.module.combat.*;
import wtf.monsoon.impl.module.exploit.*;
import wtf.monsoon.impl.module.ghost.*;
import wtf.monsoon.impl.module.hud.*;
import wtf.monsoon.impl.module.movement.*;
import wtf.monsoon.impl.module.player.*;
import wtf.monsoon.impl.module.visual.*;
import wtf.monsoon.impl.processor.player.*;
import wtf.monsoon.impl.processor.*;
import wtf.monsoon.impl.processor.viamcp.*;
import wtf.monsoon.impl.ui.character.CharacterManager;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.panel.PanelGUI;
import wtf.monsoon.impl.ui.recode.panel.PanelScreen;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.misc.script.ScriptLoader;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ProtectedInitialization {

    private boolean doneLoading = false;

    @NativeObf
    public void start() {
        if (!Wrapper.loggedIn) {
            return;
        }

        this.doneLoading = false;

        Wrapper.getMonsoon().setModuleManager(new ModuleManager());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getModuleManager());

        // Most important three lines of code in the client.
        Wrapper.setSexToyManager(new SexToyManager());
        Wrapper.getSexToyManager().init();
        Wrapper.getEventBus().subscribe(Wrapper.getSexToyManager());

        /*
         * Start Module initialization (protected)
         */

        // Load scripts.
        Wrapper.getMonsoon().setScriptLoader(new ScriptLoader());
        Wrapper.getMonsoon().getScriptLoader().loadScripts();

        Wrapper.getMonsoon().setPlayer(new MonsoonPlayerObject());
        Wrapper.getEventBus().subscribe(Wrapper.getMonsoon().getPlayer());

        // Initialize all the modules.

        // Combat
        Wrapper.getMonsoon().getModuleManager().putModule(Aura.class, new Aura());
        Wrapper.getMonsoon().getModuleManager().putModule(TargetStrafe.class, new TargetStrafe());
        Wrapper.getMonsoon().getModuleManager().putModule(Velocity.class, new Velocity());
        Wrapper.getMonsoon().getModuleManager().putModule(Criticals.class, new Criticals());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoPot.class, new AutoPot());

        // Movement
        Wrapper.getMonsoon().getModuleManager().putModule(Flight.class, new Flight());
        Wrapper.getMonsoon().getModuleManager().putModule(HighJump.class, new HighJump());
        Wrapper.getMonsoon().getModuleManager().putModule(InventoryMove.class, new InventoryMove());
        Wrapper.getMonsoon().getModuleManager().putModule(LongJump.class, new LongJump());
        Wrapper.getMonsoon().getModuleManager().putModule(NoSlow.class, new NoSlow());
        Wrapper.getMonsoon().getModuleManager().putModule(Speed.class, new Speed());
        Wrapper.getMonsoon().getModuleManager().putModule(Sprint.class, new Sprint());
        Wrapper.getMonsoon().getModuleManager().putModule(Step.class, new Step());
        Wrapper.getMonsoon().getModuleManager().putModule(Spider.class, new Spider());

        // Player
        Wrapper.getMonsoon().getModuleManager().putModule(AutoArmor.class, new AutoArmor());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoHypixel.class, new AutoHypixel());
        Wrapper.getMonsoon().getModuleManager().putModule(ChestStealer.class, new ChestStealer());
        Wrapper.getMonsoon().getModuleManager().putModule(InventoryManager.class, new InventoryManager());
        Wrapper.getMonsoon().getModuleManager().putModule(LovenseIntegration.class, new LovenseIntegration());
        Wrapper.getMonsoon().getModuleManager().putModule(NoFall.class, new NoFall());
        Wrapper.getMonsoon().getModuleManager().putModule(Phase.class, new Phase());
        Wrapper.getMonsoon().getModuleManager().putModule(Scaffold.class, new Scaffold());
        Wrapper.getMonsoon().getModuleManager().putModule(ServerSideStrafe.class, new ServerSideStrafe());
        Wrapper.getMonsoon().getModuleManager().putModule(AntiVoid.class, new AntiVoid());
        Wrapper.getMonsoon().getModuleManager().putModule(NoRotate.class, new NoRotate());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoTool.class, new AutoTool());
        Wrapper.getMonsoon().getModuleManager().putModule(AntiAim.class, new AntiAim());
        Wrapper.getMonsoon().getModuleManager().putModule(ChatSpammer.class, new ChatSpammer());
        Wrapper.getMonsoon().getModuleManager().putModule(KillInsults.class, new KillInsults());

        // Visual
        Wrapper.getMonsoon().getModuleManager().putModule(Accent.class, new Accent());
        Wrapper.getMonsoon().getModuleManager().putModule(Ambience.class, new Ambience());
        Wrapper.getMonsoon().getModuleManager().putModule(BlockAnimations.class, new BlockAnimations());
        Wrapper.getMonsoon().getModuleManager().putModule(Blur.class, new Blur());
        Wrapper.getMonsoon().getModuleManager().putModule(ChestESP.class, new ChestESP());
        Wrapper.getMonsoon().getModuleManager().putModule(ChinaHat.class, new ChinaHat());
        Wrapper.getMonsoon().getModuleManager().putModule(ClickGUI.class, new ClickGUI());
        Wrapper.getMonsoon().getModuleManager().putModule(EnchantColour.class, new EnchantColour());
        Wrapper.getMonsoon().getModuleManager().putModule(ESP.class, new ESP());
        Wrapper.getMonsoon().getModuleManager().putModule(Nametags.class, new Nametags());
        Wrapper.getMonsoon().getModuleManager().putModule(NoRender.class, new NoRender());
        Wrapper.getMonsoon().getModuleManager().putModule(ShaderESP.class, new ShaderESP());
        Wrapper.getMonsoon().getModuleManager().putModule(SuperheroFX.class, new SuperheroFX());
        Wrapper.getMonsoon().getModuleManager().putModule(Tracers.class, new Tracers());
        Wrapper.getMonsoon().getModuleManager().putModule(Trajectories.class, new Trajectories());
        Wrapper.getMonsoon().getModuleManager().putModule(ViewModel.class, new ViewModel());
        Wrapper.getMonsoon().getModuleManager().putModule(CharacterRenderer.class, new CharacterRenderer());
        Wrapper.getMonsoon().getModuleManager().putModule(HitMarkers.class, new HitMarkers());

        // Exploit
        Wrapper.getMonsoon().getModuleManager().putModule(Disabler.class, new Disabler());
        Wrapper.getMonsoon().getModuleManager().putModule(NoC03.class, new NoC03());
        Wrapper.getMonsoon().getModuleManager().putModule(PingSpoof.class, new PingSpoof());
        Wrapper.getMonsoon().getModuleManager().putModule(TimerModule.class, new TimerModule());
        Wrapper.getMonsoon().getModuleManager().putModule(ResetVL.class, new ResetVL());
        Wrapper.getMonsoon().getModuleManager().putModule(Blink.class, new Blink());

        // Ghost
        Wrapper.getMonsoon().getModuleManager().putModule(AutoBridger.class, new AutoBridger());
        Wrapper.getMonsoon().getModuleManager().putModule(AutoClicker.class, new AutoClicker());
        Wrapper.getMonsoon().getModuleManager().putModule(ClickDelayRemover.class, new ClickDelayRemover());
        Wrapper.getMonsoon().getModuleManager().putModule(HitBox.class, new HitBox());
        Wrapper.getMonsoon().getModuleManager().putModule(Reach.class, new Reach());

        // HUD
        Wrapper.getMonsoon().getModuleManager().putModule(HUDArrayList.class, new HUDArrayList());
        Wrapper.getMonsoon().getModuleManager().putModule(Arrows.class, new Arrows());
        Wrapper.getMonsoon().getModuleManager().putModule(Hotbar.class, new Hotbar());
        Wrapper.getMonsoon().getModuleManager().putModule(HUD.class, new HUD());
        Wrapper.getMonsoon().getModuleManager().putModule(InventoryDisplay.class, new InventoryDisplay());
        Wrapper.getMonsoon().getModuleManager().putModule(NotificationsModule.class, new NotificationsModule());
        Wrapper.getMonsoon().getModuleManager().putModule(SessionInfo.class, new SessionInfo());
        Wrapper.getMonsoon().getModuleManager().putModule(Speedometer.class, new Speedometer());
        Wrapper.getMonsoon().getModuleManager().putModule(TargetHUD.class, new TargetHUD());
        Wrapper.getMonsoon().getModuleManager().putModule(CrosshairCustomizer.class, new CrosshairCustomizer());
//        Wrapper.getMonsoon().getModuleManager().putModule(Spotify.class, new Spotify());

        // instantiation funkiness
        Wrapper.getMonsoon().getModuleManager().getModules().forEach(module -> {
            Arrays.stream(module.getClass().getDeclaredFields())
                    .filter(field -> Setting.class.isAssignableFrom(field.getType()))

                    .forEach(field -> {
                        field.setAccessible(true);

                        try {
                            Setting<?> setting = ((Setting<?>) field.get(module));

                            if (!field.isAnnotationPresent(Exclude.class) && setting.getParent() == null) {
                                module.getSettings().add(setting);
                            }

                            if(setting.getValue() instanceof Enum<?>) {
                                for(Enum<?> en : ((Enum) setting.getValue()).getClass().getEnumConstants()) {
                                    for(Field f : en.getClass().getDeclaredFields()) {
                                        if(f.getType().isAssignableFrom(ModeProcessor.class)) {
                                            f.setAccessible(true);
                                            ModeProcessor value = (ModeProcessor) f.get(en);
                                            Arrays.asList(value.getModeSettings()).forEach(mode -> {
                                                module.getSettings().add(mode);
                                                mode.visibleWhen(() -> setting.getValue().equals(en));
                                            });
                                        }
                                    }
                                }
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });

            module.getSettings().add(module.getKey());
        });

        // Initialize all the commands.
        Wrapper.getMonsoon().setCommandManager(new CommandManager());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SayCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new ConfigCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new ToggleCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new FriendCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SettingCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new DefaultsCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new DoxCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new DuplicateCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new BindCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new ReloadCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SpamCommand());
        Wrapper.getMonsoon().getCommandManager().getCommands().add(new BlacklistMap());
//        Wrapper.getMonsoon().getCommandManager().getCommands().add(new SpotifyCommand());

        // Initialize the Friend Manager.
        Wrapper.getMonsoon().setFriendManager(new FriendManager());

        // Initialize the Window GUI.
        Wrapper.getMonsoon().setWindowGUI(new WindowGUI());

        Wrapper. getMonsoon().setConfigSystem(new ConfigSystem());
        Wrapper.getMonsoon().getConfigSystem().load("current", true);
        Wrapper.getMonsoon().getConfigSystem().loadAlts(Wrapper.getMonsoon().getAltManager());

        // Moved it here so the config window can initialize properly
        Wrapper.getMonsoon().setPanelGUI(new PanelGUI());
        Wrapper.getMonsoon().setRecodePanelGUI(new PanelScreen());

        // Initialize the Character manager.
        Wrapper.getMonsoon().setCharacterManager(new CharacterManager());

        Wrapper.getMonsoon().setProcessorManager(new ProcessorManager());
        Wrapper.getMonsoon().getProcessorManager().addProcessor(LagbackProcessor.class, new LagbackProcessor());
        // Wrapper.getMonsoon().getProcessorManager().addProcessor(BlinkProcessor.class, new BlinkProcessor());
        // Wrapper.getMonsoon().getProcessorManager().addProcessor(HypixelAPIProcessor.class, new HypixelAPIProcessor());
        // Wrapper.getMonsoon().getProcessorManager().addProcessor(BlockPlacementProcessor.class, new BlockPlacementProcessor());
        // Wrapper.getMonsoon().getProcessorManager().addProcessor(FlyingPacketProcessor.class, new FlyingPacketProcessor());

        doneLoading = true;
    }

    @NativeObf
    public void checkIfDoneLoading() {
        if (this.doneLoading) {
            Minecraft.getMinecraft().displayGuiScreen(new MainMenu());
        }
    }

}
