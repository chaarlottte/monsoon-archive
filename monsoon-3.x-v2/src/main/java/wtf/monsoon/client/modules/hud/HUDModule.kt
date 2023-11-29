package wtf.monsoon.client.modules.hud

import com.viaversion.viaversion.util.Pair
import me.bush.eventbuskotlin.listener
import wtf.monsoon.Wrapper.monsoon
import wtf.monsoon.backend.Category
import wtf.monsoon.backend.manager.script.link.ModuleWrapper
import wtf.monsoon.backend.module.Module
import wtf.monsoon.client.event.EventRender2D
import wtf.monsoon.client.util.network.PacketUtil.mc
import wtf.monsoon.client.util.ui.NVGWrapper
import wtf.monsoon.temp.ITempUI
import wtf.monsoon.temp.TempRenderer
import java.awt.Color
import java.util.stream.Collectors

class HUDModule : Module("HUD", "The client's HUD.", Category.HUD), ITempUI {

    init {
        TempRenderer.register(this) { this.isEnabled() }
    }

    override fun render2D(nvg: NVGWrapper, width: Int, height: Int) {
        with (nvg) {
            text("Monsoon",8F,8F,"sbold",28F, Color(0x30CFFF), 3F)
            text("Monsoon",8F,8F,"regular",28F, Color(0x30CFFF))

            val sortedModules = monsoon.moduleManager.modules.stream()
                .sorted(Comparator.comparingDouble { module: Module ->
                    textWidth(ModuleWrapper(module).getNameAndMetadata(), "regular", 18F).toDouble()
                }.reversed())
                .filter { module -> module!!.isEnabled() }
                .filter { module -> module!!.visible.getValue() }
                .collect(Collectors.toList())

            var i = 0

            sortedModules.forEach { module ->
                val modName = ModuleWrapper(module).getNameAndMetadata()
                val textHeight = textHeight("regular", 18F) + 4F

                text(modName, width - 9F - 8F, 8 + i * textHeight + 1F, "regular", 18F, Color(0x30CFFF), NVGWrapper.Alignment.RIGHT_TOP, 10F)
                text(modName, width - 9F - 8F, 8 + i * textHeight + 1F, "regular", 18F, Color(0x30CFFF), NVGWrapper.Alignment.RIGHT_TOP, 10F)
                text(modName, width - 9F - 8F, 8 + i * textHeight + 1F, "regular", 18F, Color(0x30CFFF), NVGWrapper.Alignment.RIGHT_TOP)

                i++
            }
        }
    }

    companion object {

        fun generateModuleDataAndWidth(module: Module): Pair<Double, String> {
            val text = StringBuilder()
                .append(module.name)

            if (module.metadata.invoke().isNotEmpty()) {
                text.append(module.metadata.invoke())
            }

            return Pair(mc.fontRendererObj.getStringWidth(text.toString()).toDouble(), text.toString())
        }

    }

}