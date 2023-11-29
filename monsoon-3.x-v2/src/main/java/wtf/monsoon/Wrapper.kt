package wtf.monsoon

import me.bush.eventbuskotlin.*
import org.apache.logging.log4j.*
import wtf.monsoon.backend.manager.*
import wtf.monsoon.backend.manager.script.ScriptManager
import wtf.monsoon.client.command.*
import wtf.monsoon.client.handler.*
import wtf.monsoon.client.modules.client.*
import wtf.monsoon.client.modules.hud.*
import wtf.monsoon.client.modules.visual.*
import wtf.monsoon.client.modules.movement.*
import wtf.monsoon.client.modules.combat.*
import wtf.monsoon.client.modules.exploit.*
import wtf.monsoon.client.modules.player.*
import wtf.monsoon.client.ui.kpanel.UIScreen
import wtf.monsoon.client.util.ui.*
import wtf.monsoon.misc.protection.ProtectionManager
import wtf.monsoon.misc.via.*
import java.util.*

object Wrapper {

    @JvmStatic lateinit var monsoon: Monsoon
    @JvmStatic lateinit var logger: Logger
    @JvmStatic val developerBuild = true

    @JvmStatic
    fun init() {
        logger = LogManager.getLogger("monsoon")

        logger.info("Starting Monsoon 3.0")
        monsoon = Monsoon()

        monsoon.fileManager = FileManager()

        monsoon.protectionManager = ProtectionManager()
        monsoon.protectionManager.init()

        logger.info("Initialising event bus")

        monsoon.bus = EventBusNoCache()
        monsoon.bus.subscribe(monsoon)

        monsoon.packetHandler = PacketHandler()

        // Processor.INSTANCE.subscribe();
        logger.info("Initialising modules")
        monsoon.moduleManager = ModuleManager()

        monsoon.moduleManager[ClickGUI::class.java] = ClickGUI()
        monsoon.moduleManager[HUDModule::class.java] = HUDModule()
        monsoon.moduleManager[BlockAnimations::class.java] = BlockAnimations()
        monsoon.moduleManager[Sprint::class.java] = Sprint()
        monsoon.moduleManager[Velocity::class.java] = Velocity()
        monsoon.moduleManager[Aura::class.java] = Aura()
        monsoon.moduleManager[Speed::class.java] = Speed()
        monsoon.moduleManager[KeepSprint::class.java] = KeepSprint()
        monsoon.moduleManager[Scaffold::class.java] = Scaffold()
        monsoon.moduleManager[Disabler::class.java] = Disabler()
        monsoon.moduleManager[Blink::class.java] = Blink()
        monsoon.moduleManager[NoC03::class.java] = NoC03()
        monsoon.moduleManager[NoFall::class.java] = NoFall()
        monsoon.moduleManager[Flight::class.java] = Flight()
        monsoon.moduleManager[NoSlow::class.java] = NoSlow()
        monsoon.moduleManager[PingSpoof::class.java] = PingSpoof()
        monsoon.moduleManager[Stealer::class.java] = Stealer()
        monsoon.moduleManager[InventoryManager::class.java] = InventoryManager()
        monsoon.moduleManager[TargetStrafe::class.java] = TargetStrafe()
        monsoon.moduleManager[ItemPhysics::class.java] = ItemPhysics()

        monsoon.scriptManager = ScriptManager()
        monsoon.scriptManager.initialise()

        // instantiation funkiness
        monsoon.moduleManager.forEach {
            it.value.reflect()
        }

        logger.info("Initialising commands")
        monsoon.commandManager = CommandManager()
        monsoon.commandManager[SayCommand::class.java] = SayCommand()
        monsoon.commandManager[ToggleCommand::class.java] = ToggleCommand()
        monsoon.commandManager[ConfigCommand::class.java] = ConfigCommand()
        monsoon.commandManager[HClipCommand::class.java] = HClipCommand()
        monsoon.commandManager[VClipCommand::class.java] = VClipCommand()
        monsoon.commandManager[ReloadEvents::class.java] = ReloadEvents()

        logger.info("Initialising NanoVG")
        monsoon.nvg = NVGWrapper()
        monsoon.nvg.init(ArrayList())

        monsoon.dropdownGui = UIScreen()

        monsoon.moduleManager.getModule(HUDModule::class.java).setEnabled(true)

        monsoon.targetsManager = TargetsManager()
        monsoon.bus.subscribe(monsoon.targetsManager)

        monsoon.fileManager.loadConfig("default")
        monsoon.fileManager.loadBinds()

        monsoon.altManager = AltManager()
        monsoon.fileManager.loadAlts()

        try {
            ViaMCP.getInstance().start()
            ViaMCP.getInstance().initAsyncSlider()
            monsoon.bus.subscribe(ViaMCPFixes())
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @JvmStatic
    fun shutdown() {
        monsoon.fileManager.saveConfig("default")
        monsoon.fileManager.saveBinds()
        monsoon.fileManager.saveAlts()
    }
}