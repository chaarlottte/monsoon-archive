package wtf.monsoon.impl.ui.character;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.*;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventUpdateEnumSetting;
import wtf.monsoon.impl.module.visual.CharacterRenderer;
import wtf.monsoon.impl.ui.ScalableScreen;

import java.awt.*;

public class CharacterManager {

    @Getter @Setter
    private boolean dragging;

    @Getter @Setter
    private float dragX, dragY;

    @Getter @Setter
    private float x, y, width, height;

    private CharacterRenderer configModule;

    private AnimatedResourceLocation configIssueGif;

    private float animTime = 150f;

    @Getter
    private final Animation toggleHudAnimation = new Animation(() -> animTime, false, () -> Easing.CUBIC_IN_OUT);

    private boolean shouldSetValue = false;

    private CharacterRenderer.Image newValue = null;

    private Timer animResetTimer = new Timer();

    public CharacterManager() {
        Wrapper.getEventBus().subscribe(this);
        configModule = Wrapper.getModule(CharacterRenderer.class);
        configIssueGif = new AnimatedResourceLocation("monsoon/config_issue", 107, 3);
    }

    @EventLink
    private Listener<EventUpdateEnumSetting> eventUpdateEnumSettingListener = e -> {
        if(e.getSetting().equals(configModule.getImage())) {

            this.getToggleHudAnimation().setState(false);
            shouldSetValue = true;
            newValue = (CharacterRenderer.Image) e.getNewValue();
            animResetTimer.reset();
            e.setCancelled(true);
        }
    };

    @EventLink
    private Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted < 10) {
            this.updateWidthHeight(true, configModule.getImage().getValue());
        }

        if (Minecraft.getMinecraft().currentScreen == null) {
            this.getToggleHudAnimation().setState(false);
        }
    };

    public void initGui(GuiScreen guiScreen) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        if (this.isValidGuiScreen(guiScreen)) {
            this.updateWidthHeight(false, configModule.getImage().getValue());
        }

        this.getToggleHudAnimation().setState(true);
    }

    public void draw(int mouseX, int mouseY, float partialTicks, GuiScreen guiScreen) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final float scaleFactor = 1.0f / (sr.getScaleFactor() * .5f);

        if(this.isValidGuiScreen(guiScreen)) {
            if (!Mouse.isButtonDown(0)) {
                this.dragging = false;
            }

            if (shouldSetValue && newValue != null) {
                if (animResetTimer.hasTimeElapsed(animTime, false)) {
                    configModule.getImage().setValueSilent(newValue);

                    this.updateWidthHeight(true, newValue);
                    getToggleHudAnimation().setState(true);
                    shouldSetValue = false;
                    newValue = null;
                }
            }

            drag(mouseX / scaleFactor, mouseY / scaleFactor);

            RenderUtil.scaleXY(getX() + getWidth() / 2f, getY() + getHeight() / 2f, getToggleHudAnimation(), this::renderImage);
        }

    }

    private void renderImage() {
        switch(configModule.getImage().getValue()) {
            case ASTOLFO:
                this.setWidth(175f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/astolfo_1.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case FELIX:
                this.setWidth(175f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/felix.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case SIMON:
                this.setWidth(175f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/simon.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case BARRY:
                this.setWidth(225f);
                this.setHeight(226f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/barry.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case BLAHAJ:
                this.setWidth(175f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/blahaj.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case TEE_GRIZZLY:
                this.setWidth(175f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/retard.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case KOBLEY:
                this.setWidth(240f);
                this.setHeight(200f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/kobley.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case CONFIG_ISSUE:
                this.setWidth(172.5f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(configIssueGif.getTexture(), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                configIssueGif.update();
                break;
            case MR_WOOD:
                this.setWidth(172.5f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/wood.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case SKEPPY:
                this.setWidth(260f);
                this.setHeight(140f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/nathan.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
            case HAIKU:
                this.setWidth(175f);
                this.setHeight(250f);
                DrawUtil.draw2DImage(new ResourceLocation("monsoon/characters/haiku.png"), getX(), getY(), getWidth(), getHeight(), Color.WHITE);
                break;
        }
    }

    public void drag(float mx, float my) {
        if(!this.dragging && Mouse.isButtonDown(0)) this.dragging = false;
        if (this.dragging) {
            this.setX(mx + this.dragX);
            this.setY(my + this.dragY);
            if(!Mouse.isButtonDown(0)) {
                this.dragging = false;
            }
        }
        if(this.isHovered(mx, my) && Mouse.isButtonDown(0)) {
            if (!this.dragging) {
                this.dragX =  (getX() - mx);
                this.dragY =  (getY() - my);
                this.dragging = true;
            }
        }
    }

    public void onClick(int mouseX, int mouseY, int mouseButton, GuiScreen guiScreen) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final float scaleFactor = 1.0f / (sr.getScaleFactor() * .5f);

        if(this.isValidGuiScreen(guiScreen)) {
            if (mouseButton == 0 && this.isHovered(mouseX, mouseY)) {
                this.dragging = true;
            }
        }
    }

    private void updateWidthHeight(boolean updateXY, CharacterRenderer.Image image) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        switch(image) {
            case ASTOLFO:
            case SIMON:
            case FELIX:
            case TEE_GRIZZLY:
                if(updateXY) {
                    this.setX(sr.getScaledWidth() - 185f);
                    this.setY(sr.getScaledHeight() - 250f);
                }
                this.setWidth(175f);
                this.setHeight(250f);
                break;
            case BLAHAJ:
            case HAIKU:
                if(updateXY) {
                    this.setX(sr.getScaledWidth() - 185f);
                    this.setY(sr.getScaledHeight() - 260f);
                }
                this.setWidth(175f);
                this.setHeight(250f);
                break;
            case KOBLEY:
                if(updateXY) {
                    this.setX(sr.getScaledWidth() - 250f);
                    this.setY(sr.getScaledHeight() - 210f);
                }
                this.setWidth(240f);
                this.setHeight(200f);
                break;
            case CONFIG_ISSUE:
            case MR_WOOD:
                if(updateXY) {
                    this.setX(sr.getScaledWidth() - 182.5f);
                    this.setY(sr.getScaledHeight() - 260f);
                }
                this.setWidth(172.5f);
                this.setHeight(250f);
                break;
            case BARRY:
                if(updateXY) {
                    this.setX(sr.getScaledWidth() - 235f);
                    this.setY(sr.getScaledHeight() - 236f);
                }
                this.setWidth(225f);
                this.setHeight(226f);
                break;
            case SKEPPY:
                if(updateXY) {
                    this.setX(sr.getScaledWidth() - 260f);
                    this.setY(sr.getScaledHeight() - 140f);
                }
                this.setWidth(260f);
                this.setHeight(140f);
                break;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state, GuiScreen guiScreen) {
        this.dragging = false;
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= getX() && mouseY >= getY() && mouseX <= getX() + getWidth() && mouseY <= getY() + getHeight();
    }

    private boolean isValidGuiScreen(GuiScreen guiScreen) {
        if(guiScreen == null) return false;
        else if(!configModule.isEnabled()) return false;
        else if(Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) return false;
        else if(Minecraft.getMinecraft().thePlayer.ticksExisted < 20) return false;
        else if(!Minecraft.getMinecraft().getNetHandler().doneLoadingTerrain) return false;
        else if(guiScreen instanceof GuiChat) return false;
        else if(!(guiScreen instanceof ScalableScreen) && !configModule.renderInMinecraftGuis()) return false;
        else return true;
    }

}
