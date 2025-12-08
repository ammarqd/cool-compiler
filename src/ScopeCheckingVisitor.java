import ast.*;
import ast.visitor.BaseVisitor;
import java.util.*;

class ScopeContext {
    private final ClassNode currentClass;
    private final ClassTable classTable = Semant.classTable;
    private final Map<Symbol, MethodNode> methodsMap;
    private final Map<Symbol, AttributeNode> attributesMap;

    public ScopeContext(ClassNode currentClass) {
        this.currentClass = currentClass;
        this.methodsMap = new HashMap<>(classTable.getClassMethodsMap().get(currentClass.getName()));
        this.attributesMap = new HashMap<>(classTable.getClassAttributesMap().get(currentClass.getName()));
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public AttributeNode getAttribute(Symbol name) {
        return attributesMap.get(name);
    }

    public MethodNode getMethod(Symbol name) {
        return methodsMap.get(name);
    }
}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        if (!Semant.classTable.isTypeDefined(TreeConstants.Main)) {
            Utilities.semantError().println("Class Main is not defined.");
        }

        ArrayList<ClassNode> objectClasses = Semant.classTable.getInheritanceMap().get(TreeConstants.Object_);

        // Traverse the full inheritance hierarchy,
        for (int i = objectClasses.size() - 1; i >= 0; i--) {
            visitInheritanceHierarchy(objectClasses.get(i));
        }

        return null;
    }

    private void visitInheritanceHierarchy(ClassNode classNode) {
        ScopeContext context = new ScopeContext(classNode);

        visit(classNode, context); // Visit current class, utilising the visitor pattern, and DFS traversal

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {
            visitInheritanceHierarchy(child);
        }
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.Main
                && context.getMethod(TreeConstants.main_meth) == null) {
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

        if (node.getName() == TreeConstants.self) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute.");
        }

        visit(node.getInit(), context);
        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.self) {
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

        if (node.getIdentifier() == TreeConstants.self) {
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
    public Void visit(CaseNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (BranchNode branch : node.getCases()) {
            if (branch.getName() == TreeConstants.self) {
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

        if (node.getName() == TreeConstants.self) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Cannot assign to 'self'.");
        }

        visit(node.getExpr(), context);
        return null;
    }

}
