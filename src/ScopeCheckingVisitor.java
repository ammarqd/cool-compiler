import ast.*;
import ast.visitor.BaseVisitor;

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
    
    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        Semant.getTable(Semant.Kind.CLASS).enterScope();
        
        for (ClassNode classNode : node.getClasses()) {
            classNode.accept(this, new ScopeContext(classNode));
        }
        
        Semant.getTable(Semant.Kind.CLASS).exitScope();
        return null;
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {
        Semant.getTable(Semant.Kind.METHOD).enterScope();
        Semant.getTable(Semant.Kind.ATTRIBUTE).enterScope();
        Semant.getTable(Semant.Kind.CLASS).addId(node.getName(), node.getName());

        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }
        
        Semant.getTable(Semant.Kind.METHOD).exitScope();
        Semant.getTable(Semant.Kind.ATTRIBUTE).exitScope();
        return null;
    }

    @Override
    public Void visit(MethodNode node, ScopeContext context) {
        context.setCurrentFeature(node);

        if (Semant.getTable(Semant.Kind.METHOD).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Method " + node.getName() + " is multiply defined");
        } else {
            Semant.getTable(Semant.Kind.METHOD).addId(node.getName(), node.getReturn_type());
            
            Semant.getTable(Semant.Kind.VARIABLE).enterScope();
            
            for (FormalNode formal : node.getFormals()) {
                formal.accept(this, context);
            }
            
            if (node.getExpr() != null) {
                node.getExpr().accept(this, context);
            }
            
            Semant.getTable(Semant.Kind.VARIABLE).exitScope();
        }
        return null;
    }

    @Override
    public Void visit(AttributeNode node, ScopeContext context) {
        if (Semant.getTable(Semant.Kind.ATTRIBUTE).probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Attribute " + node.getName() + " is multiply defined");
        } else {
            Semant.getTable(Semant.Kind.ATTRIBUTE).addId(node.getName(), node.getType_decl());
        }
        
        if (node.getInit() != null) {
            node.getInit().accept(this, context);
        }
        
        return null;
    }
}