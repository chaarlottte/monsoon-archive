package wtf.opengui;

import lombok.Setter;

public class GuiGL {
    int dWidth = 0, dHeight = 0, mx, my;

    @Setter
    IRenderer renderer;

    Screen s = null;

    public void resize(int dWidth, int dHeight) {
        this.dWidth = dWidth;
        this.dHeight = dHeight;
    }

    //IMPORTANT:
    //  Make sure that the target object (c) implements Screen!!!
    public void render(Object c) {
        s = ((Screen)c);

        s.elements.forEach(e -> {
            e.setRenderer(renderer);
            e.render(mx,my);
        });
    }

    public void onOpen() {
        if(s != null) {
            s.elements.clear();
            // s.build(dWidth, dHeight);
            s.build();
            System.out.println(s.elements.size());
        }
    }

    public void onClose() {

    }

    public void mouseMove(int mouseX, int mouseY) {
        this.mx = mouseX;
        this.my = mouseY;
    }

    public void mouseClick(int mouseX, int mouseY) {

    }

    public void mouseRelease(int mouseX, int mouseY) {

    }

    public void mouseScroll(float amount) {

    }

    public void keyPress(int keycode) {

    }
}
