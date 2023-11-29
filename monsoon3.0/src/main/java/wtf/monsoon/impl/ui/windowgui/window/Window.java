package wtf.monsoon.impl.ui.windowgui.window;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.drawables.CategoryDrawable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Surge
 * @since 28/08/2022
 */
public class Window {

    @Getter
    @Setter
    private float x, y, width, height, lastX, lastY;

    @Getter
    @Setter
    private boolean dragging;

    @Getter
    private final List<CategoryDrawable> categoryDrawables = new ArrayList<>();

    @Getter
    @Setter
    private CategoryDrawable selected;

    // in case i do change something
    private final Animation dragAnimation = new Animation(() -> 400f, false, () -> Easing.CUBIC_IN_OUT);

    public Window(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        for (Category category : Category.values()) {
            categoryDrawables.add(new CategoryDrawable(this, category, getX(), getY(), 80, 20));
        }

        selected = categoryDrawables.get(0);
    }

    public void render(float mouseX, float mouseY, int mouseDelta) {
        if (dragAnimation.getState()) {
            setX(mouseX - lastX);
            setY(mouseY - lastY);
        }

        int alpha = 150;

        RoundedUtils.shadowGradient(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 10, 22, 0.5f,
                ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)),
                ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)),
                ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, alpha), new Color(135, 56, 232, alpha)), false
        );

        RoundedUtils.outline(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2, 10, 1f, 1f,
                ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 180, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 90, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255))
        );

        RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 10, WindowGUI.BACKGROUND);

        Wrapper.getFontUtil().greycliff40.drawStringWithGradient("Monsoon", getX() + 7, getY() + 1,
                ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                ColorUtil.fadeBetween(10, 0, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)),
                false
        );

        Wrapper.getFont().drawString(Wrapper.getMonsoon().getVersion(), getX() + 88, getY() + 2, Color.WHITE, false);

        RoundedUtils.round(getX(), getY() + 25, 80, getHeight() - 80, 5, WindowGUI.LAYER_ONE);
        RenderUtil.drawRect(getX() + 0.5, getY() + 25.5, 20, getHeight() - 81, WindowGUI.LAYER_ONE.getRGB());

        RoundedUtils.round(getX() + 85, getY() + 25, getWidth() - 90, getHeight() - 80, 5, WindowGUI.LAYER_ONE);

        float categoryY = getY() + 25;
        for (CategoryDrawable categoryDrawable : categoryDrawables) {
            categoryDrawable.setX(getX());
            categoryDrawable.setY(categoryY);

            categoryDrawable.draw(mouseX, mouseY, mouseDelta);

            categoryY += categoryDrawable.getHeight();
        }

        Minecraft mc = Minecraft.getMinecraft();

        mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
        mc.currentScreen.drawTexturedModalRect(getX() + 12, getY() + getHeight() - 44, 32, 32, 32, 32);

        Wrapper.getFont().drawString(mc.getSession().getUsername(), getX() + 48, getY() + getHeight() - 44, Color.WHITE, false);

        RoundedUtils.round(getX() + 48, getY() + getHeight() - 31, 110, 8, 3, WindowGUI.INTERACTABLE.darker());

        RoundedUtils.round(getX() + 48, getY() + getHeight() - 31, ((mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth()) * 100) + (mc.thePlayer.getAbsorptionAmount() / 4) * 10, 8, 3, new Color(250, 250, 0));
        RoundedUtils.round(getX() + 48, getY() + getHeight() - 31, (mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth()) * 100, 8, 3, Wrapper.getPallet().getMain());

        Wrapper.getFont().drawString((mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) + " / " + (mc.thePlayer.getMaxHealth() + 4), getX() + 48, getY() + getHeight() - 22, Color.WHITE, false);
    }

    public void mouseClicked(float mouseX, float mouseY, Click click) {
        if (mouseX >= getX() && mouseY >= getY() && mouseX <= getX() + getWidth() && mouseY <= getY() + 25) {
            lastX = mouseX - getX();
            lastY = mouseY - getY();

            dragAnimation.setState(true);
        }

        getCategoryDrawables().forEach(categoryDrawable -> categoryDrawable.mouseClicked(mouseX, mouseY, click));
    }

    public void mouseReleased(int mouseX, int mouseY, Click click) {
        dragAnimation.setState(false);

        getCategoryDrawables().forEach(categoryDrawable -> categoryDrawable.mouseReleased(mouseX, mouseY, click));
    }

    public void keyTyped(char typedChar, int keyCode) {
        getCategoryDrawables().forEach(categoryDrawable -> categoryDrawable.keyTyped(typedChar, keyCode));
    }

}
