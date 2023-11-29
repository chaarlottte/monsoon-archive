package wtf.monsoon.impl.ui.notification;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.main.Main;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.module.hud.NotificationsModule;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glScalef;

public class Notification extends Drawable {

    // TODO: Different icons for each notification type
    // this would require updating the entypo font
    // and i dont want to!
    private final NotificationType type;
    private final String title;
    private final String description;

    private final Animation animation = new Animation(() -> 700F, true, () -> Easing.BACK_IN_OUT);

    private long initTime = 0L;

    public Notification(float x, float y, NotificationType type, String title, String description) {
        super(x, y, 125, 30);

        this.type = type;
        this.title = title;
        this.description = description;

        animation.resetToDefault();
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        if (initTime == 0L && animation.getAnimationFactor() >= 1) {
            initTime = System.currentTimeMillis();
        }

        long time = initTime > 0 ? System.currentTimeMillis() - initTime : 0;

        if (time >= 1500) {
            animation.setState(false);
        }

        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 5, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.2f));

        RenderUtil.pushScissor(getX(), getY(), MathHelper.clamp_float(getWidth() * (time / 1500f), 0, getWidth()), getHeight());

        RoundedUtils.gradient(getX(), getY(), getWidth(), getHeight(), 1f, Wrapper.getModule(NotificationsModule.class).barOpacity.getValue(), ColorUtil.getAccent(Wrapper.getModule(NotificationsModule.class).barDarken.getValue()));

        RenderUtil.popScissor();

        RoundedUtils.outline(getX(), getY(), getWidth(), getHeight(), 5f, 2f, 2f, ColorUtil.getAccent());

        glScalef(2, 2, 2);
        {
            float factor = 0.5f;
            Wrapper.getFontUtil().entypo18.drawString(type.icon, (getX() + 8) * factor, (getY() + 6) * factor, type.color, false);

            glScalef(factor, factor, factor);
        }

        Wrapper.getFontUtil().productSans.drawString(title, getX() + 27, getY() + 4, Color.WHITE, false);
        Wrapper.getFontUtil().productSansSmall.drawString(description, getX() + 27, getY() + 16, Color.WHITE, false);
    }

    @Override
    public float getWidth() {
        float titleWidth = Wrapper.getFontUtil().productSans.getStringWidth(this.title);
        float descWidth = Wrapper.getFontUtil().productSansSmall.getStringWidth(this.description);
        return Math.max(titleWidth, descWidth) + 47;
    }

    public Animation getAnimation() {
        return animation;
    }

    public boolean shouldNotificationHide() {
        return !animation.getState() && animation.getAnimationFactor() == 0;
    }

    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        return false;
    }

    public void mouseReleased(float mouseX, float mouseY, Click click) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }
}
