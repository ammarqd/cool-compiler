import ast.*;
import ast.visitor.BaseVisitor;

class MyContext {
    private final ClassNode currentClass;   // Current class node

    public MyContext(ClassNode currentClass) {
        this.currentClass = currentClass;
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }
}

public class TypeCheckingVisitor extends BaseVisitor<Symbol, MyContext> {

    @Override
    public Symbol visit(ProgramNode node, MyContext context) {
        for (ClassNode classNode : node.getClasses()) {
            classNode.accept(this, new MyContext(classNode));
        }
        return TreeConstants.No_type;
    }

    @Override
    public Symbol visit(ClassNode node, MyContext context) {
        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, new MyContext(node));
        }
        return node.getName();
    }

    private Symbol checkBinaryOperation(Symbol left, Symbol right, MyContext context, String operation) {
        if (!left.equals(TreeConstants.Int) || !right.equals(TreeConstants.Int)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("non-Int arguments: " + left + " " + operation + " " + right);
            return null;
        }
        return TreeConstants.Int;
    }

    @Override
    public Symbol visit(PlusNode node, MyContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        Symbol resultType = checkBinaryOperation(t1, t2, context, "+");
        node.setType(resultType);
        return resultType;
    }

    @Override
    public Symbol visit(SubNode node, MyContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        Symbol resultType = checkBinaryOperation(t1, t2, context, "-");
        node.setType(resultType);
        return resultType;
    }

    @Override
    public Symbol visit(MulNode node, MyContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        Symbol resultType = checkBinaryOperation(t1, t2, context, "*");
        node.setType(resultType);
        return resultType;
    }

    @Override
    public Symbol visit(DivideNode node, MyContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        Symbol resultType = checkBinaryOperation(t1, t2, context, "/");
        node.setType(resultType);
        return resultType;
    }

    @Override
    public Symbol visit(IntConstNode node, MyContext context) {
        node.setType(TreeConstants.Int);
        return TreeConstants.Int;
    }

    @Override
    public Symbol visit(StringConstNode node, MyContext context) {
        node.setType(TreeConstants.Str);
        return TreeConstants.Str;
    }

    @Override
    public Symbol visit(BoolConstNode node, MyContext context) {
        node.setType(TreeConstants.Bool);
        return TreeConstants.Bool;
    }
}
