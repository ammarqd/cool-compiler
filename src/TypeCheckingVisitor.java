import ast.*;
import ast.visitor.BaseVisitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TypeContext {
    private final ClassNode currentClass;
    private final Map<Symbol, MethodNode> methodMap;
    private final Map<Symbol, AttributeNode> attributeMap;

    public TypeContext(ClassNode currentClass) {
        this.currentClass = currentClass;
        this.attributeMap = new HashMap<>();
        this.methodMap = new HashMap<>();
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public MethodNode getMethod(Symbol name) {
        return methodMap.get(name);
    }

    public AttributeNode getAttribute(Symbol name) {
        return attributeMap.get(name);
    }

    public void addMethod(Symbol name, MethodNode method) {
        methodMap.put(name, method);
    }

    public void addAttribute(Symbol name, AttributeNode attr) {
        attributeMap.put(name, attr);
    }

    public Map<Symbol, AttributeNode> getAttributeMap() {
        return attributeMap;
    }

    public Map<Symbol, MethodNode> getMethodMap() {
        return methodMap;
    }
}

public class TypeCheckingVisitor extends BaseVisitor<Symbol, TypeContext> {

    @Override
    public Symbol visit(ProgramNode node, TypeContext context) {

        ArrayList<ClassNode> objectClasses = Semant.classTable.getInheritanceMap().get(TreeConstants.Object_);
        ClassNode objectNode = Semant.classTable.getClassMap().get(TreeConstants.Object_);

        // Skip iterating over basic classes (IO, String, Int, Bool), as we'll handle them separately
        for (int i = objectClasses.size() - 1; i >= 4; i--) {
            TypeContext rootContext = new TypeContext(objectClasses.get(i));

            // Add the default Object methods to methodMap
            for (FeatureNode feature : objectNode.getFeatures()) {
                rootContext.getMethodMap().put(((MethodNode)feature).getName(), (MethodNode)feature);
            }

            visitClassHierarchy(objectClasses.get(i), rootContext);
        }

        // Handle IONode separately at the end
        ArrayList<ClassNode> IO_classes = Semant.classTable.getInheritanceMap().get(TreeConstants.IO);

        if (!IO_classes.isEmpty()) {
            ClassNode IONode = Semant.classTable.getClassMap().get(TreeConstants.IO);
            TypeContext IOContext = new TypeContext(IONode);

            for (FeatureNode feature : objectNode.getFeatures()) {
                IOContext.getMethodMap().put(((MethodNode)feature).getName(), (MethodNode)feature);
            }

            for (FeatureNode feature : IONode.getFeatures()) {
                IOContext.getMethodMap().put(((MethodNode)feature).getName(), (MethodNode)feature);
            }

            for (ClassNode child : IO_classes) {
                visitClassHierarchy(child, IOContext);
            }
        }

        return null;
    }


    private void visitClassHierarchy(ClassNode classNode, TypeContext parentContext) {
        TypeContext context = new TypeContext(classNode);

        if (parentContext != null) {
            context.getMethodMap().putAll(parentContext.getMethodMap());
            context.getAttributeMap().putAll(parentContext.getAttributeMap());
        }

        for (FeatureNode feature : classNode.getFeatures()) {
            if (feature instanceof MethodNode method) {
                if (!context.getMethodMap().containsKey(method.getName())) {
                    context.addMethod(method.getName(), method);
                }
            } else if (feature instanceof AttributeNode attribute) {
                if (!context.getAttributeMap().containsKey(attribute.getName())) {
                    context.addAttribute(attribute.getName(), attribute);
                }
            }
        }

        visit(classNode, context); // Visit current class, utilising the visitor pattern

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {
            visitClassHierarchy(child, context);
        }
    }

    @Override
    public Symbol visit(ClassNode node, TypeContext context) {
        Semant.symTable.enterScope();

        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }

        Semant.symTable.exitScope();
        return TreeConstants.SELF_TYPE;
    }

    @Override
    public Symbol visit(AttributeNode node, TypeContext context) {
        Symbol idType = node.getType_decl();
        if (!(node.getInit() instanceof NoExpressionNode)) {
            Symbol exprType = visit(node.getInit(), context);
            if (!Semant.classTable.isSubType(exprType, idType)) {
                Utilities.semantError(context.getCurrentClass()).println("Inferred type " +
                        exprType + " of initialization of " + node.getName()
                        + " does not conform to identifier's declared type " + idType + ".");
                return TreeConstants.Object_;
            }
        }
        return node.getType_decl();
    }

    @Override
    public Symbol visit(MethodNode node, TypeContext context) {
        Semant.symTable.enterScope();

        for (FormalNode f : node.getFormals()) {
            Semant.symTable.addId(f.getName(), f.getType_decl());
        }

        Symbol bodyType = visit(node.getExpr(), context);

        if (!Semant.classTable.isSubType(bodyType, node.getReturn_type())) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Inferred return type " + bodyType + " of method " + node.getName()
                            + " does not conform to declared return type " + node.getReturn_type());
            return TreeConstants.Object_;
        }

        Semant.symTable.exitScope();
        return bodyType;
    }

    @Override
    public Symbol visit(DispatchNode node, TypeContext context) {
        Symbol exprType = visit(node.getExpr(), context);

        MethodNode method = context.getMethod(node.getName());

        List<FormalNode> formals = method.getFormals();
        List<ExpressionNode> actuals = node.getActuals();
        if (formals.size() != actuals.size()) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Method " + node.getName() + " called with wrong number of arguments. " +
                            "Expected: " + formals.size() + ", got: " + actuals.size());
            node.setType(TreeConstants.Object_);
        }

        for (int i = 0; i < formals.size(); i++) {
            Symbol formalType = formals.get(i).getType_decl();
            Symbol actualType = visit(actuals.get(i), context);

            if (!Semant.classTable.isSubType(actualType, formalType)) {
                Utilities.semantError(context.getCurrentClass())
                        .println("In call to method " + node.getName() +
                                ", argument #" + (i + 1) + " type " + actualType +
                                " does not conform to formal parameter type " + formalType);
                node.setType(TreeConstants.Object_);
            }
        }

        if (exprType.equals(TreeConstants.self)) {
            node.setType(TreeConstants.SELF_TYPE);
        } else {
            node.setType(method.getReturn_type());
        }

        return node.getType();
    }

    public Symbol visit(LetNode node, TypeContext context) {
        Semant.symTable.enterScope();

        Symbol idType = node.getType_decl();
        Symbol initType = visit(node.getInit(), context);

        Semant.symTable.addId(node.getIdentifier(), idType);

        Symbol bodyType = visit(node.getBody(), context);

        if (!Semant.classTable.isSubType(initType, idType)) {
            Utilities.semantError(context.getCurrentClass()).println("Inferred type "
                    + initType + " of initialization of " + node.getIdentifier()
                    + " does not conform to identifier's declared type " + idType);
            node.setType(bodyType);
            return node.getType();
        }

        Semant.symTable.exitScope();
        return node.getType();
    }

    public Symbol visit(AssignNode node, TypeContext context) {
        Symbol idType = Semant.symTable.lookup(node.getName());
        if (idType == null) {
            idType = context.getAttribute(node.getName()).getType_decl();
        }
        Symbol exprType = visit(node.getExpr(), context);
        if (!Semant.classTable.isSubType(exprType, idType)) {
            Utilities.semantError(context.getCurrentClass()).println("Type " + exprType +
                    " of assigned expression does not conform to declared type " +
                    idType + " of identifier b.");
            return TreeConstants.Object_;
        }
        return exprType;
    }

    public Symbol visit(NewNode node, TypeContext context) {
        node.setType(node.getType_name());
        return node.getType();
    }

    public Symbol visit(EqNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!t1.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("Illegal comparison with a basic type.");
            node.setType(TreeConstants.Object_);
            return node.getType();
        } else {
            node.setType(TreeConstants.Bool);
        }
        return node.getType();
    }

    public Symbol visit(LEqNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!TreeConstants.Int.equals(t1) || !TreeConstants.Int.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " <= " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    public Symbol visit(LTNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!TreeConstants.Int.equals(t1) || !TreeConstants.Int.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " < " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(PlusNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!TreeConstants.Int.equals(t1) || !TreeConstants.Int.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " + " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(SubNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!TreeConstants.Int.equals(t1) || !TreeConstants.Int.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " - " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(MulNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!TreeConstants.Int.equals(t1) || !TreeConstants.Int.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " * " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(DivideNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (!TreeConstants.Int.equals(t1) || !TreeConstants.Int.equals(t2)) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " / " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(ObjectNode node, TypeContext context) {
        if (node.getName().equals(TreeConstants.self)) {
            node.setType(TreeConstants.SELF_TYPE);
        } else {
            Symbol idType = Semant.symTable.lookup(node.getName());
            if (idType == null) {
                idType = context.getAttribute(node.getName()).getType_decl();
            }
            node.setType(idType);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(IntConstNode node, TypeContext context) {
        node.setType(TreeConstants.Int);
        return TreeConstants.Int;
    }

    @Override
    public Symbol visit(StringConstNode node, TypeContext context) {
        node.setType(TreeConstants.Str);
        return TreeConstants.Str;
    }

    @Override
    public Symbol visit(BoolConstNode node, TypeContext context) {
        node.setType(TreeConstants.Bool);
        return TreeConstants.Bool;
    }

}
