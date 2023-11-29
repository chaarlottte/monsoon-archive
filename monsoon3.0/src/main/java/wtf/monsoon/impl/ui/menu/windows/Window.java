package wtf.monsoon.impl.ui.menu.windows;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.ColourAnimation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.api.util.render.BlurUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class Window {

    @Getter
    private float x;

    @Getter
    private float y;

    @Getter
    private final float width;

    @Getter
    @Setter
    private float height;

    @Getter
    private final float header;

    @Getter
    private boolean dragging = false;

    boolean shouldClose;
    ColourAnimation closeButtonHover = new ColourAnimation(ColorUtil.TRANSPARENT, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.5), () -> 250F, false, () -> Easing.LINEAR);

    private float lastX;
    private float lastY;

    public Window(float x, float y, float width, float height, float header) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.header = header;
    }

    public void render(float mouseX, float mouseY) {
        boolean closeHovered = (mouseX >= x + width - 12.5f && mouseY >= y + 1.5f && mouseX <= x + width - 12.5f + 10.5f && mouseY <= y + 1.5f + 10.5f);
        closeButtonHover.setState(closeHovered);

        if (dragging) {
            x = mouseX - lastX;
            y = mouseY - lastY;
        }


        Color bg = ColorUtil.interpolate(Wrapper.getPallet().getBackground(), new Color(0, 0, 0, 0), 0.2);

        BlurUtil.blur_shader_2.bindFramebuffer(false);
        BlurUtil.preBlur();
        RoundedUtils.glRound(x, y, width, height, 5, bg.getRGB());
        BlurUtil.postBlur(6, 2);
        Wrapper.getMinecraft().getFramebuffer().bindFramebuffer(false);

        float g = 0;
        float h = 0;

        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];

        RoundedUtils.shadowGradient(x - g, y - h, width + g * 2, height + h * 2, 5, 10, 1f,
                ColorUtil.fadeBetween(10, 270, c1, c2),
                ColorUtil.fadeBetween(10, 0, c1, c2),
                ColorUtil.fadeBetween(10, 180, c1, c2),
                ColorUtil.fadeBetween(10, 90, c1, c2), false
        );

        RoundedUtils.round(x, y, width, height, 5, bg);

        RoundedUtils.gradient(x + 0.5f, y + header - 1, width - 1, 2, 0, 0.6f,
                ColorUtil.fadeBetween(10, 270, c1, c2),
                ColorUtil.fadeBetween(10, 0, c1, c2),
                ColorUtil.fadeBetween(10, 180, c1, c2),
                ColorUtil.fadeBetween(10, 90, c1, c2)
        );

        Wrapper.getFontUtil().entypo18.drawString(FontUtil.UNICODES_UI.NO, x + width - 10.5f, y + 2.5f, Color.WHITE, false);
    }

    public void mouseClicked(float mouseX, float mouseY, Click click) {
        boolean closeHovered = (mouseX >= x + width - 12.5f && mouseY >= y + 1.5f && mouseX <= x + width - 12.5f + 10.5f && mouseY <= y + 1.5f + 10.5f);
        if (click.equals(Click.LEFT) && mouseOverHeader(mouseX, mouseY)) {
            if (closeHovered) {
                shouldClose = true;
            }

            dragging = true;

            lastX = mouseX - x;
            lastY = mouseY - y;
        }
    }

    public void mouseReleased() {
        dragging = false;
    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public boolean mouseOverHeader(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + header;
    }

    public boolean shouldWindowClose() {
        return shouldClose;
    }
}
