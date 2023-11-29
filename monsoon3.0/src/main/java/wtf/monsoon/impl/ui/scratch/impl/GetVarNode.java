package wtf.monsoon.impl.ui.scratch.impl;

import wtf.monsoon.impl.ui.scratch.VSNode;

public class GetVarNode extends VSNode {

    public GetVarNode(String varName) {
        super("get var", new String[]{ varName });
    }

    @Override
    public void work(StringBuilder builder) {

    }

    @Override
    public VSNode copy() {
        return null;
    }

}
