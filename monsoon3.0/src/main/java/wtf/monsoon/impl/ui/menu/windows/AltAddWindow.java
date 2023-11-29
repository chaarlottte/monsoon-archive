package wtf.monsoon.impl.ui.menu.windows;

import com.thealtening.api.response.Account;
import com.thealtening.auth.service.AlteningServiceType;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;
import wtf.monsoon.api.manager.alt.MicrosoftOAuth2Login;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.misc.server.packet.impl.MPacketUpdateUsername;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Surge
 * @since 22/08/2022
 */
public class AltAddWindow extends Window {

    private MainMenu menuContext;

    private final TextField email = new TextField("Email", getX() + 2, getY() + 16, getWidth() - 4, 16);
    private final TextField password = new TextField("Password", getX() + 2, getY() + 34, getWidth() - 4, 16);

    private Alt.Authenticator authenticator = Alt.Authenticator.MICROSOFT;

    private boolean waitingForApiKey = false;

    private final AltWindow.Button authButton = new AltWindow.Button("Auth: null", getX() + 2, getY() + 52, getWidth() - 4, 16, () -> {
        switch (authenticator) {
            case MICROSOFT:
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                authenticator = Alt.Authenticator.CRACKED;
                break;
            case CRACKED:
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.THEALTENING);
                authenticator = Alt.Authenticator.ALTENING;
                break;
            case ALTENING:
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                authenticator = Alt.Authenticator.OAUTH;
                break;
            case OAUTH:
                Wrapper.getMonsoon().getAltManager().getAlteningAuthentication().updateService(AlteningServiceType.MOJANG);
                authenticator = Alt.Authenticator.MICROSOFT;
                break;
        }
    });

    private final AltWindow.Button addButton = new AltWindow.Button("Add Alt", getX() + 2, getY() + 70, getWidth() - 4, 16, () -> {

        if (waitingForApiKey && authenticator == Alt.Authenticator.ALTENING) {
            if (!email.getText().trim().isEmpty()) {
                Wrapper.getMonsoon().getAltManager().setApiKey(email.getText());
                waitingForApiKey = false;
            }
        } else if(authenticator == Alt.Authenticator.OAUTH) {
            try {
                MicrosoftOAuth2Login microsoftOAuth2 = new MicrosoftOAuth2Login();
                microsoftOAuth2.getAccessToken();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {

            if (email.getText().trim().isEmpty()) {
                menuContext.getWindows().add(new ErrorWindow("Empty email / username field!", new String[]{"The email / username field cannot be empty!"}, Minecraft.getMinecraft().displayWidth / 4f - 100, 100, 200, 100, 14));
                return;
            }

            if (password.getText().trim().isEmpty() && authenticator.equals(Alt.Authenticator.MICROSOFT)) {
                menuContext.getWindows().add(new ErrorWindow("Empty password field!", new String[]{"The password cannot be empty whilst using", "Microsoft authentication!"}, Minecraft.getMinecraft().displayWidth / 4f - 100, 100, 200, 100, 14));
                return;
            }

            Alt alt = new Alt(email.getText(), password.getText(), authenticator);

            Wrapper.getMonsoon().getAltManager().addAlt(alt);
            Wrapper.getMonsoon().getConfigSystem().saveAlts(Wrapper.getMonsoon().getAltManager());
        }
    });

    private final AltWindow.Button fromClipboard = new AltWindow.Button("Import from clipboard", getX() + 2, getY() + 52, getWidth() - 4, 16, () -> {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (data.contains(":")) {
                String[] combo = data.split(":");
                email.setText(combo[0]);
                password.setText(combo[1]);
            } else if (data.contains("|")) {
                String[] combo = data.split("\\|");
                email.setText(combo[0]);
                password.setText(combo[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    private final AltWindow.Button randomName = new AltWindow.Button("Generate Random Name", getX() + 2, getY() + 52, getWidth() - 4, 16, () -> {
        try {
            String username = StringUtil.getValidUsername();
            while (username.length() > 16) username = StringUtil.getValidUsername();
            email.setText(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    private final AltWindow.Button generate = new AltWindow.Button("Generate Account", getX() + 2, getY() + 52, getWidth() - 4, 16, () -> {
        try {
            if (Wrapper.getMonsoon().getAltManager().getApiKey().equals("api-0000-0000-0000")) {
                System.out.println("Now listening for API key.");
                waitingForApiKey = true;
            } else {
                System.out.println("Generating acc");
                final Account account = Wrapper.getMonsoon().getAltManager().getAlteningAltFetcher().getAccount();
                email.setText(account.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    public AltAddWindow(MainMenu menuContext, float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);

        this.menuContext = menuContext;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);

        Wrapper.getFont().drawString("Add Alt", getX() + 4, getY() + 1, Color.WHITE, false);

        if (authenticator == Alt.Authenticator.CRACKED || authenticator == Alt.Authenticator.ALTENING) {
            password.focused = false;
        }

        authButton.setText("Auth: " + StringUtil.formatEnum(authenticator));

        if (waitingForApiKey && authenticator == Alt.Authenticator.ALTENING) {
            addButton.setText("Submit API Key");
        } else if(authenticator == Alt.Authenticator.OAUTH) {
            addButton.setText("Login with Microsoft");
        } else {
            addButton.setText("Add Alt");
        }

        email.setX(getX() + 2);
        password.setX(getX() + 2);
        authButton.setX(getX() + 2);
        addButton.setX(getX() + 2);
        fromClipboard.setX(getX() + 2);
        generate.setX(getX() + 2);
        randomName.setX(getX() + 2);

        email.setY(getY() + 16);
        password.setY(getY() + 34);
        authButton.setY(getY() + (authenticator == Alt.Authenticator.MICROSOFT ? 52 : authenticator == Alt.Authenticator.OAUTH ? 16 : 34));
        addButton.setY(getY() + (authenticator == Alt.Authenticator.MICROSOFT ? 88 : authenticator == Alt.Authenticator.OAUTH ? 34 : 70));
        fromClipboard.setY(getY() + 70);
        randomName.setY(getY() + 52);
        generate.setY(getY() + 52);

        email.emptyText = authenticator == Alt.Authenticator.MICROSOFT ? "Email" : authenticator == Alt.Authenticator.ALTENING ? (waitingForApiKey ? "Input your API key here" : "Alt Token") : "Username";

        setHeight(authenticator == Alt.Authenticator.MICROSOFT ? 106 : authenticator == Alt.Authenticator.OAUTH ? 58 :  90);

        if (authenticator != Alt.Authenticator.OAUTH) email.render(mouseX, mouseY);

        if (authenticator == Alt.Authenticator.MICROSOFT) {
            password.render(mouseX, mouseY);
            fromClipboard.render(mouseX, mouseY);
        }

        if (authenticator == Alt.Authenticator.ALTENING) {
            generate.render(mouseX, mouseY);
        }

        if (authenticator == Alt.Authenticator.CRACKED) {
            randomName.render(mouseX, mouseY);
        }

        authButton.render(mouseX, mouseY);
        addButton.render(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);

        email.clicked(mouseX, mouseY, click);

        if (authenticator == Alt.Authenticator.MICROSOFT) {
            password.clicked(mouseX, mouseY, click);
            fromClipboard.onClick(mouseX, mouseY, click.getButton());
        }

        if (authenticator == Alt.Authenticator.CRACKED) {
            randomName.onClick(mouseX, mouseY, click.getButton());
        }

        authButton.onClick(mouseX, mouseY, click.getButton());
        addButton.onClick(mouseX, mouseY, click.getButton());
        generate.onClick(mouseX, mouseY, click.getButton());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        email.keyTyped(typedChar, keyCode);

        if (authenticator == Alt.Authenticator.MICROSOFT) {
            password.keyTyped(typedChar, keyCode);
        }
    }

    private class TextField {
        private String emptyText;

        @Getter
        @Setter
        private String text = "";

        @Setter
        private float x;

        @Setter
        private float y;

        private final float width;
        private final float height;

        private boolean focused;

        private final Animation hover = new Animation(() -> 200f, false, () -> Easing.LINEAR);

        public TextField(String emptyText, float x, float y, float width, float height) {
            this.emptyText = emptyText;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void render(float mouseX, float mouseY) {
            hover.setState(mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);

            Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.6 - hover.getAnimationFactor() * 0.3);
            RoundedUtils.round(x, y, width, height, 2, bg);

            Keyboard.enableRepeatEvents(true);

            if (text.isEmpty() && !focused) {
                Wrapper.getFont().drawString(emptyText, x + 4, y + 2, Color.GRAY, false);
            } else {
                String old = text;

                if (emptyText.equalsIgnoreCase("Password")) {
                    text = text.replaceAll(".", "*");
                }

                Wrapper.getFont().drawString(text + (focused ? (System.currentTimeMillis() % 1000 >= 500 ? "_" : "") : ""), x + 4, y + 2, Color.WHITE, false);

                text = old;
            }
        }

        public void clicked(float mouseX, float mouseY, Click click) {
            if (hover.getState()) {
                focused = !focused;
            } else {
                focused = false;
            }
        }

        public void keyTyped(char typedChar, int keyCode) {
            if (focused) {
                if (keyCode == Keyboard.KEY_RETURN) {
                    focused = false;
                } else if (focused && Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
                    if (!text.isEmpty()) {
                        text = text.substring(0, text.length() - 1);
                    }
                } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    text = text + typedChar;
                }

                if (GuiScreen.isCtrlKeyDown()) {
                    if (keyCode == Keyboard.KEY_V) {
                        try {
                            text = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                        } catch (UnsupportedFlavorException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
