package wtf.opengui.prims;


import wtf.opengui.Element;

public class EleBox extends Element {
    public EleBox() {
        setType("box");
    }
    @Override
    public void render(int mx, int my) {
        getRenderer().fill(this);
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) {

    }

    @Override
    public void mouseReleased(int mx, int my, int btn) {

    }
}
