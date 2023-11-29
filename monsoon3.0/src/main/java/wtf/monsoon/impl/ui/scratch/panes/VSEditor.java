package wtf.monsoon.impl.ui.scratch.panes;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.lwjgl.input.Mouse;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.impl.ui.scratch.VSNode;
import wtf.monsoon.impl.ui.scratch.VSScreen;
import wtf.monsoon.impl.ui.scratch.rendering.VSRenderNode;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class VSEditor extends Comp {
    ArrayList<VSNode> nodes = new ArrayList<>();
    ArrayList<VSRenderNode> renders = new ArrayList<>();
    @Setter
    VSOutputPanel outputPanel;
    @Setter
    VSVariablePanel variablePanel;
    float dragX = 0, dragY = 0;
    boolean drag;
    VSRenderNode selectedRenderer = null, changeChildOrParent;

    @Getter
    private StringBuilder builder = new StringBuilder();

    public VSEditor(VSOutputPanel outputPanel, VSVariablePanel variablePanel, @NonNull float x, @NonNull float y, @NonNull float w, @NonNull float h) {
        super(x, y, w, h);
        this.outputPanel = outputPanel;
        this.variablePanel = variablePanel;
    }

    @Override
    public void render(float mx, float my) {
        if(!Mouse.isButtonDown(0)) {
            drag = false;
            selectedRenderer = null;
        }

        if(drag && selectedRenderer != null) {
            selectedRenderer.setX(mx - dragX);
            selectedRenderer.setY(my - dragY);
        }

        nodes.forEach(n -> {
            n.setOutputPanel(outputPanel);
            n.setVariablePanel(variablePanel);
        });
        renders.removeIf(r -> r.shouldDelete);
        renders.forEach(r -> r.render(mx,my));

        for (VSRenderNode render : renders) {
            if(renders.contains(render.variableEndpoint)) {} else render.variableEndpoint = null;
            if(renders.contains(render.variableParent)) {
                if (render.variableParent != null) {
                    ui.line(
                            render.getX() + render.getW(),
                            render.getY() + render.getH() / 2f, render.variableParent.getX(),
                            render.variableParent.getY() + render.variableParent.getH() / 2f,
                            1,
                            new Color(0x2b755a)
                    );
                }
            } else render.variableParent = null;
            if(renders.contains(render.parent)) {
//                if (render.parent != null)
//                    ui.line(render.getX(), render.getY() + render.getH() / 2f, render.parent.getX() + render.parent.getW(), render.parent.getY() + render.parent.getH() / 2f, 1, new Color(0x6965ba));
            } else render.parent = null;
            if(renders.contains(render.child)) {
                if (render.child != null)
                    ui.line(render.getX() + render.getW(), render.getY() + render.getH() / 2f, render.child.getX(), render.child.getY() + render.child.getH() / 2f, 1, new Color(0x6965ba));
            } else render.child = null;

            if(render.draggingChild || render.draggingParent) {
                changeChildOrParent = render;
            }

            if(changeChildOrParent != null && render != changeChildOrParent) {
                if (changeChildOrParent.draggingParent)
                    changeChildOrParent.parent = render.parent;
                if (changeChildOrParent.draggingChild)
                    changeChildOrParent.child = render.child;
            }
        }
    }

    @Override
    public void click(float mx, float my, int button) {
        renders.forEach(r -> {
            if(r.canClick) {
                if(r.hovered() && button == 0) {
                    drag = true;
                    dragX = mx - r.getX();
                    dragY = my - r.getY();
                    selectedRenderer = r;
                }
            }

            r.click(mx,my,button);
        });

        if(button == 0)
            for (VSRenderNode render : renders) {
                if(changeChildOrParent != null) {
                    if(changeChildOrParent.draggingVariable) {
                        if(render.node.getInputs().size() > 0) {
                            int yOff = 0;
                            Object newInput = render.node.getInputs().get(render.hoveredVariables.indexOf(Boolean.TRUE));

                            for (Object input : render.node.getInputs()) {
                                if(hovered(render.getX() - 4, render.getY() + 20+2+yOff - 4, 8, 8)) {
                                    System.out.println("h");
                                    changeChildOrParent.variableEndpoint = render;
                                    render.variableParent = changeChildOrParent;
                                }
                                yOff++;
                            }
                        }

                        changeChildOrParent.draggingParent = false;
                        changeChildOrParent.draggingChild = false;
                        changeChildOrParent.draggingVariable = false;
                        render.draggingParent = false;
                        render.draggingChild = false;
                        render.draggingVariable = false;
                    }

                    if(changeChildOrParent.draggingChild && render.hoveredParent) {
                        changeChildOrParent.child = render;
                        render.parent = changeChildOrParent;

                        changeChildOrParent.draggingParent = false;
                        changeChildOrParent.draggingChild = false;
                        changeChildOrParent.draggingVariable = false;
                        render.draggingParent = false;
                        render.draggingChild = false;
                        render.draggingVariable = false;
                    }

                    if(changeChildOrParent.draggingParent && render.hoveredChild) {
                        changeChildOrParent.parent = render;
                        render.child = changeChildOrParent;

                        changeChildOrParent.draggingParent = false;
                        changeChildOrParent.draggingChild = false;
                        changeChildOrParent.draggingVariable = false;
                        render.draggingParent = false;
                        render.draggingChild = false;
                        render.draggingVariable = false;
                    }
                }
            }
    }

    public void spawn(String name, VSNode nodeInstance, String[] args) {
        VSNode node = nodeInstance.copy();

        nodes.add(node);
        renders.add(new VSRenderNode(x+10,y+10,60,20+(args != null ? 10 * args.length : 0), node, this));
    }

    public void reset() {
        this.builder = new StringBuilder();
    }
}
