package wtf.monsoon.api.module;

import lombok.Getter;
import lombok.Setter;
import me.surge.animation.Animation;
import me.surge.animation.ColourAnimation;
import me.surge.animation.Easing;
import wtf.monsoon.api.util.render.ColorUtil;

import java.awt.*;

public abstract class HUDModule extends Module {

    // X position of the HUD module
    @Getter @Setter
    private float x, defaultX;

    // Y position of the HUD module
    @Getter @Setter
    private float y, defaultY;

    // Width of the HUD module
    @Getter @Setter
    private float width;

    // Height of the HUD module
    @Getter @Setter
    private float height;

    @Getter
    private final Animation hoverAnimation = new Animation(() -> 300F, false, () -> Easing.SINE_IN_OUT);

    @Getter
    private final ColourAnimation linearHoverAnimation = new ColourAnimation(ColorUtil.TRANSPARENT, Color.WHITE, () -> 300F, false, () -> Easing.LINEAR);

    public HUDModule(String name, String description) {
        super(name, description, Category.HUD);
        this.x = 30;
        this.y = 30;
        this.defaultX = 30;
        this.defaultY = 30;
    }

    public HUDModule(String name, String description, float x, float y) {
        super(name, description, Category.HUD);
        this.x = x;
        this.y = y;
        this.defaultX = x;
        this.defaultY = y;
    }

    /**
     * Renders the module
     */
    public abstract void render();

    /**
     * Where to stencil the blur
     */
    public abstract void blur();

    /**
     * Checks if the mouse is over the HUD module's boundaries
     *
     * @param mouseX The mouse's X
     * @param mouseY The mouse's Y
     * @return Whether the mouse is hovered over the HUD module
     */
    public boolean hovered(float mouseX, float mouseY) {
        return mouseX >= getX() && mouseY >= getY() && mouseX <= getX() + getWidth() && mouseY <= getY() + getHeight();
    }
}
