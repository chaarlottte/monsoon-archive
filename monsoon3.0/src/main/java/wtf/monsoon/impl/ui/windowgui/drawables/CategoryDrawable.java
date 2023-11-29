package wtf.monsoon.impl.ui.windowgui.drawables;

import lombok.Getter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.windowgui.WindowGUI;
import wtf.monsoon.impl.ui.windowgui.window.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Surge
 * @since 28/08/2022
 */
public class CategoryDrawable extends Drawable {

    @Getter
    private final Window parent;

    @Getter
    private final Category category;

    @Getter
    private float scroll;

    private final Animation hover = new Animation(() -> 200f, false, () -> Easing.LINEAR);
    private final Animation selected = new Animation(() -> 200f, false, () -> Easing.LINEAR);

    private final List<ModuleDrawable> moduleDrawables = new ArrayList<>();

    public CategoryDrawable(Window parent, Category category, float x, float y, float width, float height) {
        super(x, y, width, height);

        this.parent = parent;
        this.category = category;

        Wrapper.getMonsoon().getModuleManager().getModulesByCategory(category).forEach(module -> {
            moduleDrawables.add(new ModuleDrawable(this, module, getX(), getY(), ((getParent().getWidth() - 90) / 3) - 4, 30));
        });
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        hover.setState(MathUtils.within(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY));
        selected.setState(parent.getSelected() == this);

        RenderUtil.rect(getX(), getY(), getWidth(), getHeight() + 1.5f, selected.getState() ? ColorUtil.interpolate(ColorUtil.interpolate(WindowGUI.INTERACTABLE, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)), selected.getAnimationFactor()), ColorUtil.interpolate(WindowGUI.HOVER, ColorUtil.fadeBetween(10, 270, new Color(0, 238, 255, 255), new Color(135, 56, 232, 255)).darker(), selected.getAnimationFactor()), hover.getAnimationFactor()) : WindowGUI.INTERACTABLE);

        Wrapper.getFontUtil().greycliff26.drawString(StringUtil.formatEnum(category), getX() + 5, getY() + 2, Color.WHITE, false);

        if (selected.getState()) {
            float leftTotal = 0f;
            for (ModuleDrawable moduleDrawable : moduleDrawables.stream().filter(moduleDrawable -> moduleDrawable.getColumn() != null && moduleDrawable.getColumn().equals(Column.LEFT)).collect(Collectors.toList())) {
                leftTotal += moduleDrawable.getHeight() + moduleDrawable.getOffset() + 2;
            }

            float middleTotal = 0f;
            for (ModuleDrawable moduleDrawable : moduleDrawables.stream().filter(moduleDrawable -> moduleDrawable.getColumn() != null && moduleDrawable.getColumn().equals(Column.MIDDLE)).collect(Collectors.toList())) {
                middleTotal += moduleDrawable.getHeight() + moduleDrawable.getOffset() + 2;
            }

            float rightTotal = 0f;
            for (ModuleDrawable moduleDrawable : moduleDrawables.stream().filter(moduleDrawable -> moduleDrawable.getColumn() != null && moduleDrawable.getColumn().equals(Column.RIGHT)).collect(Collectors.toList())) {
                rightTotal += moduleDrawable.getHeight() + moduleDrawable.getOffset() + 2;
            }

            float longest = leftTotal;

            if (middleTotal > longest) {
                longest = middleTotal;
            }

            if (rightTotal > longest) {
                longest = rightTotal;
            }

            scroll = (float) (scroll + mouseDelta * 0.5);
            scroll = MathHelper.clamp_float(scroll, -Math.max(0, (longest - (getParent().getHeight() - 87))), 0);

            float x = getParent().getX() + 90;

            // align modules
            Column column = Column.LEFT;

            float leftY = getParent().getY() + 29 + scroll;
            float middleY = getParent().getY() + 29 + scroll;
            float rightY = getParent().getY() + 29 + scroll;

            RenderUtil.pushScissor(x, getParent().getY() + 26, getParent().getWidth() - 90, getParent().getHeight() - 81);

            for (ModuleDrawable moduleDrawable : moduleDrawables) {
                moduleDrawable.setColumn(column);

                switch (column) {
                    case LEFT:
                        moduleDrawable.setX(x);
                        x += (getParent().getWidth() - 98) / 3;

                        moduleDrawable.setY(leftY);
                        leftY += moduleDrawable.getHeight() + moduleDrawable.getOffset() + 2;

                        column = Column.MIDDLE;
                        break;

                    case MIDDLE:
                        moduleDrawable.setX(x);
                        x += (getParent().getWidth() - 98) / 3;

                        moduleDrawable.setY(middleY);
                        middleY += moduleDrawable.getHeight() + moduleDrawable.getOffset() + 2;

                        column = Column.RIGHT;
                        break;

                    case RIGHT:
                        moduleDrawable.setX(x);
                        x = getParent().getX() + 90;

                        moduleDrawable.setY(rightY);
                        rightY += moduleDrawable.getHeight() + moduleDrawable.getOffset() + 2;

                        column = Column.LEFT;
                        break;
                }

                moduleDrawable.draw(mouseX, mouseY, mouseDelta);
            }

            RenderUtil.popScissor();
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (hover.getState()) {
            parent.setSelected(this);
        }

        if (MathUtils.within(getParent().getX() + 85, getParent().getY() + 25, getParent().getWidth() - 90, getParent().getHeight() - 80, mouseX, mouseY)) {
            moduleDrawables.forEach(moduleDrawable -> moduleDrawable.mouseClicked(mouseX, mouseY, click));
        }

        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {
        moduleDrawables.forEach(moduleDrawable -> moduleDrawable.mouseReleased(mouseX, mouseY, click));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        moduleDrawables.forEach(moduleDrawable -> moduleDrawable.keyTyped(typedChar, keyCode));
    }

    public enum Column {
        LEFT,
        MIDDLE,
        RIGHT
    }

}
