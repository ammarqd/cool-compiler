import ast.*;
import ast.visitor.BaseVisitor;

class MyContext {
    private final ClassNode currentClass;
    private MethodNode currentMethod;

    public MyContext(ClassNode currentClass) {
        this.currentClass = currentClass;
        this.currentMethod = null;
    }

    public void setCurrentMethod(MethodNode method) {
        this.currentMethod = method;
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public MethodNode getCurrentMethod() {
        return currentMethod;
    }
}

public class TypeCheckingVisitor extends BaseVisitor<Symbol, MyContext> {

    @Override
    public Symbol visit(ProgramNode node, MyContext context) {
        for (ClassNode classNode : node.getClasses()) {
            visit(classNode, new MyContext(classNode));
        }
        return null;
    }

    @Override
    public Symbol visit(ClassNode node, MyContext context) {
        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }
        return TreeConstants.SELF_TYPE;
    }

    @Override
    public Symbol visit(MethodNode node, MyContext context) {
        context.setCurrentMethod(node);
        Symbol bodyType = visit(node.getExpr(), context);

        if (bodyType == null) {
            return TreeConstants.SELF_TYPE;
        }

        if (bodyType != node.getReturn_type()) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Inferred return type " + bodyType + 
                            " of method " + node.getName() +
                            " does not conform to declared return type " + 
                            node.getReturn_type());
            return TreeConstants.No_type;
        }
        
        return TreeConstants.SELF_TYPE;
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
        Symbol left = node.getE1().accept(this, context);
        Symbol right = node.getE2().accept(this, context);
        Symbol resultType = checkBinaryOperation(left, right, context, "+");
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

    @Override
    public Symbol visit(ObjectNode node, MyContext context) {
        Symbol name = node.getName();
        
        // Handle 'self' as a special case
        if (name == TreeConstants.self) {
            node.setType(TreeConstants.SELF_TYPE);
            return TreeConstants.SELF_TYPE;
        }
        
        // Look up the identifier in the symbol table
        Symbol type = (Symbol) Semant.symtable.lookup(name);
        
        if (type == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Undeclared identifier " + name);
            node.setType(TreeConstants.No_type);
            return TreeConstants.No_type;
        }
        
        node.setType(type);
        return type;
    }

    @Override
    public Symbol visit(AttributeNode node, MyContext context) {
        Symbol name = node.getName();
        Symbol declaredType = node.getType_decl();
        
        // Check if attribute is named 'self'
        if (name == TreeConstants.self) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute");
            return TreeConstants.No_type;
        }
        
        Symbol initType = visit(node.getInit(), context);
        
        // NoExpressionNode visitor will return null, so we only check type conformance
        // when there is an initialization expression
        if (initType != null && initType != declaredType) {
            // Special handling for SELF_TYPE in initialization
            if (!(initType == TreeConstants.SELF_TYPE && 
                  declaredType == context.getCurrentClass().getName())) {
                Utilities.semantError(context.getCurrentClass())
                        .println("Type " + initType + 
                                " of initialization expression does not conform to " +
                                "declared type " + declaredType + 
                                " of attribute " + name);
                return TreeConstants.No_type;
            }
        }
        
        // Add attribute to symbol table
        Semant.symtable.addId(name, declaredType);
        
        return TreeConstants.SELF_TYPE;
    }
}
