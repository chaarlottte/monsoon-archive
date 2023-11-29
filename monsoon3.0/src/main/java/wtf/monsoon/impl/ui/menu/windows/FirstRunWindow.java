package wtf.monsoon.impl.ui.menu.windows;

import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This window should only be displayed on the first time the client is run
 *
 * @author Surge
 * @since 21/08/2022
 */
public class FirstRunWindow extends Window {

    private final ArrayList<String> content = new ArrayList<>(Arrays.asList(
            "Hey there!",
            "Thanks for choosing to buy Monsoon.",
            "",
            "As it looks like you are new to using Monsoon,",
            "here's how to get started!",
            "",
            "The GUI can be opened by pressing [RSHIFT]",
            "whilst ingame",
            "",
            "The modules can be expanded to show their",
            "settings by right clicking on them",
            "",
            "Some of these settings have subsettings!",
            "Right click to see if they have any!",
            "",
            "We hope you enjoy using Monsoon!"
    ));

    public FirstRunWindow(float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);

        Wrapper.getFont().drawString("First time using Monsoon?", getX() + 4, getY() + 1, Color.WHITE, false);

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
