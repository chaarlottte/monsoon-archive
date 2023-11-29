package wtf.monsoon.impl.ui.login;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.Gui;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.config.ConfigSystem;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.module.visual.Ambience;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.login.draw.LoginButton;
import wtf.monsoon.impl.ui.login.draw.LoginEmailField;
import wtf.monsoon.impl.ui.login.draw.LoginPassField;
import wtf.monsoon.impl.ui.menu.MainMenu;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.misc.protection.ProtectedInitialization;

import java.awt.*;
import java.io.IOException;

public class LoginScreen extends ScalableScreen {

    Animation anim1 = new Animation(() -> 800F, false, () -> Easing.LINEAR),
              anim2 = new Animation(() -> 400F, false, () -> Easing.LINEAR),
              anim3 = new Animation(() -> 500F, false, () -> Easing.CIRC_OUT);

    int progress = 0;

    LoginEmailField emailField;
    LoginPassField passField;
    LoginButton loginButton;

    private Timer loginCheckTimer = new Timer();
    private boolean checkingLogin = false, attemptingLogin = false;

    @Override
    public void init() {
        emailField = new LoginEmailField(0,0,120,20);
        passField = new LoginPassField(0,0,120,20);
        loginButton = new LoginButton(0,0,120,20, this);

        String[] credentials = ConfigSystem.getUsernamePassword();
        if(credentials.length == 2) {
            String username = credentials[0], password = credentials[1];
            emailField.setEmail(username);
            passField.setPassword(password);
        }
    }

    @Override
    public void render(float mouseX, float mouseY) {
        if(Wrapper.loggedIn) mc.displayGuiScreen(new MainMenu());

        if(progress == 0) anim1.setState(true);
        if(anim1.getAnimationFactor() == 1) anim2.setState(true);
        if(anim2.getAnimationFactor() == 1) progress++;
        if(progress == 1) anim3.setState(true);

        Color accent1 = new Color(0, 238, 255, 255),
              accent2 = new Color(135, 56, 232, 255),
              background = Color.BLACK;
        Gui.drawRect(0,0,scaledWidth,scaledHeight, background.getRGB());

        emailField.setX(scaledWidth/2f - emailField.getWidth()/2f);
        emailField.setY(scaledHeight/2f - emailField.getHeight()/2f);

        passField.setX(scaledWidth/2f - passField.getWidth()/2f);
        passField.setY(scaledHeight/2f - passField.getHeight()/2f + passField.getHeight() + 12);

        loginButton.setEmail(emailField.getEmail());
        loginButton.setPassword(passField.getPassword());

        loginButton.setX(scaledWidth/2f - loginButton.getWidth()/2f);
        loginButton.setY(scaledHeight/2f - loginButton.getHeight()/2f + loginButton.getHeight()*2 + 12*2);

        RenderUtil.scaleX(scaledWidth/2f,scaledHeight/2f,anim3, () -> {
            Wrapper.getFontUtil().productSansSmall.drawCenteredString("email", getScaledWidth()/2f, emailField.getY() - 10, new Color(0x3A3A3A), false);
            emailField.draw(mouseX,mouseY,0);
            Wrapper.getFontUtil().productSansSmall.drawCenteredString("password", getScaledWidth()/2f, passField.getY() - 10, new Color(0x3A3A3A), false);
            passField.draw(mouseX,mouseY,0);
            loginButton.draw(mouseX, mouseY, 0);
        });

        Wrapper.getFontUtil().greycliff40.drawCenteredString("Hi.",
                scaledWidth/2f,
                (float) (scaledHeight/2f-Wrapper.getFontUtil().greycliff40.getHeight()/2f - 70*anim3.getAnimationFactor()),
                ColorUtil.interpolate(background, Color.GRAY, anim1.getAnimationFactor()),false);
        Wrapper.getFontUtil().greycliff19.drawCenteredString("Before we continue you need to login.",
                scaledWidth/2f,
                (float) (scaledHeight/2f-Wrapper.getFontUtil().greycliff19.getHeight()/2f + 18 - 70*anim3.getAnimationFactor()),
                ColorUtil.interpolate(background, Color.GRAY, anim2.getAnimationFactor()),false);
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        emailField.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        passField.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        // loginButton.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton));
        attemptingLogin = loginButton.mouseClickedLoginButton(mouseX, mouseY, Click.getClick(mouseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        emailField.keyTyped(typedChar, keyCode);
        passField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}
