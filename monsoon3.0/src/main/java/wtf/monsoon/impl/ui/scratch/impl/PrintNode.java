package wtf.monsoon.impl.ui.scratch.impl;

import wtf.monsoon.impl.ui.scratch.VSNode;

public class PrintNode extends VSNode {

    public PrintNode(String input) {
        super("print", new String[]{ input });
    }

    @Override
    public void work(StringBuilder builder) {
        if (getChild() != null) {
            getChild().work(builder);
        }

        builder.append("\n")
                .append("std::println(")
                .append(getInputs().get(0))
                .append(")");
    }

    @Override
    public VSNode copy() {
        return null;
    }

}
