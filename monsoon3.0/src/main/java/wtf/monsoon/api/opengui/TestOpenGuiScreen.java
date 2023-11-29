package wtf.monsoon.api.opengui;

import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.opengui.CStyle;
import wtf.opengui.Screen;

import java.awt.*;

public class TestOpenGuiScreen extends ScalableScreen implements Screen {

    @Override
    public void build() {
        ele("box", "#red");
        ele("box", "#green");
        ele("box", "#blue");
        ele("box", "#yellow");

        style("box", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.RED;
            s.radius = 1;
            s.height = 80;
            s.width = 160;
            return s;
        });

        style("#red", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.RED;
            return s;
        });

        style("#green", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.GREEN;
            return s;
        });

        style("#blue", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.BLUE;
            return s;
        });

        style("#yellow", () -> {
            CStyle s = new CStyle();
            s.fill_color = Color.YELLOW;
            return s;
        });
        Screen.super.build();
    }

    @Override
    public void init() {

    }

    @Override
    public void render(float mouseX, float mouseY) {

    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {

    }
}
