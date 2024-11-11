import ast.*;
import ast.visitor.BaseVisitor;

public class ScopeCheckingVisitor extends BaseVisitor<Object, Object> {
    private SymbolTable<Symbol> symTable;

    public ScopeCheckingVisitor(SymbolTable<Symbol> symtable) {
        this.symTable = symtable;
    }

    @Override
    public Object visit(ProgramNode node, Object context) {
        symTable.enterScope();

        for (ClassNode classNode : node.getClasses()) {
            classNode.accept(this, context);
        }

        return null;
    }

    @Override
    public Object visit(ClassNode node, Object context) {
        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }
        return null;
    }

    @Override
    public Object visit(MethodNode node, Object context) {
        Symbol methodKey = StringTable.idtable.addString(node.getName().toString());
        symTable.addId(methodKey, node.getReturn_type());

        symTable.enterScope();

        if (node.getExpr() != null) {
            node.getExpr().accept(this, context);
        }

        symTable.exitScope();
        return null;
    }

    @Override
    public Object visit(ObjectNode node, Object context) {

        symTable.addId(node.getName(), node.getType());
        return null;
    }
}