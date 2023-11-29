package wtf.monsoon.impl.ui.login.draw;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class LoginEmailField extends Drawable {
    @Getter @Setter String email = "";
    Animation focused = new Animation(() -> 400f, false, () -> Easing.CIRC_IN_OUT);
    public LoginEmailField(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        Keyboard.enableRepeatEvents(true);

        Color accent1 = new Color(0, 238, 255, 255),
                accent2 = new Color(135, 56, 232, 255),
                background = Color.BLACK;
        Color fade1 = ColorUtil.fadeBetween(10, 0, accent1, accent2);
        float animVal = (float) focused.getAnimationFactor();

        RoundedUtils.round(getX(),getY(),getWidth(),getHeight(),5, new Color(0x111111));
        RoundedUtils.round(getX() + 1,getY() + 1,getWidth() - 2,getHeight() - 2,4, background);
        RenderUtil.rect(getX()+getWidth()/2f-4-(Math.max(20, Wrapper.getFontUtil().productSans.getStringWidth(email)/2f-10))*animVal, getY()+getHeight()-5, 8+(Math.max(20, Wrapper.getFontUtil().productSans.getStringWidth(email)/2f-10)*2)*animVal, 1,  ColorUtil.interpolate(background,fade1,focused.getAnimationFactor()/2));
        Wrapper.getFontUtil().productSans.drawCenteredString(email,getX()+getWidth()/2f, getY()+getHeight()/2f-Wrapper.getFontUtil().productSans.getHeight()/2f-1-animVal, ColorUtil.interpolate(new Color(0x3A3A3A), new Color(0x9D9D9D), focused.getAnimationFactor()), false);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if(hovered(mouseX, mouseY) && click.equals(Click.LEFT)) {
            focused.setState(true);
        } else {
            focused.setState(false);
        }
        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(focused.getState()) {
            if(ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                email += typedChar;
            }
            if(email.length() != 0) {
                if(keyCode== Keyboard.KEY_BACK) {
                    email = email.substring(0, email.length() - 1);
                }
            }
            if (GuiScreen.isCtrlKeyDown()) {
                if (keyCode == Keyboard.KEY_V) {
                    try {
                        email = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                    } catch (UnsupportedFlavorException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
