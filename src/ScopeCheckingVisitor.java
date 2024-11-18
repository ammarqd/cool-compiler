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

public class ScopeCheckingVisitor extends BaseVisitor<Symbol, ScopeContext> {

    @Override
    public Symbol visit(ProgramNode node, ScopeContext context) {
        for (ClassNode classNode : node.getClasses()) {
            visit(classNode, new ScopeContext(classNode));
        }
        return null;
    }

    @Override
    public Symbol visit(ClassNode node, ScopeContext context) {

        Semant.getTable(Semant.Kind.METHOD).enterScope();
        Semant.getTable(Semant.Kind.ATTRIBUTE).enterScope();

        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }

        Semant.getTable(Semant.Kind.ATTRIBUTE).exitScope();
        Semant.getTable(Semant.Kind.METHOD).exitScope();
        return null;
    }

    @Override
    public Symbol visit(MethodNode node, ScopeContext context) {

        if (Semant.getTable(Semant.Kind.METHOD).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Method " + node.getName() + " is multiply defined");
        } else {
            Semant.getTable(Semant.Kind.METHOD).addId(node.getName(), node.getReturn_type());
            Semant.getTable(Semant.Kind.VARIABLE).enterScope();

            for (FormalNode formal : node.getFormals()) {
                visit(formal, context);
            }

            visit(node.getExpr(), context);
            Semant.getTable(Semant.Kind.VARIABLE).exitScope();
        }
        return null;
    }

    @Override
    public Symbol visit(AttributeNode node, ScopeContext context) {
        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                .println("'self' cannot be the name of an attribute");
        }
        if (Semant.getTable(Semant.Kind.ATTRIBUTE).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Attribute " + node.getName() + " is multiply defined");
        } else {
            Semant.getTable(Semant.Kind.ATTRIBUTE).addId(node.getName(), node.getType_decl());
            Semant.getTable(Semant.Kind.VARIABLE).enterScope();

            visit(node.getInit(), context);

            Semant.getTable(Semant.Kind.VARIABLE).exitScope();
        }
        return null;
    }

    @Override
    public Symbol visit(FormalNode node, ScopeContext context) {
        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                .println("'self' cannot be the name of a formal parameter");
        }

        if (Semant.getTable(Semant.Kind.VARIABLE).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Formal parameter " + node.getName() + " is multiply defined");
        } else {
            Semant.getTable(Semant.Kind.VARIABLE).addId(node.getName(), node.getType_decl());
        }
        return null;
    }

    @Override
    public Symbol visit(LetNode node, ScopeContext context) {
        if (node.getIdentifier().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                .println("'self' cannot be bound in a 'let' expression");
        }

        if (Semant.getTable(Semant.Kind.VARIABLE).probe(node.getIdentifier()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Let variable " + node.getIdentifier() + " is multiply defined");
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
    public Symbol visit(ObjectNode node, ScopeContext context) {

        Symbol name = node.getName();
        if (name.equals(TreeConstants.self)) return TreeConstants.SELF_TYPE;

        Symbol varResult = Semant.getTable(Semant.Kind.VARIABLE).lookup(name);
        if (varResult != null) return varResult;

        Symbol attrResult = Semant.getTable(Semant.Kind.ATTRIBUTE).lookup(name);
        if (attrResult != null) return attrResult;

        Symbol methodResult = Semant.getTable(Semant.Kind.METHOD).lookup(name);
        if (methodResult != null) return methodResult;

        Utilities.semantError(context.getCurrentClass().getFilename(), node)
                .println("Undeclared identifier " + name);
        return null;
    }

    @Override
    public Symbol visit(DispatchNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (ExpressionNode actual : node.getActuals()) {
            visit(actual, context);
        }

        if (Semant.getTable(Semant.Kind.METHOD).lookup(node.getName()) == null &&
            !Semant.getClassTable().isBuiltInMethod(node.getName())) {
            Utilities.semantError(context.getCurrentClass())
                .println("Undeclared method " + node.getName());
        }

        return null;
    }

    @Override
    public Symbol visit(StaticDispatchNode node, ScopeContext context) {
        visit(node.getExpr(), context);

        for (ExpressionNode actual : node.getActuals()) {
            visit(actual, context);
        }

        if (Semant.getTable(Semant.Kind.METHOD).lookup(node.getName()) == null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Undeclared method " + node.getName());
        }

        return null;
    }

    @Override
    public Symbol visit(BlockNode node, ScopeContext context) {
        for (ExpressionNode expr : node.getExprs()) {
            visit(expr, context);
        }
        return null;
    }

    @Override
    public Symbol visit(CaseNode node, ScopeContext context) {
        visit(node.getExpr(), context);

        for (BranchNode branch : node.getCases()) {
            if (branch.getName().equals(TreeConstants.self)) {
                Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be bound in a 'case' branch");
            }

            Semant.getTable(Semant.Kind.VARIABLE).enterScope();
            Semant.getTable(Semant.Kind.VARIABLE).addId(branch.getName(), branch.getType_decl());
            visit(branch.getExpr(), context);
            Semant.getTable(Semant.Kind.VARIABLE).exitScope();
        }
        return null;
    }

    @Override
    public Symbol visit(AssignNode node, ScopeContext context) {
        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                .println("Cannot assign to 'self'");
        }

        visit(node.getExpr(), context);
        return null;
    }

}
