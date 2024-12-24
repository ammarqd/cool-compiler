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
        Map<Symbol, MethodNode> defaultObjectMethods = Semant.classTable.getClassMethodsMap().get(TreeConstants.Object_);

        for (int i = objectClasses.size() - 1; i >= 0; i--) {
            ClassNode classNode = objectClasses.get(i);

            Map<Symbol, AttributeNode> currentClassAttributes = Semant.classTable.getClassAttributesMap().get(classNode.getName());
            Map<Symbol, MethodNode> currentClassMethods = Semant.classTable.getClassMethodsMap().get(classNode.getName());

            TypeContext typeContext = new TypeContext(classNode);
            typeContext.getMethodMap().putAll(defaultObjectMethods);

            if (currentClassAttributes != null) {
                typeContext.getAttributeMap().putAll(currentClassAttributes);

            }

            if (currentClassMethods != null) {
                typeContext.getMethodMap().putAll(currentClassMethods);
            }

            visitClassHierarchy(classNode, typeContext);
        }
        return null;
    }


    private void visitClassHierarchy(ClassNode classNode, TypeContext context) {

        visit(classNode, context); // Visit current class, utilising the visitor pattern

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {

            Map<Symbol, MethodNode> defaultObjectMethods = Semant.classTable.getClassMethodsMap().get(TreeConstants.Object_);
            Map<Symbol, AttributeNode> currentClassAttributes = Semant.classTable.getClassAttributesMap().get(child.getName());
            Map<Symbol, MethodNode> currentClassMethods = Semant.classTable.getClassMethodsMap().get(child.getName());

            TypeContext typeContext = new TypeContext(child);
            typeContext.getMethodMap().putAll(defaultObjectMethods);

            if (currentClassAttributes != null) {
                typeContext.getAttributeMap().putAll(currentClassAttributes);
            }

            if (currentClassMethods != null) {
                typeContext.getMethodMap().putAll(currentClassMethods);
            }

            visitClassHierarchy(child, typeContext);
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
        Symbol exprType = visit(node.getInit(), context);
        if (!Semant.classTable.isSubType(exprType, idType)) {
            Utilities.semantError(context.getCurrentClass()).println("Inferred type " +
                    exprType + " of initialization of attribute " + node.getName()
                    + " does not conform to declared type " + idType + ".");
            return TreeConstants.Object_;

        }
        return node.getType_decl();
    }

    @Override
    public Symbol visit(NoExpressionNode node, TypeContext context) {
        node.setType(TreeConstants.No_type);
        return TreeConstants.No_type;
    }

    @Override
    public Symbol visit(MethodNode node, TypeContext context) {

        Semant.symTable.enterScope();

        for (FormalNode f : node.getFormals()) {
            Semant.symTable.addId(f.getName(), f.getType_decl());
        }

        Symbol bodyType = visit(node.getExpr(), context);

        if (!Semant.classTable.isSubType(bodyType, node.getReturn_type(), context.getCurrentClass().getName())) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Inferred return type " + bodyType + " of method " + node.getName()
                            + " does not conform to declared return type " + node.getReturn_type() + ".");
            return TreeConstants.Object_;
        }

        Semant.symTable.exitScope();
        return bodyType;
    }

    @Override
    public Symbol visit(DispatchNode node, TypeContext context) {

        Symbol exprType = visit(node.getExpr(), context);
        Map<Symbol, MethodNode> classMethods;

        if (exprType == TreeConstants.SELF_TYPE) {
            classMethods = Semant.classTable.getClassMethodsMap().get(context.getCurrentClass().getName());
        } else {
            classMethods = Semant.classTable.getClassMethodsMap().get(exprType);
        }

        if (!classMethods.containsKey(node.getName())) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Dispatch to undefined method " + node.getName() + ".");
            return TreeConstants.Object_;
        }

        MethodNode method = classMethods.get(node.getName());
        List<FormalNode> formals = classMethods.get(node.getName()).getFormals();
        List<ExpressionNode> actuals = node.getActuals();

        if (formals.size() != actuals.size()) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Method " + node.getName() + " called with wrong number of arguments. " +
                            "Expected: " + formals.size() + ", got: " + actuals.size());
            node.setType(TreeConstants.Object_);
            return node.getType();
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
                return node.getType();
            }
        }

        Symbol returnType = method.getReturn_type();

        if (returnType == TreeConstants.SELF_TYPE) {
            node.setType(exprType);
        } else {
            node.setType(returnType);
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
            node.setType(TreeConstants.Object_);
            return node.getType();
        }

        node.setType(bodyType);

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
            node.setType(TreeConstants.Object_);
            return node.getType();
        }
        node.setType(exprType);
        return node.getType();
    }

    public Symbol visit(CondNode node, TypeContext context) {
        visit(node.getCond(), context);
        Symbol type = Semant.classTable.getLeastUpperBound(
                visit(node.getThenExpr(), context),
                visit(node.getElseExpr(), context));
        node.setType(type);
        return type;
    }

    public Symbol visit(LoopNode node, TypeContext context) {
        Symbol condType = visit(node.getCond(), context);
        if (condType != TreeConstants.Bool) {
            Utilities.semantError(context.getCurrentClass()).println("Loop condition does not have type Bool.");
        }
        visit(node.getBody(), context);
        node.setType(TreeConstants.Object_);
        return node.getType();
    }

    public Symbol visit(BlockNode node, TypeContext context) {
        List<ExpressionNode> expressions = node.getExprs();
        int length = expressions.size();

        for (int i = 0; i < length - 1; i++) {
            visit(expressions.get(i), context);
        }

        Symbol type = visit(expressions.get(length - 1), context);
        node.setType(type);
        return type;
    }


    public Symbol visit(CaseNode node, TypeContext context) {
        return node.getType();
    }

    public Symbol visit(NewNode node, TypeContext context) {
        node.setType(node.getType_name());
        return node.getType();
    }

    public Symbol visit(CompNode node, TypeContext context) {
        node.setType(visit(node.getE1(), context));
        return node.getType();
    }

    public Symbol visit(EqNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if ((t1 == TreeConstants.Int || t1 == TreeConstants.Str || t1 == TreeConstants.Bool)
                || (t2 == TreeConstants.Int || t2 == TreeConstants.Str || t2 == TreeConstants.Bool)) {
            if (t1 != t2) {
                Utilities.semantError(context.getCurrentClass()).println("Illegal comparison with a basic type.");
                node.setType(TreeConstants.Object_);
                return TreeConstants.Object_;
            }
        }
        node.setType(TreeConstants.Bool);
        return node.getType();
    }

    public Symbol visit(LEqNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (TreeConstants.Int != t1 || TreeConstants.Int != t2) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " <= " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Bool);
        }
        return node.getType();
    }

    public Symbol visit(LTNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (TreeConstants.Int != t1 || TreeConstants.Int != t2) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " < " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Bool);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(PlusNode node, TypeContext context) {
        Symbol t1 = visit(node.getE1(), context);
        Symbol t2 = visit(node.getE2(), context);
        if (TreeConstants.Int != t1 || TreeConstants.Int != t2) {
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
        if (TreeConstants.Int != t1 || TreeConstants.Int != t2) {
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
        if (TreeConstants.Int != t1 || TreeConstants.Int != t2) {
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
        if (TreeConstants.Int != t1 || TreeConstants.Int != t2) {
            Utilities.semantError(context.getCurrentClass()).println("non-Int arguments: " + t1 + " / " + t2);
            node.setType(TreeConstants.Object_);
        } else {
            node.setType(TreeConstants.Int);
        }
        return node.getType();
    }

    @Override
    public Symbol visit(ObjectNode node, TypeContext context) {
        if (node.getName() == TreeConstants.self) {
            node.setType(TreeConstants.SELF_TYPE);
            return TreeConstants.SELF_TYPE;
        }

        Symbol idType = Semant.symTable.lookup(node.getName());
        if (idType != null) {
            node.setType(idType);
        } else {
            AttributeNode attr = context.getAttribute(node.getName());
            if (attr != null) {
                node.setType(attr.getType_decl());
            } else {
                node.setType(TreeConstants.Object_);
            }
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
