package wtf.monsoon.impl.ui.menu.windows;

import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class ErrorWindow extends Window {

    private final String title;
    private final String[] content;

    public ErrorWindow(String title, String[] content, float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);

        this.title = title;
        this.content = content;

        setHeight(getHeader() + 2 + (content.length * Wrapper.getFont().getHeight()));
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);

        Wrapper.getFont().drawString(title, getX() + 4, getY() + 1, Color.WHITE, false);

        float y = getY() + getHeader() + 2;

        for (String line : content) {
            Wrapper.getFont().drawString(line, getX() + 4, y, Color.WHITE, false);

            y += Wrapper.getFont().getHeight();
        }

        setHeight(y - getY() + 4);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, Click click) {
        super.mouseClicked(mouseX, mouseY, click);
    }

}
