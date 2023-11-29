package wtf.monsoon.impl.ui.scratch;

import lombok.Getter;
import lombok.Setter;
import me.surge.api.Executor;
import me.surge.lexer.symbol.SymbolTable;
import me.surge.lexer.value.link.JvmLinkMethod;
import me.surge.lexer.value.method.BaseMethodValue;
import wtf.monsoon.api.ui.Comp;
import wtf.monsoon.api.ui.Screen;
import wtf.monsoon.impl.ui.scratch.impl.StartNode;
import wtf.monsoon.impl.ui.scratch.panes.VSEditor;
import wtf.monsoon.impl.ui.scratch.panes.VSOutputPanel;
import wtf.monsoon.impl.ui.scratch.panes.VSVariablePanel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class VSScreen extends Screen {
    VSEditor editor;
    VSOutputPanel outputPanel;
    VSVariablePanel variablePanel;
    ArrayList<SpawnButton> spawnButtonList = new ArrayList<>();
    @Override
    public void init() {
        super.init();
        outputPanel = new VSOutputPanel(0,dh-200,dw,200);
        variablePanel = new VSVariablePanel(dw-180, 0, 180, dh-200);
        editor = new VSEditor(outputPanel, variablePanel, 180,0,dw-180-180,dh-200);

        spawnButtonList.add(new SpawnButton(20, new StartNode(), "Start Node"));

        Executor executor = new Executor();

        float y = 36;
        for (SymbolTable.Symbol symbol : executor.getGlobalSymbolTable().get("std").getSymbols().getSymbols()) {
            if (symbol.getValue() instanceof JvmLinkMethod) {
                String[] arguments = null;

                // TODO: Correct argument values!
                if (symbol.getValue() instanceof JvmLinkMethod) {
                    arguments = new String[((JvmLinkMethod) symbol.getValue()).getArgumentNames().size()];

                    int index = 0;
                    for (BaseMethodValue.Argument argumentName : ((JvmLinkMethod) symbol.getValue()).getArgumentNames()) {
                        arguments[index] = argumentName.getName();
                        index++;
                    }
                }

                Node node = new Node(symbol.getIdentifier(), arguments);
                node.setPrefix("std");
                node.setSymbol(symbol);

                spawnButtonList.add(new SpawnButton(y, node, symbol.getIdentifier()).arguments(arguments));
                y += 16;
            }
        }
    }

    @Override
    public void render(float mx, float my) {
        ui.rect(0,0,dw,dh, new Color(0x1E1F22));
        ui.rect(0,0,180,dh, new Color(0x2B2D30));
        ui.rect(0,0,180,18, new Color(0x313336));

        ui.text("Toolbox", "product_sans", 16, 3, 3, new Color(0xcac6c6));
        ui.rect(0,18,180,1, new Color(0x141517));
        ui.rect(180,0,1,dh, new Color(0x141517));
        ui.rect(0,dh-200,dw,200, new Color(0x2B2D30));

        editor.render(mx,my);
        variablePanel.render(mx,my);
        outputPanel.render(mx,my);
        spawnButtonList.forEach(s -> s.render(mx,my));
    }

    @Override
    public void click(float mx, float my, int button) {
        editor.click(mx,my,button);
        outputPanel.click(mx,my,button);
        variablePanel.click(mx,my,button);
        spawnButtonList.forEach(s -> s.click(mx,my,button));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        variablePanel.onKey(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    class SpawnButton extends Comp {
        String[] args;
        VSNode node;
        String text;

        public SpawnButton(float y, VSNode node, String text) {
            super(0, y, 180, 16);
            this.node = node;
            this.text = text;
        }

        @Override
        public void render(float mx, float my) {
            if(hovered()) {
                ui.rect(x,y,w,h, new Color(0x43454a));
            }

            ui.text(text, "product_sans", 14, x+5, y+2, new Color(0xcac6c6));
        }

        @Override
        public void click(float mx, float my, int button) {
            if(hovered() && button == 0) {
                editor.spawn(text, node, args);
            }
        }

        public SpawnButton arguments(String[] args) {
            this.args = args;
            return this;
        }
    }

    public static class Node extends VSNode {

        @Getter @Setter
        private SymbolTable.Symbol symbol;

        @Getter @Setter
        private String prefix;

        @Getter
        private final String[] inputArray;

        public Node(String name, String[] inputs) {
            super(name, inputs);

            this.inputArray = inputs;
        }

        @Override
        public void work(StringBuilder builder) {
            builder.append("\n");

            if (this.getPrefix() != null) {
                builder.append(this.getPrefix())
                        .append("::");
            }

            builder.append(name)
                    .append("(");

            int index = 0;
            for (BaseMethodValue.Argument ignored : ((JvmLinkMethod) symbol.getValue()).getArgumentNames()) {
                builder.append(getInputs().get(index));
                index++;
            }

            builder.append(")");

            if (getChild() != null) {
                getChild().work(builder);
            }
        }

        public Node copy() {
            Node node = new Node(this.name, this.getInputArray());
            node.setPrefix(this.getPrefix());
            node.setSymbol(this.getSymbol());
            return node;
        }
    }
}
