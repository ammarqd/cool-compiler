import ast.*;
import ast.visitor.BaseVisitor;
import java.util.HashMap;
import java.util.Map;

enum Kind {
    CLASS,
    METHOD,
    ATTRIBUTE,
    VARIABLE
}

class ScopeContext {
    private final ClassNode currentClass;
    private FeatureNode currentFeature;  // Could be method or attribute

    public ScopeContext(ClassNode currentClass) {
        this.currentClass = currentClass;
        this.currentFeature = null;
    }

    public void setCurrentFeature(FeatureNode feature) {
        this.currentFeature = feature;
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public FeatureNode getCurrentFeature() {
        return currentFeature;
    }
}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {
    private final Map<Kind, SymbolTable<Symbol>> tables;

    public ScopeCheckingVisitor() {
        tables = new HashMap<>();
        for (Kind k : Kind.values()) {
            tables.put(k, new SymbolTable<>());
        }
    }

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        tables.get(Kind.CLASS).enterScope();
        for (ClassNode classNode : node.getClasses()) {
            classNode.accept(this, new ScopeContext(classNode));
        }
        return null;
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {
        tables.get(Kind.CLASS).addId(node.getName(), node.getName());
        tables.get(Kind.METHOD).enterScope();
        tables.get(Kind.ATTRIBUTE).enterScope();

        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }
        return null;
    }

    @Override
    public Void visit(MethodNode node, ScopeContext context) {
        context.setCurrentFeature(node);
        
        if (tables.get(Kind.METHOD).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Method " + node.getName() + " is multiply defined");
        } else {
            tables.get(Kind.METHOD).addId(node.getName(), node.getReturn_type());
        }
        
        tables.get(Kind.VARIABLE).enterScope();
        // Handle formals (parameters)
        for (FormalNode formal : node.getFormals()) {
            formal.accept(this, context);
        }
        
        if (node.getExpr() != null) {
            node.getExpr().accept(this, context);
        }
        tables.get(Kind.VARIABLE).exitScope();
        return null;
    }

    @Override
    public Void visit(AttributeNode node, ScopeContext context) {
        context.setCurrentFeature(node);
        
        if (tables.get(Kind.ATTRIBUTE).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Attribute " + node.getName() + " is multiply defined");
        } else {
            tables.get(Kind.ATTRIBUTE).addId(node.getName(), node.getType_decl());
        }

        if (node.getInit() != null) {
            node.getInit().accept(this, context);
        }
        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {
        if (tables.get(Kind.VARIABLE).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                .println("Formal parameter " + node.getName() + " is multiply defined");
        } else {
            tables.get(Kind.VARIABLE).addId(node.getName(), node.getType_decl());
        }
        return null;
    }
}