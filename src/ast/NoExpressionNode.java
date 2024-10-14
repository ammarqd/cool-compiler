package ast;

import ast.visitor.TreeVisitor;

public class NoExpressionNode extends ExpressionNode {

    public NoExpressionNode(int lineNumber) {
        super(lineNumber);
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
