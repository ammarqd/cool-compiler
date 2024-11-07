import ast.*;
import ast.visitor.BaseVisitor;

class MyContext { }

public class TypeCheckingVisitor extends BaseVisitor<Object, MyContext> {

    @Override
    public Symbol visit(IntConstNode node, MyContext data) {
        node.setType(TreeConstants.Int);
        return TreeConstants.Int;
    }

}
