package wtf.monsoon.impl.ui.scratch.impl;

import wtf.monsoon.impl.ui.scratch.VSNode;

public class StartNode extends VSNode {

    public StartNode() {
        super("start", new String[]{});
    }

    @Override
    public void work(StringBuilder builder) {
        getOutputPanel().getLines().clear();

        if(getChild() != null) {
            getChild().work(builder);
        }
    }

    @Override
    public VSNode copy() {
        return new StartNode().setChild(this.getChild());
    }

}
