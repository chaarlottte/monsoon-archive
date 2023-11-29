package wtf.monsoon.impl.ui.scratch.rendering;

import lombok.NonNull;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.util.font.FontUtil;
import wtf.monsoon.impl.ui.scratch.VSNode;
import wtf.monsoon.impl.ui.scratch.panes.VSEditor;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class VSRenderNode extends Comp {
    public VSRenderNode parent, child, variableEndpoint, variableParent;
    public VSNode node;
    public boolean shouldDelete, canClick = true, draggingChild, draggingParent, hoveredChild, hoveredParent, draggingVariable, hoveredVariable;
    public ArrayList<Boolean> hoveredVariables = new ArrayList<>();
    boolean showContextMenu;
    float cx = 0,cy = 0;
    HashMap<String, Runnable> actions = new HashMap();

    public VSRenderNode(@NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h, VSNode node, VSEditor editor) {
        super(x, y, w, h);
        this.node = node;
        actions.put("Delete Node", () -> shouldDelete =  true);

        if (node.getName().equalsIgnoreCase("start")) {
            actions.put("Run Node", () -> {
                editor.reset();

                StringBuilder build = new StringBuilder();
                node.work(build);
                editor.getBuilder().append(build);

                node.getOutputPanel().print(editor.getBuilder());
            });
        }

        for (Object input : node.getInputs()) {
            hoveredVariables.add(false);
        }
    }

    @Override
    public void render(float mx, float my) {
        hoveredChild = hovered(x+w-4,y+h/2f-4,8,8);
        hoveredVariable = hovered(x+w-4,y+h/2f-4,8,8);
        hoveredParent = hovered(x-4,y+h/2f-4,8,8);

        ui.round(x,y,w,h, 2,new Color(0xb99235));
        ui.round(x+1,y+1,w-2,h-2, 2,new Color(0x2A2A2A));
        ui.text(node.getName(), "product_sans", 14, x+4, y+3, Color.WHITE);


        ui.text(FontUtil.UNICODES_UI.NO, "entypo", 14, x+w-12+2, y+1, new Color(0xFD526F));

        if(!node.getName().equalsIgnoreCase("get var")) {
            if(hovered(x+w-12,y+2,10,10) || hoveredChild || hoveredParent) {
                canClick = false;
            } else {
                canClick = true;
            }

            int yOff = 0;
            for (Object input : node.getInputs()) {
                hoveredVariables.set(yOff, hovered(x - 4, y + 20+2+yOff - 4, 8, 8));
                ui.round(x - 4, y + 20+2+yOff - 4, 8, 8, 4, new Color(0x2b755a));
                ui.round(x - 3, y + 20+2+yOff - 3, 6, 6, 3, new Color(0x2A2A2A));
                yOff++;
            }

            //child
            ui.round(x + w - 4, y + h / 2f - 4, 8, 8, 4, new Color(0x6965ba));
            ui.round(x + w - 3, y + h / 2f - 3, 6, 6, 3, new Color(0x2A2A2A));

            //parent
            if (!node.getName().equalsIgnoreCase("start")) {
                ui.round(x - 4, y + h / 2f - 4, 8, 8, 4, new Color(0x6965ba));
                ui.round(x - 3, y + h / 2f - 3, 6, 6, 3, new Color(0x2A2A2A));
            }
            if (draggingChild) {
                ui.line(x + w, y + h / 2f, mx, my, 1, new Color(0x6965ba));
                ui.round(x + w - 1, y + h / 2f - 1, 2, 2, 1, new Color(0x6965ba));
            }
            if (draggingParent) {
                ui.line(x, y + h / 2f, mx, my, 1, new Color(0x6965ba));
                ui.round(x - 1, y + h / 2f - 1, 2, 2, 1, new Color(0x6965ba));
            }


            if (child != null) {
                if (child.parent != this) child = null;
                ui.round(x + w - 1, y + h / 2f - 1, 2, 2, 1, new Color(0x6965ba));
                if (child != null)
                    node.setChild(child.node);
            } else {
                node.setChild(null);
            }

            if (parent != null) {
                if (parent.child != this) parent = null;
                ui.round(x - 1, y + h / 2f - 1, 2, 2, 1, new Color(0x6965ba));
                if (parent != null)
                    node.setParent(parent.node);
            } else {
                node.setParent(null);
            }
        } else {
            if(hovered(x+w-12,y+2,10,10) || hoveredVariable) {
                canClick = false;
            } else {
                canClick = true;
            }

            setH(40);

            ui.round(x + w - 4, y + h / 2f - 4, 8, 8, 4, new Color(0x2b755a));
            ui.round(x + w - 3, y + h / 2f - 3, 6, 6, 3, new Color(0x2A2A2A));

            if(draggingVariable) {
                ui.line(x+w,y+h/2f,mx,my,1,new Color(0x2b755a));
            }

            ui.round(x+4, y+20, w-8, 16, 2,new Color(0xa0a0a0));
            ui.round(x+4+1, y+20+1, w-8-2, 16-2, 2,new Color(0x2A2A2A));
        }

        if(showContextMenu) {
            ui.rect(x,y,w,h, new Color(0x7B000000, true));

            int yd = 0;
            for (Object s : actions.keySet().toArray()) {
                ui.rect(cx,cy+yd*16, 80, 16, new Color(0x2A2A2A));
                if(hovered(cx,cy+yd*16, 80, 16))
                    ui.rect(cx,cy+yd*16, 80, 16, new Color(0x343434));
                ui.text(s.toString(),"product_sans",11,cx+3,cy+3+yd*16, Color.WHITE);
                yd++;
            }
        }
    }

    @Override
    public void click(float mx, float my, int button) {
        if(hovered(x+w-13,y+4,10,10) && button == 0)
            shouldDelete = true;
        if(!hoveredChild && !hoveredParent && !draggingChild && !draggingParent && hovered() && button == 1) {
            showContextMenu = true;
            cx = mx+2; cy = my+2;
        }


        if(!showContextMenu) {
            if(!node.getName().equalsIgnoreCase("get var")) {
                if (hoveredChild) {
                    if (button == 1)
                        child = null;
                    draggingChild = true;
                }

                if (hoveredParent) {
                    if (button == 1)
                        parent = null;
                    draggingParent = true;
                }
            } else {
                if(hoveredVariable) {
                    draggingVariable = true;
                }
            }
            if (button == 1) {
                draggingChild = false;
                draggingParent = false;
                draggingVariable = false;
            }
        }


        if(showContextMenu) {
            int yd = 0;
            for (Runnable s : actions.values()) {
                if(hovered(cx,cy+yd*16, 80, 16)) {
                    s.run();
                    showContextMenu = false;
                }
                yd++;
            }

            if(!hovered())
                showContextMenu = false;
        }
    }
}
