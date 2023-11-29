package wtf.monsoon.impl.ui.login.draw;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import store.vantage.api.models.AuthResponse;
import store.vantage.api.utils.AuthUtil;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.login.LoginScreen;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.misc.protection.ProtectedInitialization;

import java.awt.*;

public class LoginButton extends Drawable {

    Animation hovered = new Animation(() -> 200F,  false, () -> Easing.LINEAR);
    Animation enabled = new Animation(() -> 200F,  false, () -> Easing.LINEAR);

    @Setter
    private String email = "", password = "";

    @Getter private LoginScreen parent;

    public LoginButton(float x, float y, float width, float height, LoginScreen parent) {
        super(x, y, width, height);
        this.parent = parent;
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        //enabled.setState(email.contains("@") && password.length() > 6);
        enabled.setState(true);
        hovered.setState(hovered(mouseX, mouseY));

        Color accent1 = new Color(0, 238, 255, 255),
                accent2 = new Color(135, 56, 232, 255),
                background = Color.BLACK;
        Color fade1 = ColorUtil.fadeBetween(10, 0, accent1, accent2);
        float animVal = (float) hovered.getAnimationFactor();

        RoundedUtils.round(getX(),getY(),getWidth(),getHeight(),5, new Color(0x111111));
        RoundedUtils.round(getX() + 1,getY() + 1,getWidth() - 2,getHeight() - 2,4, background);
        RenderUtil.rect(
                getX()+getWidth()/2f-4-30*animVal,
                getY()+getHeight()-5, 8+60*animVal,
                1,
                ColorUtil.interpolate(background,fade1,hovered.getAnimationFactor()/2)
        );

        Wrapper.getFontUtil().productSans.drawCenteredString("Login",getX()+getWidth()/2f, getY()+getHeight()/2f-Wrapper.getFontUtil().productSans.getHeight()/2f-animVal*2, ColorUtil.interpolate(new Color(0x3A3A3A), new Color(0x9D9D9D), enabled.getAnimationFactor()), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        return false;
    }

    public boolean mouseClickedLoginButton(float mouseX, float mouseY, Click click) {
        if(hovered.getState() && enabled.getState() && click.equals(Click.LEFT)) {
            // Wrapper.getMonsoon().getNetworkManager().setEmail(email);
            // Wrapper.getMonsoon().getNetworkManager().setPassword(password);
            // new Thread(() -> Wrapper.getMonsoon().getNetworkManager().init()).start();
            // Wrapper.getMonsoon().getNetworkManager().init();
            this.fakeLogin(); // YOUR RETARDED ASS BETTER REMEMBER TO REMOVE THIS SHIT
            try {
                //AuthResponse resp = AuthUtil.authenticate(email);
                //System.out.printf("Auth Response: %s%n", resp);
                //System.out.printf("IsError? %s%n", resp.isError);
                //System.out.printf("Message: %s%n", resp.message);
            } catch (Exception e) {
                System.out.println("AuthError " + e.getStackTrace().toString());
            }
            return true;
        }
        return false;
    }

    /*
     * REMOVE THIS BEFORE YOU FUCKING RELEASE YOU RETARDED MONKEY
     * IF VANTAGE WAS UP THERE WOULD BE NO NEED FOR THIS SHIT
     * BUT NOOOO
     * THANKS ALAN
     * THANKS RYUZAKI
     * FUCK MY LIFE
     */
    private void fakeLogin() {
        Wrapper.loggedIn = true;
        new ProtectedInitialization().start();
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
