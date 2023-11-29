package wtf.opengui;

import lombok.Data;
import wtf.opengui.prims.EleBox;

@Data
public abstract class Element {
    String id, type;
    IRenderer renderer;
    float x=0,y=0,w,h;
    int mx,my;
    boolean click_down;
    CStyle style = new CStyle();
    public abstract void render(int mx, int my);
    public abstract void mouseClicked(int mx, int my, int btn);
    public abstract void mouseReleased(int mx, int my, int btn);

    protected boolean hovered() {
        return mx >= x && mx <= x+w && my >= y && y <= y+h;
    }

    public Element setID(String id) {
        this.id = id;
        return this;
    }

    public static Element getByType(String type) {
        switch (type) {
            case "box": return new EleBox();
            default: return null;
        }
    }
}
