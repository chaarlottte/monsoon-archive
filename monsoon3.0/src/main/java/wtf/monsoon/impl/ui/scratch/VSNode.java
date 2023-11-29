package wtf.monsoon.impl.ui.scratch;

import lombok.Data;
import wtf.monsoon.impl.ui.scratch.panes.VSOutputPanel;
import wtf.monsoon.impl.ui.scratch.panes.VSVariablePanel;

import java.util.ArrayList;
import java.util.Arrays;

@Data
public abstract class VSNode {
    VSVariablePanel variablePanel;
    VSOutputPanel outputPanel;
    String name;
    ArrayList<Object> inputs = new ArrayList<>();
    VSNode parent, child;

    public VSNode(String name, String[] inputs) {
        this.name = name;
        this.inputs.addAll(Arrays.asList(inputs));
    }

    public VSNode setChild(VSNode child) {
        this.child = child;
        return this;
    }

    public abstract void work(StringBuilder builder);
    public abstract VSNode copy();

}
