package wtf.monsoon.impl.ui.menu;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.BlurUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.api.util.render.StencilUtil;

import java.awt.*;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class MenuButton {

    private final ResourceLocation texture;
    private final Runnable onClick;

    private final float x;
    private float y;
    private final float width;
    private final float height;

    private final Animation hover = new Animation(() -> 250f, false, () -> Easing.CUBIC_OUT);

    public MenuButton(String iconName, Runnable onClick, float x, float y, float width, float height) {
        this.texture = new ResourceLocation("/monsoon/mainmenu/" + iconName + ".png");

        this.onClick = onClick;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(float mouseX, float mouseY) {
        hover.setState(isHovered(mouseX, mouseY));

        String name = this.texture.getResourcePath().replace("/monsoon/mainmenu/", "").replace(".png", "");

        String let = "";

        switch (name.charAt(0)) {
            case 's':
                let = "c";
                break;
            case 'm':
                let = "b";
                break;
            case 'o':
                let = "d";
                break;
            case 'q':
                let = "e";
                break;
        }

        Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.2);

        BlurUtil.blur_shader_2.bindFramebuffer(false);
        BlurUtil.preBlur();
        RoundedUtils.glRound(x, (float) (y - ((height / 4f) * hover.getAnimationFactor())), width, height, 16, bg.getRGB());
        BlurUtil.postBlur(2, 2);
        Wrapper.getMinecraft().getFramebuffer().bindFramebuffer(false);

        GlStateManager.color(1, 1, 1, 1);
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RoundedUtils.round(x, y, width, height, 16, Color.WHITE);
        StencilUtil.bindReadStencilBuffer(1);

        Wrapper.getFont().drawCenteredString(name.substring(0, 1).toUpperCase() + name.substring(1), x + width / 2f, y + height + Wrapper.getFont().getHeight() / 2 - (float) ((Wrapper.getFont().getHeight() - 1) * hover.getAnimationFactor()) * 2, new Color(255, 255, 255, (int) (255 * hover.getAnimationFactor())), true);

        StencilUtil.uninitStencilBuffer();
        GlStateManager.color(1, 1, 1, 1);

        float g = 0;
        float h = 0;

        Color c1 = new Color(0, 238, 255, 255);
        Color c2 = new Color(135, 56, 232, 255);

        RoundedUtils.shadowGradient(x - g, (float) ((y - ((height / 4f) * hover.getAnimationFactor())) - h), width + g * 2, height + h * 2, 16, (float) hover.getAnimationFactor() * 7, 1f,
                ColorUtil.fadeBetween(10, 270, c1, c2),
                ColorUtil.fadeBetween(10, 0, c1, c2),
                ColorUtil.fadeBetween(10, 180, c1, c2),
                ColorUtil.fadeBetween(10, 90, c1, c2), false
        );

        RoundedUtils.round(x, (float) (y - ((height / 4f) * hover.getAnimationFactor())), width, height, 16, bg);

        Wrapper.getFontUtil().menuIcons.drawCenteredString(let, x + width / 2f, (float) ((y - ((height / 4f) * hover.getAnimationFactor())) + height / 2f) - Wrapper.getFontUtil().menuIcons.getHeight() / 2f, new Color(1f, 1f, 1f, 0.5f + (float) hover.getAnimationFactor() / 3f), false);
    }

    public void mouseClicked(float mouseX, float mouseY) {
        if (hover.getState()) {
            onClick.run();
        }
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
