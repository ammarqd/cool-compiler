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

        return null;
    }
}
