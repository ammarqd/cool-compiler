import ast.*;
import ast.visitor.BaseVisitor;
import java.util.HashMap;
import java.util.Map;

class ScopeContext {
    private final ClassNode currentClass;
    private final Map<Symbol, MethodNode> methodMap;
    private final ClassTable classTable;

    public ScopeContext(ClassNode currentClass, ClassTable classTable) {
        this.currentClass = currentClass;
        this.classTable = classTable;
        this.methodMap = new HashMap<>();
        registerInheritedMethods(currentClass);
        registerMethods();
    }

    private void registerInheritedMethods(ClassNode node) {
        ClassNode current = classTable.getClass(node.getParent());

        while (current != null) {
            for (FeatureNode feature : current.getFeatures()) {
                if (feature instanceof MethodNode method) {
                    methodMap.put(method.getName(), method);
                }
            }
            current = classTable.getClass(current.getParent());
        }

    }

    private void registerMethods() {
        for (FeatureNode feature : currentClass.getFeatures()) {
            if (feature instanceof MethodNode method) {
                if (methodMap.get(method.getName()) != null) {
                    Utilities.semantError(currentClass)
                            .println("Method " + method.getName() + " is multiply defined.");
                } else {
                    methodMap.put(method.getName(), method);
                }
            }
        }
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public ClassTable getClassTable() {
        return classTable;
    }

    public MethodNode getMethod(Symbol name) {
        return methodMap.get(name);
    }

}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        if (Semant.getClassTable().getClass(TreeConstants.Main) == null) {
            Utilities.semantError().println("Class Main is not defined");
        }

        for (ClassNode classNode : node.getClasses()) {
            visit(classNode, new ScopeContext(classNode, Semant.getClassTable()));
        }

        return null;
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.Main) &&
                context.getMethod(TreeConstants.main_meth) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("No 'main' method in class Main.");
        }

        Semant.getTable().enterScope();

        // Second pass: Check implementations
        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }

        Semant.getTable().exitScope();

        return null;
    }

    @Override
    public Void visit(MethodNode node, ScopeContext context) {

        Semant.getTable().enterScope();

        for (FormalNode formal : node.getFormals()) {
            visit(formal, context);
        }

        visit(node.getExpr(), context);

        Semant.getTable().exitScope();

        return null;
    }

    @Override
    public Void visit(AttributeNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute.");
        }

        Semant.getTable().addId(node.getName(), node.getType_decl());

        visit(node.getInit(), context);

        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of a formal parameter.");
        }

        if (Semant.getTable().probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Formal parameter " + node.getName() + " is multiply defined.");
        } else {
            Semant.getTable().addId(node.getName(), node.getType_decl());
        }
        return null;
    }

    @Override
    public Void visit(LetNode node, ScopeContext context) {

        if (node.getIdentifier().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be bound in a 'let' expression.");
        }

        if (Semant.getTable().probe(node.getIdentifier()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Let variable " + node.getIdentifier() + " is multiply defined.");
        } else {
            Semant.getTable().enterScope();
            Semant.getTable().addId(node.getIdentifier(), node.getType_decl());

            visit(node.getInit(), context);
            visit(node.getBody(), context);

            Semant.getTable().exitScope();
        }
        return null;
    }


    @Override
    public Void visit(ObjectNode node, ScopeContext context) {

        if (Semant.getTable().lookup(node.getName()) == null &&
                Semant.getTable().lookup(node.getName()) == null) {
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
            && !Semant.getClassTable().isBuiltInMethod(node.getName())) {
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
    public Void visit(BlockNode node, ScopeContext context) {

        for (ExpressionNode expr : node.getExprs()) {
            visit(expr, context);
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

            Semant.getTable().enterScope();
            Semant.getTable().addId(branch.getName(), branch.getType_decl());
            visit(branch.getExpr(), context);
            Semant.getTable().exitScope();
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
