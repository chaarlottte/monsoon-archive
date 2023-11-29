package wtf.monsoon.impl.ui.panel.elements.config;

import lombok.Getter;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.ui.primitive.Click;
import wtf.monsoon.impl.ui.primitive.Drawable;
import wtf.monsoon.impl.ui.panel.elements.config.ConfigPanel.Button;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigPane extends Drawable {

    @Getter
    private final File config;
    private boolean deleted = false;

    private final ConfigPanel parent;

    private final ArrayList<Button> buttons = new ArrayList<>();

    private final Animation hover = new Animation(() -> 200F, false, () -> Easing.LINEAR);

    @Getter
    private final Animation deleteScaleDown = new Animation(() -> 200F, false, () -> Easing.CUBIC_IN_OUT);

    public ConfigPane(ConfigPanel parent, File config, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.parent = parent;
        this.config = config;

        buttons.add(new Button(
                "Load",
                () -> {
                    String configName = config.getName().split("\\.")[0];

                    if (Wrapper.getMonsoon().getConfigSystem().configExists(configName)) {
                        Wrapper.getMonsoon().getConfigSystem().load(configName, true);
                    }
                },
                getX(),
                getY(),
                getWidth() / 2f - 5,
                8,
                false
        ));

        buttons.add(new Button(
                "Delete",
                () -> {
                    String configName = config.getName().split("\\.")[0];

                    if (Wrapper.getMonsoon().getConfigSystem().configExists(configName) && !configName.equals("current")) {
                        deleted = new File("monsoon" + File.separator + "configs" + File.separator + configName + ".json").delete();
                    }
                },
                getX(),
                getY(),
                getWidth() / 2f - 5,
                8,
                config.getName().split("\\.")[0].equals("current")
        ));

        deleteScaleDown.setState(true);
    }

    @Override
    public void draw(float mouseX, float mouseY, int mouseDelta) {
        if (deleted && !deleteScaleDown.getState() && deleteScaleDown.getAnimationFactor() == 0.0) {
            parent.getPanes().remove(this);
        }

        hover.setState(hovered(mouseX, mouseY));
        deleteScaleDown.setState(!deleted);
        String[] split = config.getName().split("\\.");
        String configName = split[0];

        Color c1 = ColorUtil.getClientAccentTheme()[0];
        Color c2 = ColorUtil.getClientAccentTheme()[1];
        Color c3 = ColorUtil.getClientAccentTheme().length > 2 ? ColorUtil.getClientAccentTheme()[2] : ColorUtil.getClientAccentTheme()[0];
        Color c4 = ColorUtil.getClientAccentTheme().length > 3 ? ColorUtil.getClientAccentTheme()[3] : ColorUtil.getClientAccentTheme()[1];

        Color cc1, cc2, cc3, cc4;
        if (ColorUtil.getClientAccentTheme().length > 3) {
            cc1 = c1;
            cc2 = c2;
            cc3 = c3;
            cc4 = c4;
        } else {
            cc1 = ColorUtil.fadeBetween(10, 270, c1, c2);
            cc2 = ColorUtil.fadeBetween(10, 0, c1, c2);
            cc3 = ColorUtil.fadeBetween(10, 180, c1, c2);
            cc4 = ColorUtil.fadeBetween(10, 90, c1, c2);
        }

        RenderUtil.scaleXY(getX() + getWidth() / 2f, getY() + getHeight() / 2f, deleteScaleDown, () -> {
            RoundedUtils.round(getX(), getY(), getWidth(), getHeight(), 4, new Color(0x191919));
            RoundedUtils.outline(getX(), getY(), getWidth(), getHeight(), 4, 0.5f, 1f, cc1, cc2, cc3, cc4);

            Wrapper.getFontUtil().productSansBold.drawCenteredString(configName, getX() + getWidth() / 2f, getY() + 2f, new Color(0xff8f8f8f), false);

            float x = getX() + ((getWidth() / 2) - ((buttons.size() / 2f) * buttons.get(0).getWidth()));

            for (Button button : buttons) {
                button.setX(x);
                button.setY(getY() + getHeight() - 12);

                button.draw(mouseX, mouseY, mouseDelta);

                x += button.getWidth();
            }
        });
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, Click click) {
        if (!deleted) {
            for (Button button : buttons) {
                button.mouseClicked(mouseX, mouseY, click);
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, Click click) {}

    @Override
    public void keyTyped(char typedChar, int keyCode) {}


}
