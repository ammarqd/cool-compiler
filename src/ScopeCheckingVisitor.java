import ast.*;
import ast.visitor.BaseVisitor;

import java.util.*;

class ScopeContext {
    private final ClassNode currentClass;
    private final Map<Symbol, MethodNode> methodMap;
    private final Map<Symbol, AttributeNode> attributeMap;

    public ScopeContext(ClassNode currentClass) {
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

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        if (!Semant.classTable.isValidType(TreeConstants.Main)) {
            Utilities.semantError().println("Class Main is not defined.");
        }

        ArrayList<ClassNode> objectNode = Semant.classTable.getClassMap().get(TreeConstants.No_class);
        ArrayList<ClassNode> objectClasses = Semant.classTable.getClassMap().get(TreeConstants.Object_);

        // Skip last 3 classes (String, Int, Bool), since they disallow inheritance
        for (int i = objectClasses.size() - 1; i >= 3; i--) {
            ScopeContext rootContext = new ScopeContext(objectClasses.get(i));

            // Add the default Object methods to methodMap
            for (FeatureNode feature : objectNode.get(0).getFeatures()) {
                rootContext.getMethodMap().put(((MethodNode)feature).getName(), (MethodNode)feature);
            }

            visitClassHierarchy(objectClasses.get(i), rootContext);
        }

        return null;
    }


    private void visitClassHierarchy(ClassNode classNode, ScopeContext parentContext) {
        ScopeContext context = new ScopeContext(classNode);

        if (parentContext != null) {
            context.getMethodMap().putAll(parentContext.getMethodMap());
            context.getAttributeMap().putAll(parentContext.getAttributeMap());
        }

        for (FeatureNode feature : classNode.getFeatures()) {
            if (feature instanceof MethodNode method) {
                if (!context.getMethodMap().containsKey(method.getName())) {
                    context.addMethod(method.getName(), method);
                } else {
                    MethodNode parentMethod = context.getMethod(method.getName());
                    if (parentMethod.getReturn_type() != method.getReturn_type()) {
                        Utilities.semantError(classNode).println("In redefined method " + method.getName()
                                + ", return type " + method.getReturn_type() + " is different from original return type "
                                + parentMethod.getReturn_type() + ".");
                    } else if (method.getFormals().size() != parentMethod.getFormals().size()) {
                        Utilities.semantError(classNode).println("Incompatible number of formal parameters in redefined method "
                                + method.getName());
                    } else {
                        context.addMethod(method.getName(), method);
                    }
                }
            } else if (feature instanceof AttributeNode attribute) {
                if (!context.getAttributeMap().containsKey(attribute.getName())) {
                    context.addAttribute(attribute.getName(), attribute);
                } else {
                    Utilities.semantError(classNode).println("Attribute " + attribute.getName()
                            + " is an attribute of an inherited class.");
                }
            }
        }

        visit(classNode, context); // Visit current class, utilising the visitor pattern

        ArrayList<ClassNode> children = Semant.classTable.getClassMap().get(classNode.getName());
        for (ClassNode child : children) {
            visitClassHierarchy(child, context);
        }
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.Main) &&
                context.getMethod(TreeConstants.main_meth) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("No 'main' method in class Main.");
        }

        Semant.symTable.enterScope();


        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }

        Semant.symTable.exitScope();

        return null;
    }

    @Override
    public Void visit(MethodNode node, ScopeContext context) {

        Semant.symTable.enterScope();

        for (FormalNode formal : node.getFormals()) {
            visit(formal, context);
        }

        visit(node.getExpr(), context);

        Semant.symTable.exitScope();

        return null;
    }

    @Override
    public Void visit(AttributeNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute.");
        }

        context.addAttribute(node.getName(), node);

        visit(node.getInit(), context);

        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of a formal parameter.");
        }

        if (Semant.symTable.probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Formal parameter " + node.getName() + " is multiply defined.");
        } else {
            Semant.symTable.addId(node.getName(), node.getType_decl());
        }
        return null;
    }

    @Override
    public Void visit(LetNode node, ScopeContext context) {

        if (node.getIdentifier().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be bound in a 'let' expression.");
        }

        Semant.symTable.enterScope();
        Semant.symTable.addId(node.getIdentifier(), node.getType_decl());

        visit(node.getInit(), context);
        visit(node.getBody(), context);

        Semant.symTable.exitScope();

        return null;
    }


    @Override
    public Void visit(ObjectNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.self) {
            return null;
        }

        if (Semant.symTable.lookup(node.getName()) == null && context.getAttribute(node.getName()) == null) {
            Utilities.semantError(context.getCurrentClass().getFilename(), node)
                    .println("Undeclared identifier " + node.getName() + ".");
        }
        return null;
    }

    @Override
    public Void visit(DispatchNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (ExpressionNode actual : node.getActuals()) {
            visit(actual, context);
        }

        if (context.getMethod(node.getName()) == null
                && !Semant.classTable.isBuiltInMethod(node.getName())) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Dispatch to undefined method " + node.getName() + ".");
        }

        return null;
    }

    @Override
    public Void visit(StaticDispatchNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (ExpressionNode actual : node.getActuals()) {
            visit(actual, context);
        }

        if (context.getMethod(node.getName()) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Static dispatch to undefined method " + node.getName() + ".");
        }

        return null;
    }

    @Override
    public Void visit(CaseNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (BranchNode branch : node.getCases()) {
            if (branch.getName().equals(TreeConstants.self)) {
                Utilities.semantError(context.getCurrentClass())
                        .println("'self' cannot be bound in a 'case' branch.");
            }

            Semant.symTable.enterScope();
            Semant.symTable.addId(branch.getName(), branch.getType_decl());
            visit(branch.getExpr(), context);
            Semant.symTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visit(AssignNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Cannot assign to 'self'.");
        }

        visit(node.getExpr(), context);
        return null;
    }

}
