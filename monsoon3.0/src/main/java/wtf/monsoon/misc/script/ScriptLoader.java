package wtf.monsoon.misc.script;

import kotlin.Pair;
import me.surge.api.Executor;
import me.surge.api.LoadHelper;
import me.surge.lexer.error.Error;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.misc.script.wrapper.ScriptPlayer;
import wtf.monsoon.misc.script.wrapper.ScriptUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScriptLoader {

    public File dir, scriptDir;

    public Map<String, String> toReplace = new HashMap<>();

    public void loadScripts() {
        dir = new File(Minecraft.getMinecraft().mcDataDir, "Monsoon");
        scriptDir = new File(dir, "scripts");

        // first run kek
        if (!dir.exists()) {
            return;
        }

        if (!scriptDir.exists()) {
            scriptDir.mkdir();
        }

        Wrapper.getLogger().info("Loading scripts from dir " + scriptDir.toString());

        for (File file : Objects.requireNonNull(scriptDir.listFiles())) {
            if (file.getName().endsWith(".mfl")) {
                try {
                    Wrapper.getLogger().info("Loading script: " + file.getName());
                    String script = readFile(file.getAbsolutePath());

                    for (String string : toReplace.keySet()) {
                        if (script.contains(string)) {
                            script = script.replace(string, toReplace.get(string));
                        }
                    }

                    Executor executor = new Executor()
                            .loadClass("player", ScriptPlayer.class)
                            .loadClass("util", ScriptUtil.class)
                            .loadClass("manager", ScriptManager.class)
                            .loadClass("gl11", GL11.class);

                    loadJvmClasses(executor);
                    Pair<Object, Error> result = executor.evaluate(file.getName(), script);

                    if (result.getSecond() != null) {
                        Wrapper.getLogger().error(result.getSecond().toString());
                    }
                } catch (Exception ex) {
                    Wrapper.getLogger().error("Could not load script: " + file.getName());
                    ex.printStackTrace();
                }
            }
        }
    }

    public void unloadScripts() {
        for (Module module : Wrapper.getMonsoon().getModuleManager().getModulesByCategory(Category.SCRIPT)) {
            Wrapper.getMonsoon().getModuleManager().getModules().remove(module);
        }
    }

    public void reloadScripts() {
        this.unloadScripts();
        this.loadScripts();
    }

    private void addReplacedText() {
        toReplace.put("newModule ({", "luajava.createProxy(module, {");
        toReplace.put("gui ({", "luajava.createProxy(gui, {");
    }

    private void loadJvmClasses(Executor executor) {
        // null checks are just so i can minimise it lmao

        // packets
        if (executor != null) {
            executor.loadClassAsContainer("C0APacketAnimation", new C0APacketAnimation())
                    .loadClassAsContainer("C0BPacketEntityAction", new C0BPacketEntityAction())
                    .loadClassAsContainer("C0CPacketInput", new C0CPacketInput())
                    .loadClassAsContainer("C0DPacketCloseWindow", new C0DPacketCloseWindow())
                    .loadClassAsContainer("C0EPacketClickWindow", new C0EPacketClickWindow())
                    .loadClassAsContainer("C0FPacketConfirmTransaction", new C0FPacketConfirmTransaction())
                    .loadClassAsContainer("C01PacketChatMessage", new C01PacketChatMessage())
                    .loadClassAsContainer("C02PacketUseEntity", new C02PacketUseEntity())
                    .loadClassAsContainer("C03PacketPlayer", new C03PacketPlayer())
                    .loadClassAsContainer("C04PacketPlayerPosition", new C03PacketPlayer.C04PacketPlayerPosition())
                    .loadClassAsContainer("C05PacketPlayerLook", new C03PacketPlayer.C05PacketPlayerLook())
                    .loadClassAsContainer("C06PacketPlayerPosLook", new C03PacketPlayer.C06PacketPlayerPosLook())
                    .loadClassAsContainer("C07PacketPlayerDigging", new C07PacketPlayerDigging())
                    .loadClassAsContainer("C08PacketPlayerBlockPlacement", new C08PacketPlayerBlockPlacement())
                    .loadClassAsContainer("C09PacketHeldItemChange", new C09PacketHeldItemChange())
                    .loadClassAsContainer("C10PacketCreativeInventoryAction", new C10PacketCreativeInventoryAction())
                    .loadClassAsContainer("C11PacketEnchantItem", new C11PacketEnchantItem())
                    .loadClassAsContainer("C12PacketUpdateSign", new C12PacketUpdateSign())
                    .loadClassAsContainer("C13PacketPlayerAbilities", new C13PacketPlayerAbilities())
                    .loadClassAsContainer("C14PacketTabComplete", new C14PacketTabComplete())
                    .loadClassAsContainer("C15PacketClientSettings", new C15PacketClientSettings())
                    .loadClassAsContainer("C16PacketClientStatus", new C16PacketClientStatus())
                    .loadClassAsContainer("C17PacketCustomPayload", new C17PacketCustomPayload())
                    .loadClassAsContainer("C18PacketSpectate", new C18PacketSpectate())
                    .loadClassAsContainer("C19PacketResourcePackStatus", new C19PacketResourcePackStatus());

            executor.loadClassAsContainer("S0APacketUseBed", new S0APacketUseBed())
                    .loadClassAsContainer("S0BPacketAnimation", new S0BPacketAnimation())
                    .loadClassAsContainer("S0CPacketSpawnPlayer", new S0CPacketSpawnPlayer())
                    .loadClassAsContainer("S0DPacketCollectItem", new S0DPacketCollectItem())
                    .loadClassAsContainer("S0EPacketSpawnObject", new S0EPacketSpawnObject())
                    .loadClassAsContainer("S0FPacketSpawnMob", new S0FPacketSpawnMob())
                    .loadClassAsContainer("S00PacketKeepAlive", new S00PacketKeepAlive())
                    .loadClassAsContainer("S1BPacketEntityAttach", new S1BPacketEntityAttach())
                    .loadClassAsContainer("S1CPacketEntityMetadata", new S1CPacketEntityMetadata())
                    .loadClassAsContainer("S1DPacketEntityEffect", new S1DPacketEntityEffect())
                    .loadClassAsContainer("S1EPacketRemoveEntityEffect", new S1EPacketRemoveEntityEffect())
                    .loadClassAsContainer("S1FPacketSetExperience", new S1FPacketSetExperience())
                    .loadClassAsContainer("S01PacketJoinGame", new S01PacketJoinGame())
                    .loadClassAsContainer("S2APacketParticles", new S2APacketParticles())
                    .loadClassAsContainer("S2BPacketChangeGameState", new S2BPacketChangeGameState())
                    .loadClassAsContainer("S2CPacketSpawnGlobalEntity", new S2CPacketSpawnGlobalEntity())
                    .loadClassAsContainer("S2DPacketOpenWindow", new S2DPacketOpenWindow())
                    .loadClassAsContainer("S2EPacketCloseWindow", new S2EPacketCloseWindow())
                    .loadClassAsContainer("S02PacketChat", new S02PacketChat())
                    .loadClassAsContainer("S3APacketTabComplete", new S3APacketTabComplete())
                    .loadClassAsContainer("S3BPacketScoreboardObjective", new S3BPacketScoreboardObjective())
                    .loadClassAsContainer("S3CPacketUpdateScore", new S3CPacketUpdateScore())
                    .loadClassAsContainer("S3DPacketDisplayScoreboard", new S3DPacketDisplayScoreboard())
                    .loadClassAsContainer("S3EPacketTeams", new S3EPacketTeams())
                    .loadClassAsContainer("S3FPacketCustomPayload", new S3FPacketCustomPayload())
                    .loadClassAsContainer("S03PacketTimeUpdate", new S03PacketTimeUpdate())
                    .loadClassAsContainer("S04PacketEntityEquipment", new S04PacketEntityEquipment())
                    .loadClassAsContainer("S05PacketSpawnPosition", new S05PacketSpawnPosition())
                    .loadClassAsContainer("S06PacketUpdateHealth", new S06PacketUpdateHealth())
                    .loadClassAsContainer("S07PacketRespawn", new S07PacketRespawn())
                    .loadClassAsContainer("S08PacketPlayerPosLook", new S08PacketPlayerPosLook())
                    .loadClassAsContainer("S09PacketHeldItemChange", new S09PacketHeldItemChange())
                    .loadClassAsContainer("S10PacketSpawnPainting", new S10PacketSpawnPainting())
                    .loadClassAsContainer("S11PacketSpawnExperienceOrb", new S11PacketSpawnExperienceOrb())
                    .loadClassAsContainer("S12PacketEntityVelocity", new S12PacketEntityVelocity())
                    .loadClassAsContainer("S13PacketDestroyEntities", new S13PacketDestroyEntities())
                    .loadClassAsContainer("S14PacketEntity", new S14PacketEntity())
                    .loadClassAsContainer("S18PacketEntityTeleport", new S18PacketEntityTeleport())
                    .loadClassAsContainer("S19PacketEntityHeadLook", new S19PacketEntityHeadLook())
                    .loadClassAsContainer("S20PacketEntityProperties", new S20PacketEntityProperties())
                    .loadClassAsContainer("S21PacketChunkData", new S21PacketChunkData())
                    .loadClassAsContainer("S22PacketMultiBlockChange", new S22PacketMultiBlockChange())
                    .loadClassAsContainer("S23PacketBlockChange", new S23PacketBlockChange())
                    .loadClassAsContainer("S24PacketBlockAction", new S24PacketBlockAction())
                    .loadClassAsContainer("S25PacketBlockBreakAnim", new S25PacketBlockBreakAnim())
                    .loadClassAsContainer("S26PacketMapChunkBulk", new S26PacketMapChunkBulk())
                    .loadClassAsContainer("S27PacketExplosion", new S27PacketExplosion())
                    .loadClassAsContainer("S28PacketEffect", new S28PacketEffect())
                    .loadClassAsContainer("S29PacketSoundEffect", new S29PacketSoundEffect())
                    .loadClassAsContainer("S30PacketWindowItems", new S30PacketWindowItems())
                    .loadClassAsContainer("S31PacketWindowProperty", new S31PacketWindowProperty())
                    .loadClassAsContainer("S32PacketConfirmTransaction", new S32PacketConfirmTransaction())
                    .loadClassAsContainer("S33PacketUpdateSign", new S33PacketUpdateSign())
                    .loadClassAsContainer("S34PacketMaps", new S34PacketMaps())
                    .loadClassAsContainer("S35PacketUpdateTileEntity", new S35PacketUpdateTileEntity())
                    .loadClassAsContainer("S36PacketSignEditorOpen", new S36PacketSignEditorOpen())
                    .loadClassAsContainer("S37PacketStatistics", new S37PacketStatistics())
                    .loadClassAsContainer("S38PacketPlayerListItem", new S38PacketPlayerListItem())
                    .loadClassAsContainer("S39PacketPlayerAbilities", new S39PacketPlayerAbilities())
                    .loadClassAsContainer("S40PacketDisconnect", new S40PacketDisconnect())
                    .loadClassAsContainer("S41PacketServerDifficulty", new S41PacketServerDifficulty())
                    .loadClassAsContainer("S42PacketCombatEvent", new S42PacketCombatEvent())
                    .loadClassAsContainer("S43PacketCamera", new S43PacketCamera())
                    .loadClassAsContainer("S44PacketWorldBorder", new S44PacketWorldBorder())
                    .loadClassAsContainer("S45PacketTitle", new S40PacketDisconnect())
                    .loadClassAsContainer("S46PacketSetCompressionLevel", new S46PacketSetCompressionLevel())
                    .loadClassAsContainer("S47PacketPlayerListHeaderFooter", new S47PacketPlayerListHeaderFooter())
                    .loadClassAsContainer("S48PacketResourcePackSend", new S48PacketResourcePackSend())
                    .loadClassAsContainer("S49PacketUpdateEntityNBT", new S49PacketUpdateEntityNBT());
        }

        // enums
        if (executor != null) {
            //executor.loadEnum("C0BEntityAction", C0BPacketEntityAction.Action.class);
            //executor.getGlobalSymbolTable().get("C0BPacketEntityAction").getSymbols().set("Action", C0BPacketEntityAction.Action.class);

            LoadHelper.INSTANCE.loadEnum("Action", (Class) C0BPacketEntityAction.Action.class, executor.getValue("C0BPacketEntityAction").getSymbols());
            LoadHelper.INSTANCE.loadEnum("Action", (Class) C02PacketUseEntity.Action.class, executor.getValue("C02PacketUseEntity").getSymbols());
            LoadHelper.INSTANCE.loadEnum("Action", (Class) C07PacketPlayerDigging.Action.class, executor.getValue("C07PacketPlayerDigging").getSymbols());

            executor.loadEnum("EnumFacing", EnumFacing.class);
        }

        // other classes
        if (executor != null) {
            executor.loadClassAsContainer("BlockPos", new BlockPos(0, 0, 0));
        }
    }

    private String readFile(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, Charset.defaultCharset());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "";
    }

}
