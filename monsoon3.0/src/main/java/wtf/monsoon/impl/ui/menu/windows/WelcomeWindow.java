package wtf.monsoon.impl.ui.menu.windows;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.ui.primitive.Click;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Surge
 * @since 21/08/2022
 */
public class WelcomeWindow extends Window {

    private final ArrayList<String> content = new ArrayList<>(Arrays.asList(
            "Monsoon is a 1.8 Minecraft client intended",
            "for servers such as Hypixel and Funcraft.",
            "It features many features such as",
            "simple but good looking GUIs, bypassing",
            "modules, and much, much more!",
            "",
            "- Changelog -",
            ""
    ));

    public WelcomeWindow(float x, float y, float width, float height, float header) {
        super(x, y, width, height, header);

        setHeight(getHeader() + 2 + (content.size() * Wrapper.getFont().getHeight()));

        try {
            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("monsoon/" + "changelog.txt")).getInputStream();
            String streamContent = IOUtils.toString(stream);

            Collections.addAll(content, streamContent.split(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);

        Wrapper.getFont().drawString("Welcome to Monsoon " + Wrapper.getMonsoon().getVersion() + "!", getX() + 4, getY() + 1, Color.WHITE, false);

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
