package wtf.monsoon.impl.processor.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.processor.Processor;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.module.hud.NotificationsModule;
import wtf.monsoon.impl.ui.notification.NotificationType;

import java.awt.*;

public class LagbackProcessor extends Processor {

    @Getter
    private int totalFlags, indicatorTicks;

    @Getter
    private boolean shouldShowIndicator = false;

    private String flagIndicatorMessage = "";

    @EventLink
    private final Listener<EventPreMotion> eventPreMotionListener = e -> {

        if(shouldShowIndicator) {
            if(indicatorTicks >= 20) {
                shouldShowIndicator = false;
                flagIndicatorMessage = "";
            }
            indicatorTicks++;
        } else indicatorTicks = 0;
    };

    @EventLink
    private final Listener<EventRender2D> eventRender2DListener = e -> {
        FontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = e.getSr();

        if(shouldShowIndicator) {
            int l1 = sr.getScaledWidth() / 2 - (fr.getStringWidth(this.flagIndicatorMessage) / 2);
            int i1 = sr.getScaledHeight() / 2 + 8;
            fr.drawString(this.flagIndicatorMessage, l1 + 1, i1, 0);
            fr.drawString(this.flagIndicatorMessage, l1 - 1, i1, 0);
            fr.drawString(this.flagIndicatorMessage, l1, i1 + 1, 0);
            fr.drawString(this.flagIndicatorMessage, l1, i1 - 1, 0);
            fr.drawString(this.flagIndicatorMessage, l1, i1, Color.RED);
        }
    };

    @EventLink
    private final Listener<EventPacket> eventPacketListener = e -> {
        if(mc.thePlayer == null || mc.theWorld == null || !mc.getNetHandler().doneLoadingTerrain) return;

        if(e.getPacket() instanceof S08PacketPlayerPosLook) {
            Wrapper.getMonsoon().getModuleManager().getModulesToDisableOnFlag().forEach(this::alertFlag);
        }
    };

    private void alertFlag(Module m) {
        if(m.isEnabled()) {
            switch (Wrapper.getModule(NotificationsModule.class).flagAlert.getValue()) {
                case NOTIFICATION:
                    Wrapper.getNotifManager().notify(NotificationType.WARNING, "Flag Detected!", "Disabled " + m.getName() + " due to a flag.");
                    m.toggle();
                    break;
                case INDICATOR:
                    flagIndicatorMessage = m.getName() + " disabled";
                    shouldShowIndicator = true;
                    indicatorTicks = 0;
                    m.toggle();
                    break;
            }
        }
    }

}
