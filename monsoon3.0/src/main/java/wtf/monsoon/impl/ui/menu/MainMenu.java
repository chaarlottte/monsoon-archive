package wtf.monsoon.impl.ui.menu;

import lombok.Getter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.BlurUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.login.LoginScreen;
import wtf.monsoon.impl.ui.menu.windows.AltWindow;
import wtf.monsoon.impl.ui.menu.windows.FirstRunWindow;
import wtf.monsoon.impl.ui.menu.windows.WelcomeWindow;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.primitive.Button;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.particle.ParticleSystem;
import wtf.monsoon.impl.ui.scratch.VSScreen;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.glScalef;

/**
 * @author Surge
 * @since 20/08/2022
 */
public class MainMenu extends ScalableScreen {

    private final Animation openAnimation = new Animation(() -> 800f, false, () -> Easing.CUBIC_IN_OUT);
    private final Animation deleteWindowAnimation = new Animation(() -> 200f, false, () -> Easing.CUBIC_IN_OUT);
    private final Animation creditsAnimation = new Animation(() -> 200f, false, () -> Easing.CUBIC_IN_OUT);

    private final List<MenuButton> buttons = new ArrayList<>();

    @Getter
    private final List<Window> windows = new CopyOnWriteArrayList<>();

    private ParticleSystem particleSystem;

    private final WindowBar windowBar;

    Button btn = new Button(4,4,Wrapper.getFontUtil().productSansSmallBold,"Scratch Test Thingy", 6, (click) -> {
        if(click.equals(Click.LEFT))
            mc.displayGuiScreen(new VSScreen());
    });

    public MainMenu() {
        this.windowBar = new WindowBar(this);
        Wrapper.getEventBus().subscribe(this);
    }

    @Override
    public void init() {
        if(!Wrapper.loggedIn)
            mc.displayGuiScreen(new LoginScreen());
        particleSystem = new ParticleSystem();

        /******************* BUTTONS ********************/

        buttons.clear();

        // increment by 70

        buttons.add(new MenuButton("singleplayer", () -> mc.displayGuiScreen(new GuiSelectWorld(this)), getScaledWidth() / 2f - 140, getScaledHeight() - 120, 68, 68));
        buttons.add(new MenuButton("multiplayer", () -> mc.displayGuiScreen(new GuiMultiplayer(this)), getScaledWidth() / 2f - 70, getScaledHeight() - 120, 68, 68));
        buttons.add(new MenuButton("options", () -> mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings)), getScaledWidth() / 2f, getScaledHeight() - 120, 68, 68));
        buttons.add(new MenuButton("quit", () -> mc.shutdown(), getScaledWidth() / 2f + 70, getScaledHeight() - 120, 68, 68));

        /******************** WINDOWS ********************/

        windows.clear();

        windows.add(new WelcomeWindow(5, 5, 200, 150, 14));

        File checkFile = new File(mc.mcDataDir + "\\Monsoon");

        if (!checkFile.exists()) {
            windows.add(new FirstRunWindow(5, 200, 200, 150, 14));
        }

        windows.add(new AltWindow(this, getScaledWidth() - 205, 5, 200, 150, 14));
    }

    @Override
    public void render(float mouseX, float mouseY) {
        windows.removeIf(Window::shouldWindowClose);

        openAnimation.setState(true);
        creditsAnimation.setState(mouseX > getScaledWidth() / 2f - 140 && mouseX < getScaledWidth() / 2f + 140 && mouseY > 40 && mouseY < 80);

        mc.getTextureManager().bindTexture(new ResourceLocation("monsoon/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, getScaledWidth(), getScaledHeight(), getScaledWidth(), getScaledHeight());

        BlurUtil.alternateBlur(0, 0, getScaledWidth(), getScaledHeight(), (int) (4 * openAnimation.getAnimationFactor()));

        glScalef(2f, 2f, 2f);

        Wrapper.getFontUtil().greycliff40.drawCenteredString("Monsoon", (getScaledWidth() / 2f) / 2f, (float) (-40 + (60 * openAnimation.getAnimationFactor())), Color.WHITE, true);

        glScalef(0.5f, 0.5f, 0.5f);

        Wrapper.getFontUtil().greycliff26.drawString(Wrapper.getMonsoon().getVersion(), (float) (((getScaledWidth() / 2f) + 85) + ((getScaledWidth() / 2f) - ((getScaledWidth() / 2f) * openAnimation.getAnimationFactor()))), 45, Color.WHITE, true);

        String[] devs = {"quick", "surge", "Shoroa_", "NanoS (8 commits :OOO)", "and YesCheatPlus"};
        String concatenated = "Brought to you by: " + System.lineSeparator() + String.join(", ", devs);

        Wrapper.getFont().drawString(concatenated, 0, scaledHeight - 3 - Wrapper.getFont().getHeight() * 2, new Color(255, 255, 255, 150), true);

        float increase = 0;
        for (MenuButton button : buttons) {
            button.setY((float) (getScaledHeight() - ((120 + (increase - (increase * openAnimation.getAnimationFactor()))) * openAnimation.getAnimationFactor())));

            button.render(mouseX, mouseY);

            increase += 20;
        }

        deleteWindowAnimation.setState(false);

        for (Window window : windows) {
            if (window.isDragging()) {
                deleteWindowAnimation.setState(true);
            }

            window.render(mouseX, mouseY);
        }

        RenderUtil.drawGradientRect(0, getScaledHeight() - 50, getScaledWidth(), 50, new Color(0, 0, 0, 0).getRGB(), new Color(10, 10, 15, (int) (255 * deleteWindowAnimation.getAnimationFactor())).getRGB());

        float multiply = 0.5f;
        for (Window window : windows) {
            if (window.isDragging() && window.getY() > getScaledHeight() - 50) {
                multiply = 0.8f;
                break;
            }
        }

        Wrapper.getFont().drawCenteredString("Drag here to remove window", getScaledWidth() / 2f, getScaledHeight() - (float) deleteWindowAnimation.getAnimationFactor() * 20f, new Color(1f, 1f, 1f, 0.1f + (float) deleteWindowAnimation.getAnimationFactor() * multiply), false);

        windowBar.draw((int) mouseX, (int) mouseY);

        btn.draw(mouseX, mouseY, 0);
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY));
        windows.forEach(window -> window.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton)));
        windowBar.mouseClicked((int) mouseX, (int) mouseY);
        btn.mouseClicked(mouseX,mouseY,Click.getClick(mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        windows.removeIf(window -> window.isDragging() && window.getY() > getScaledHeight() - 50);
        windows.forEach(Window::mouseReleased);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        windows.forEach(window -> window.keyTyped(typedChar, keyCode));
    }

    @Override
    public void onGuiClosed() {
        openAnimation.resetToDefault();
    }

    private static class WindowBar {

        private final MainMenu menu;
        private final List<WindowElement> elements;
        private final Animation hover = new Animation(200f, false, Easing.LINEAR);

        public WindowBar(MainMenu menu) {
            this.menu = menu;
            elements = new ArrayList<>();

            elements.addAll(Arrays.asList(
                    new WindowElement("Welcome", () -> {
                        if (this.menu.getWindows().stream().anyMatch(window -> window instanceof WelcomeWindow)) {
                            this.menu.getWindows().removeIf(window -> window instanceof WelcomeWindow);
                        } else {
                            this.menu.getWindows().add(new WelcomeWindow(5, 5, 200, 150, 14));
                        }
                    }),

                    new WindowElement("Alt Manager", () -> {
                        if (this.menu.getWindows().stream().anyMatch(window -> window instanceof AltWindow)) {
                            this.menu.getWindows().removeIf(window -> window instanceof AltWindow);
                        } else {
                            this.menu.getWindows().add(new AltWindow(this.menu, 5, 5, 200, 150, 14));
                        }
                    })
            ));
        }

        public void draw(int mouseX, int mouseY) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

            float largest = 0;

            for (WindowElement element : elements) {
                if (Wrapper.getFont().getStringWidth(element.name) > largest) {
                    largest = Wrapper.getFont().getStringWidth(element.name);
                }
            }

            // bounds = new Rectangle((scaledResolution.getScaledWidth() / 2) - ((largest + 5) / 2), )
            hover.setState(mouseY <= elements.size() * (Wrapper.getFont().getHeight() + 2));

            RenderUtil.drawRect(
                    (scaledResolution.getScaledWidth() / 2f) - ((largest + 5) / 2),
                    -(elements.size() * (Wrapper.getFont().getHeight() + 2)) + ((elements.size() * (Wrapper.getFont().getHeight() + 2)) * hover.getAnimationFactor()),
                    largest + 10,
                    elements.size() * (Wrapper.getFont().getHeight() + 2),
                    0x90000000
            );

            float y = (float) (-(elements.size() * (Wrapper.getFont().getHeight() + 2) + 2) + ((elements.size() * (Wrapper.getFont().getHeight() + 2)) * hover.getAnimationFactor()) + 2);
            for (WindowElement element : elements) {
                Wrapper.getFont().drawCenteredString(element.name, scaledResolution.getScaledWidth() / 2f + 2, y, Color.WHITE, false);

                y += Wrapper.getFont().getHeight() + 2;
            }
        }

        public void mouseClicked(int mouseX, int mouseY) {
            if (hover.getState()) {
                ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

                float largest = 0;

                for (WindowElement element : elements) {
                    if (Wrapper.getFont().getStringWidth(element.name) > largest) {
                        largest = Wrapper.getFont().getStringWidth(element.name);
                    }
                }

                float y = (float) (-(elements.size() * (Wrapper.getFont().getHeight() + 2) + 2) + ((elements.size() * (Wrapper.getFont().getHeight() + 2)) * hover.getAnimationFactor()) + 2);
                for (WindowElement element : elements) {
                    if (
                            mouseX >= (scaledResolution.getScaledWidth() / 2f + 2) - (Wrapper.getFont().getStringWidth(element.name) / 2f) &&
                                    mouseX <= (scaledResolution.getScaledWidth() / 2f + 2) + (Wrapper.getFont().getStringWidth(element.name) / 2f) &&
                                    mouseY >= y &&
                                    mouseY <= y + Wrapper.getFont().getHeight()
                    ) {
                        element.create();
                    }

                    y += Wrapper.getFont().getHeight() + 2;
                }
            }
        }

    }

    private static class WindowElement {

        @Getter
        private final String name;
        private final Runnable create;

        public WindowElement(String name, Runnable create) {
            this.name = name;
            this.create = create;
        }

        public void create() {
            create.run();
        }

    }

}
