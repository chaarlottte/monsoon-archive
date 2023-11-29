package wtf.monsoon.impl.ui.primitive;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import wtf.monsoon.impl.ui.primitive.Click;

/**
 * @author Surge
 * @since 30/07/2022
 */

@Data
public abstract class Drawable {

    private float x, y, width, height;

    protected Minecraft mc = Minecraft.getMinecraft();
    protected FontRenderer fr = mc.fontRendererObj;

    public Drawable(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(float mouseX, float mouseY, int mouseDelta);

    public abstract boolean mouseClicked(float mouseX, float mouseY, Click click);

    public abstract void mouseReleased(float mouseX, float mouseY, Click click);

    public abstract void keyTyped(char typedChar, int keyCode);

    public float getOffset() {
        return 0f;
    }

    public boolean hovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
