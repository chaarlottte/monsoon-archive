package wtf.monsoon.impl.ui.menu.windows;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import org.lwjgl.input.Mouse;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.misc.server.packet.impl.MPacketUpdateUsername;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glScalef;

/**
 * @author Surge
 * @since 22/08/2022
 */
public class AltWindow extends Window {

    private MainMenu menuInstance;

    private final Button add = new Button("Add Alt", getX(), getY(), getWidth() - 4, 16, () -> {
        if (menuInstance.getWindows().stream().anyMatch(window -> window instanceof AltAddWindow)) {
            menuInstance.getWindows().removeIf(window -> window instanceof AltAddWindow);
        } else {
            menuInstance.getWindows().add(new AltAddWindow(menuInstance, getX(), getY() + getHeight() + 5, getWidth(), 88, getHeader()));
        }
    });

    private final List<AltButton> altButtons = new ArrayList<>();

    private float scroll = 0;

    @Getter
    @Setter
    private static String status;

    public AltWindow(MainMenu mainMenuInstance, float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);

        this.menuInstance = mainMenuInstance;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);

        if (getStatus() == null) {
            AltWindow.setStatus("Logged in as " + Minecraft.getMinecraft().session.getUsername());
        }

        Wrapper.getFont().drawString("Alt Manager", getX() + 4, getY() + 1, Color.WHITE, false);
        Wrapper.getFont().drawString(getStatus(), getX() + getWidth() - Wrapper.getFont().getStringWidth(getStatus()) - 14 - 4, getY() + 1, Color.WHITE, false);

        refreshAlts();

        float altHeight = altButtons.size() * 18;

        int mouseDelta = Mouse.getDWheel();

        if (mouseDelta != 0 && mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() + getHeader() && mouseY <= getY() + getHeight() - 18) {
            scroll += mouseDelta * 0.05f;
        }

        scroll = MathHelper.clamp_float(scroll, -Math.max(0f, (altHeight - (getHeight() - getHeader() - 18))), 0f);

        RenderUtil.pushScissor(getX(), getY() + getHeader() + 2, getWidth(), getHeight() - getHeader() - 20);

        float y = getY() + getHeader() + 2;
        for (AltButton altButton : altButtons) {
            altButton.setX(getX() + 2);
            altButton.setY(y + scroll);

            altButton.render(mouseX, mouseY);

            y += 18;
        }

        RenderUtil.popScissor();

        add.x = getX() + 2;
        add.y = getY() + getHeight() - 18;

        add.render(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);

        if (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() + getHeader() && mouseY <= getY() + getHeight() - 18) {
            altButtons.forEach(button -> button.onClick(mouseX, mouseY, click.getButton()));
        }

        add.onClick(mouseX, mouseY, click.getButton());
    }

    public void refreshAlts() {
        List<AltButton> deleteBuffer = new ArrayList<>();

        for (AltButton altButton : altButtons) {
            if (!Wrapper.getMonsoon().getAltManager().getAlts().contains(altButton.alt)) {
                deleteBuffer.add(altButton);
            }
        }

        altButtons.removeAll(deleteBuffer);

        for (Alt alt : Wrapper.getMonsoon().getAltManager().getAlts()) {
            if (altButtons.stream().noneMatch(button -> button.alt == alt)) {
                altButtons.add(new AltButton(alt, getX(), getY(), getWidth() - 4, 16));
            }
        }
    }

    static class Button {

        @Setter
        private String text;

        @Getter
        @Setter
        private float x;

        @Getter
        @Setter
        private float y;

        @Getter
        private final float width;
        private final float height;

        private final Runnable onClick;

        public final Animation hover = new Animation(() -> 200f, false, () -> Easing.LINEAR);

        public Button(String text, float x, float y, float width, float height, Runnable onClick) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.onClick = onClick;
        }

        public void render(float mouseX, float mouseY) {
            hover.setState(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);

            Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.6 - hover.getAnimationFactor() * 0.3);
            RoundedUtils.round(x, y, width, height, 4, bg);

            Wrapper.getFont().drawString(text, x + 4, y + 2, Color.WHITE, true);
        }

        public void onClick(float mouseX, float mouseY, int button) {
            if (hover.getState()) {
                onClick.run();
            }
        }
    }

    static class AltButton extends Button {

        private final Alt alt;

        public AltButton(Alt alt, float x, float y, float width, float height) {
            // excuse the code style here, i needed to see it clearly
            super(alt.getAuthenticator().equals(Alt.Authenticator.CRACKED) ? alt.getEmail() + " (Cracked)" :
                    (alt.getUsername().equals("") ?
                            alt.getEmail().contains("@") ?
                                    alt.getEmail().substring(0, 3) + alt.getEmail().substring(3, alt.getEmail().indexOf('@')).replaceAll(".", "*") + alt.getEmail().substring(alt.getEmail().indexOf('@')) : alt.getEmail() : alt.getEmail()), x, y, width, height, () -> {

                new Thread(() -> {
                    AltWindow.setStatus("Logging in...");
                    Session session = null;
                    try {
                        session = alt.getSession();
                    } catch (Exception exception) {
                        AltWindow.setStatus("Login failed!");
                        exception.printStackTrace();
                        return;
                    }

                    if (session != null) {
                        Minecraft.getMinecraft().session = session;
                    }
                    AltWindow.setStatus("Logged in as " + Minecraft.getMinecraft().session.getUsername());
                    Wrapper.getMonsoon().getServer().sendPacket(new MPacketUpdateUsername(Minecraft.getMinecraft().session.getUsername()));
                }).start();
            });

            this.alt = alt;
        }

        @Override
        public void render(float mouseX, float mouseY) {
            super.render(mouseX, mouseY);

            glScalef(1.8f, 1.8f, 1.8f);

            float factor = 1 / 1.8f;

            float side = (getX() + getWidth() - Wrapper.getFontUtil().entypo14.getStringWidth(FontUtil.UNICODES_UI.TRASH) * 1.8f - 6) * factor;

            Wrapper.getFontUtil().entypo14.drawString(FontUtil.UNICODES_UI.TRASH, side, (getY() + 2) * factor, hover.getState() && mouseX >= getX() + getWidth() - 20 ? Color.WHITE : Color.GRAY, false);

            glScalef(factor, factor, factor);
        }

        @Override
        public void onClick(float mouseX, float mouseY, int button) {
            if (hover.getState()) {
                if (button == 0 && mouseX <= getX() + getWidth() - 20) {
                    super.onClick(mouseX, mouseY, button);
                } else if (button == 0 && mouseX >= getX() + getWidth() - 20) {
                    Wrapper.getMonsoon().getAltManager().removeAlt(this.alt);
                }
            }
        }
    }
}
