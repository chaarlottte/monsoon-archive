package wtf.monsoon.impl.ui.panel.elements;

import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.module.visual.ClickGUI;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;

import java.awt.*;
import java.util.ArrayList;

public class Panel extends Drawable {

    private final Category category;
    private final Animation expandAnimation = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT);
    private final ArrayList<ElementModule> modules = new ArrayList<>();

    private float scissorHeight = 0f;

    private float scroll = 0f;
    private float real = 0f;
    private final Animation scrollAnimation = new Animation(() -> 400f, false, () -> Easing.CIRC_OUT);

    private int categorySize;

    public Panel(Category category, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.category = category;

        loadModules();

        expandAnimation.setState(true);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        float moduleHeight = 0f;

        if(Wrapper.getMonsoon().getModuleManager().getModulesByCategory(category).size() != categorySize) loadModules();

        for (ElementModule module : modules) {
            moduleHeight += module.getOffset();
        }

        scissorHeight = MathHelper.clamp_float(moduleHeight, 0f, 340f);

        if (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() + getHeight() && mouseY <= getY() + getHeight() + scissorHeight) {
            real += mouseDelta * Wrapper.getModule(ClickGUI.class).scrollSpeed.getValue();
        }

        scrollAnimation.setState(scroll != real);

        if (scroll > real) {
            float scrollDiff = scroll - real;
            scroll -= (scrollDiff / Wrapper.getModule(ClickGUI.class).scrollDivider.getValue()) * scrollAnimation.getAnimationFactor();
        }

        if (scroll < real) {
            float scrollDiff = real - scroll;
            scroll += (scrollDiff / Wrapper.getModule(ClickGUI.class).scrollDivider.getValue()) * scrollAnimation.getAnimationFactor();
        }

        scroll = MathHelper.clamp_float(scroll, -Math.max(0f, moduleHeight - scissorHeight), 0f);
        real = MathHelper.clamp_float(real, -Math.max(0f, moduleHeight - scissorHeight), 0f);

        RoundedUtils.shadow(getX(), getY(), getWidth(), (float) (getHeight() + scissorHeight * expandAnimation.getAnimationFactor()) - 0.5f, 5, 22f, Color.BLACK);
        RenderUtil.rect(getX(), getY(), getWidth(), getHeight(), new Color(0x131313));

        Wrapper.getFontUtil().productSansSmall.drawCenteredString(StringUtil.formatEnum(category), getX() + getWidth() / 2f, getY() + getHeight() / 2f - Wrapper.getFont().getHeight() / 2f, Color.WHITE, false);

        RenderUtil.pushScissor(getX(), getY() + getHeight(), getWidth(), scissorHeight * expandAnimation.getAnimationFactor());

        float moduleOffset = getY() + getHeight() + scroll;

        for (ElementModule moduleElement : modules) {
            moduleElement.setX(getX());
            moduleElement.setY(moduleOffset);

            moduleElement.draw(mouseX, mouseY, mouseDelta);

            moduleOffset += moduleElement.getOffset();
        }

        RenderUtil.popScissor();
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hovered(mouseX, mouseY) && click.equals(Click.RIGHT)) {
            expandAnimation.setState(!expandAnimation.getState());
        }

        if (expandAnimation.getState() && mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() + getHeight() && mouseY <= getY() + getHeight() + scissorHeight) {
            for (ElementModule module : modules) {
                module.mouseClicked(mouseX, mouseY, click);
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (ElementModule module : modules) {
            module.keyTyped(typedChar, keyCode);
        }
    }

    private void loadModules() {
        modules.clear();
        categorySize = Wrapper.getMonsoon().getModuleManager().getModulesByCategory(category).size();
        for (Module module : Wrapper.getMonsoon().getModuleManager().getModulesByCategory(category)) {
            modules.add(new ElementModule(module, -2000, -2000, getWidth(), getHeight()));
        }
    }
}