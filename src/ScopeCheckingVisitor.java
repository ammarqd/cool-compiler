import ast.*;
import ast.visitor.BaseVisitor;

class ScopeContext {
    private final ClassNode currentClass;

    public ScopeContext(ClassNode currentClass) {
        this.currentClass = currentClass;
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }
}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        if (Semant.getClassTable().getClass(TreeConstants.Main) == null) {
            Utilities.semantError().println("Class Main is not defined");
        }

        for (ClassNode classNode : node.getClasses()) {
            visit(classNode, new ScopeContext(classNode));
        }

        return null;
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {
        Semant.getTable(Semant.Kind.METHOD).enterScope();
        Semant.getTable(Semant.Kind.ATTRIBUTE).enterScope();

        // First pass: Register all methods and attributes
        registerFeatures(node, context);

        if (node.getName().equals(TreeConstants.Main) &&
                Semant.getTable(Semant.Kind.METHOD).probe(TreeConstants.main_meth) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("No 'main' method in class Main.");
        }

        // Second pass: Check implementations
        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }

        Semant.getTable(Semant.Kind.ATTRIBUTE).exitScope();
        Semant.getTable(Semant.Kind.METHOD).exitScope();
        return null;
    }

    private void registerFeatures(ClassNode node, ScopeContext context) {
        for (FeatureNode feature : node.getFeatures()) {
            if (feature instanceof MethodNode method) {
                if (Semant.getTable(Semant.Kind.METHOD).probe(method.getName()) != null) {
                    Utilities.semantError(context.getCurrentClass())
                            .println("Method " + method.getName() + " is multiply defined.");
                } else {
                    Semant.getTable(Semant.Kind.METHOD).addId(method.getName(), method.getReturn_type());
                }
            }
            else if (feature instanceof AttributeNode attribute) {
                if (Semant.getTable(Semant.Kind.ATTRIBUTE).probe(attribute.getName()) != null) {
                    Utilities.semantError(context.getCurrentClass())
                            .println("Attribute " + attribute.getName() + " is multiply defined.");
                } else {
                    Semant.getTable(Semant.Kind.ATTRIBUTE).addId(attribute.getName(), attribute.getType_decl());
                }
            }
        }
    }

    @Override
    public Void visit(MethodNode node, ScopeContext context) {

        Semant.getTable(Semant.Kind.VARIABLE).enterScope();

        for (FormalNode formal : node.getFormals()) {
            visit(formal, context);
        }

        visit(node.getExpr(), context);
        Semant.getTable(Semant.Kind.VARIABLE).exitScope();

        return null;
    }

    @Override
    public Void visit(AttributeNode node, ScopeContext context) {
        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute.");
        }

        Semant.getTable(Semant.Kind.ATTRIBUTE).addId(node.getName(), node.getType_decl());
        Semant.getTable(Semant.Kind.VARIABLE).enterScope();

        visit(node.getInit(), context);

        Semant.getTable(Semant.Kind.VARIABLE).exitScope();

        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {
        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of a formal parameter.");
        }

        if (Semant.getTable(Semant.Kind.VARIABLE).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Formal parameter " + node.getName() + " is multiply defined.");
        } else {
            Semant.getTable(Semant.Kind.VARIABLE).addId(node.getName(), node.getType_decl());
        }
        return null;
    }

    @Override
    public Void visit(LetNode node, ScopeContext context) {
        if (node.getIdentifier().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be bound in a 'let' expression.");
        }

        if (Semant.getTable(Semant.Kind.VARIABLE).probe(node.getIdentifier()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Let variable " + node.getIdentifier() + " is multiply defined.");
        } else {
            Semant.getTable(Semant.Kind.VARIABLE).enterScope();
            Semant.getTable(Semant.Kind.VARIABLE).addId(node.getIdentifier(), node.getType_decl());

            visit(node.getInit(), context);
            visit(node.getBody(), context);

            Semant.getTable(Semant.Kind.VARIABLE).exitScope();
        }
        return null;
    }

    @Override
    public Void visit(ObjectNode node, ScopeContext context) {
        if (Semant.getTable(Semant.Kind.VARIABLE).lookup(node.getName()) == null &&
                Semant.getTable(Semant.Kind.ATTRIBUTE).lookup(node.getName()) == null) {
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

        if (Semant.getTable(Semant.Kind.METHOD).lookup(node.getName()) == null &&
                !Semant.getClassTable().isBuiltInMethod(node.getName())) {
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

        if (Semant.getTable(Semant.Kind.METHOD).lookup(node.getName()) == null) {
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

            Semant.getTable(Semant.Kind.VARIABLE).enterScope();
            Semant.getTable(Semant.Kind.VARIABLE).addId(branch.getName(), branch.getType_decl());
            visit(branch.getExpr(), context);
            Semant.getTable(Semant.Kind.VARIABLE).exitScope();
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
